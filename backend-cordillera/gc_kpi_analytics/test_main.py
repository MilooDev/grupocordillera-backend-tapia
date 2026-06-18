import json
import pytest
from unittest.mock import MagicMock, patch
import pika
import main

# ==========================================
# CONFIGURACIÓN DE FIXTURES Y ENTORNO
# ==========================================
@pytest.fixture(autouse=True)
def setup_global_mock():
    """Inyecta de manera segura el mock de la colección analítica global"""
    main.kpi_collection = MagicMock()
    yield main.kpi_collection

# ==========================================
# TEST DE CONEXIONES Y RESILIENCIA
# ==========================================
@patch('pymongo.MongoClient')
def test_conectar_mongodb_exitoso(mock_client):
    mock_db = MagicMock()
    mock_client.return_value.__getitem__.return_value = mock_db
    
    resultado = main.conectar_mongodb()
    
    assert resultado == mock_db["kpi_ventas_diarias"]

@patch('time.sleep', return_value=None)
@patch('pymongo.MongoClient')
def test_conectar_mongodb_falla_y_apaga(mock_client, mock_sleep):
    mock_client.side_effect = Exception("Fallo total de BD")
    
    with pytest.raises(SystemExit) as sample_exit:
        main.conectar_mongodb()
        
    assert sample_exit.value.code == 1

@patch('pika.BlockingConnection')
def test_conectar_rabbitmq_exitoso(mock_conn):
    resultado = main.conectar_rabbitmq()
    assert resultado == mock_conn.return_value

@patch('time.sleep', return_value=None)
@patch('pika.BlockingConnection')
def test_conectar_rabbitmq_falla_y_apaga(mock_conn, mock_sleep):
    mock_conn.side_effect = pika.exceptions.AMQPConnectionError("Broker caído")
    
    with pytest.raises(SystemExit) as sample_exit:
        main.conectar_rabbitmq()
        
    assert sample_exit.value.code == 1

# ==========================================
# TEST DE PROCESAMIENTO DE MENSAJES (MOCK CHANNEL)
# ==========================================
def test_procesar_evento_venta_exitoso(setup_global_mock):
    mock_collection = setup_global_mock
    mock_channel = MagicMock()
    mock_method = MagicMock()
    mock_method.delivery_tag = 101
    
    body_json = json.dumps({"numeroBoleta": "BOL-2026", "total": 45000.0}).encode('utf-8')
    
    main.procesar_evento_venta(mock_channel, mock_method, None, body_json)
    
    # Validar que se llamó al Upsert atómico en Mongo
    mock_collection.update_one.assert_called_once()
    # Validar que se envió el ACK de éxito a RabbitMQ
    mock_channel.basic_ack.assert_called_once_with(delivery_tag=101)

def test_procesar_evento_venta_json_corrupto():
    mock_channel = MagicMock()
    mock_method = MagicMock()
    mock_method.delivery_tag = 102
    
    body_corrupto = b"{json_invalido: no_comillas}"
    
    main.procesar_evento_venta(mock_channel, mock_method, None, body_corrupto)
    
    # Validar que se rechazó el mensaje sin volver a encolarlo (False)
    mock_channel.basic_reject.assert_called_once_with(delivery_tag=102, requeue=False)

def test_procesar_evento_venta_error_interno(setup_global_mock):
    mock_collection = setup_global_mock
    # Forzamos una excepción genérica en tiempo de ejecución
    mock_collection.update_one.side_effect = Exception("Fallo de escritura")
    
    mock_channel = MagicMock()
    mock_method = MagicMock()
    mock_method.delivery_tag = 103
    
    body_json = json.dumps({"numeroBoleta": "BOL-999", "total": 10.0}).encode('utf-8')
    
    main.procesar_evento_venta(mock_channel, mock_method, None, body_json)
    
    # Validar que se envió un NACK solicitando reencolar el mensaje (True) para reintento
    mock_channel.basic_nack.assert_called_once_with(delivery_tag=103, requeue=True)