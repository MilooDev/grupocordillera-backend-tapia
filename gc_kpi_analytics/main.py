import pika
import json
import pymongo
import time
import os
import sys
import logging
from datetime import datetime

# ==========================================
# 1. CONFIGURACIÓN CORPORATIVA (Logs y Variables)
# ==========================================
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(message)s',
    handlers=[logging.StreamHandler(sys.stdout)]
)
logger = logging.getLogger("GC_KPI_Analytics")

RABBITMQ_HOST = os.getenv("RABBITMQ_HOST", "localhost")
MONGO_URI = os.getenv("MONGO_URI", "mongodb://localhost:27017/")

# ==========================================
# 2. CONEXIONES RESILIENTES (Tolerancia a fallos)
# ==========================================
def conectar_mongodb():
    intentos = 5
    while intentos > 0:
        try:
            client = pymongo.MongoClient(MONGO_URI, serverSelectionTimeoutMS=5000)
            client.server_info() # Forzamos la conexión para verificar
            db = client["gc_analytics_db"]
            logger.info("✅ Conexión exitosa a MongoDB Analítico.")
            return db["kpi_ventas_diarias"]
        except Exception as e:
            intentos -= 1
            logger.warning(f"⏳ MongoDB no disponible. Reintentando en 5s... ({intentos} intentos restantes)")
            time.sleep(5)
    logger.error("❌ Imposible conectar a MongoDB. Apagando servicio.")
    sys.exit(1)

def conectar_rabbitmq():
    intentos = 5
    while intentos > 0:
        try:
            connection = pika.BlockingConnection(pika.ConnectionParameters(host=RABBITMQ_HOST))
            logger.info("✅ Conexión exitosa a RabbitMQ.")
            return connection
        except pika.exceptions.AMQPConnectionError:
            intentos -= 1
            logger.warning(f"⏳ RabbitMQ no disponible. Reintentando en 5s... ({intentos} intentos restantes)")
            time.sleep(5)
    logger.error("❌ Imposible conectar a RabbitMQ. Apagando servicio.")
    sys.exit(1)

# ==========================================
# 3. LÓGICA DE NEGOCIO (Procesamiento Seguro)
# ==========================================
def procesar_evento_venta(ch, method, properties, body):
    try:
        evento_venta = json.loads(body.decode('utf-8'))
        
        numero_boleta = evento_venta.get('numeroBoleta', 'DESCONOCIDO')
        total_venta = evento_venta.get('total', 0.0)
        
        logger.info(f"📥 Procesando Venta Asíncrona: {numero_boleta} | Monto: ${total_venta}")

        fecha_hoy = datetime.now().strftime("%Y-%m-%d")
        
        # Uso de UPSERT: Operación atómica y eficiente (Si no existe, lo crea. Si existe, lo actualiza)
        kpi_collection.update_one(
            {"fecha": fecha_hoy},
            {
                "$inc": {"recaudacion_total": total_venta, "cantidad_ventas": 1},
                "$set": {"ultima_actualizacion": datetime.now()}
            },
            upsert=True
        )
        
        logger.info("📊 Dashboard de KPIs actualizado exitosamente.")
        
        # Confirmación explícita (Si esto no se llama, RabbitMQ reenvía el mensaje para evitar pérdida de datos)
        ch.basic_ack(delivery_tag=method.delivery_tag)

    except json.JSONDecodeError:
        logger.error("❌ Mensaje corrupto recibido. No es un JSON válido. Descartando...")
        ch.basic_reject(delivery_tag=method.delivery_tag, requeue=False)
    except Exception as e:
        logger.error(f"❌ Error interno procesando venta: {str(e)}")
        # NACK: Le dice a RabbitMQ "Fallé, vuelve a meter el mensaje a la cola para otro intento"
        ch.basic_nack(delivery_tag=method.delivery_tag, requeue=True)

# ==========================================
# 4. INICIALIZACIÓN DEL SERVICIO
# ==========================================
if __name__ == '__main__':
    logger.info("🚀 Iniciando Motor GC_KPI_Analytics (Python)")
    
    # Iniciar BD
    kpi_collection = conectar_mongodb()
    
    # Iniciar Broker
    rabbitmq_conn = conectar_rabbitmq()
    channel = rabbitmq_conn.channel()
    
    # Asegurar que la cola existe
    channel.queue_declare(queue='ventas_queue', durable=True)
    
    # QoS (Quality of Service): Procesa de a 1 mensaje a la vez para no saturar la RAM
    channel.basic_qos(prefetch_count=1)
    
    channel.basic_consume(queue='ventas_queue', on_message_callback=procesar_evento_venta, auto_ack=False)
    
    try:
        logger.info('⏳ Escuchando eventos retail en tiempo real. Presiona CTRL+C para apagar.')
        channel.start_consuming()
    except KeyboardInterrupt:
        logger.info("🛑 Deteniendo consumo de eventos...")
        rabbitmq_conn.close()
        logger.info("✅ Servicio apagado correctamente.")