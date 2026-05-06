import pika
import json
import pymongo
from datetime import datetime
import sys

# ==========================================
# 1. CONEXIÓN A MONGODB (Para guardar los KPIs)
# ==========================================
try:
    # Por defecto se conectará al localhost, luego en Docker lo cambiaremos
    mongo_client = pymongo.MongoClient("mongodb://localhost:27017/")
    db = mongo_client["gc_analytics_db"]
    kpi_collection = db["kpi_ventas_diarias"]
    print("[MongoDB] ✅ Conectado exitosamente a la base de datos analítica.")
except Exception as e:
    print(f"[MongoDB] ❌ Error conectando a BD: {e}")
    sys.exit(1)

# ==========================================
# 2. LÓGICA DE PROCESAMIENTO (El Consumidor)
# ==========================================
def procesar_evento_venta(ch, method, properties, body):
    try:
        # 1. Desempaquetar el mensaje (viene en bytes, lo pasamos a Diccionario Python)
        evento_venta = json.loads(body.decode('utf-8'))
        
        print("\n" + "="*50)
        print("📥 [RABBITMQ] NUEVA VENTA RECIBIDA")
        print(f"Boleta : {evento_venta.get('numeroBoleta')}")
        print(f"Total  : ${evento_venta.get('total')}")
        print("="*50)

        # 2. Calcular/Actualizar KPIs Estratégicos
        fecha_hoy = datetime.now().strftime("%Y-%m-%d")
        
        # Buscamos si ya existe un registro de KPI para hoy
        kpi_hoy = kpi_collection.find_one({"fecha": fecha_hoy})
        
        if kpi_hoy:
            # Si existe, sumamos el total de esta venta al acumulado diario
            nuevo_total = kpi_hoy["recaudacion_total"] + evento_venta.get('total', 0)
            nueva_cantidad = kpi_hoy["cantidad_ventas"] + 1
            
            kpi_collection.update_one(
                {"_id": kpi_hoy["_id"]},
                {"$set": {
                    "recaudacion_total": nuevo_total, 
                    "cantidad_ventas": nueva_cantidad,
                    "ultima_actualizacion": datetime.now()
                }}
            )
            print(f"📊 [KPI] Indicadores actualizados. Recaudación del día: ${nuevo_total}")
        else:
            # Si es la primera venta del día, creamos el documento base
            nuevo_kpi = {
                "fecha": fecha_hoy,
                "recaudacion_total": evento_venta.get('total', 0),
                "cantidad_ventas": 1,
                "ultima_actualizacion": datetime.now()
            }
            kpi_collection.insert_one(nuevo_kpi)
            print("📊 [KPI] Nuevo registro diario de indicadores creado.")

        # 3. Confirmar a RabbitMQ que el mensaje fue procesado con éxito (ACK)
        ch.basic_ack(delivery_tag=method.delivery_tag)

    except Exception as e:
        print(f"❌ [ERROR] Fallo procesando el mensaje: {e}")
        # En caso de error, rechazamos el mensaje para que vuelva a la cola (opcional)
        # ch.basic_nack(delivery_tag=method.delivery_tag, requeue=True)

# ==========================================
# 3. CONEXIÓN A RABBITMQ (El "Oyente")
# ==========================================
def iniciar_listener():
    try:
        # Conexión al servidor RabbitMQ local
        connection = pika.BlockingConnection(pika.ConnectionParameters(host='localhost'))
        channel = connection.channel()

        # Declarar la misma cola que creamos en Java (durable=True para que no se borre)
        channel.queue_declare(queue='ventas_queue', durable=True)

        # Configurar quién va a procesar los mensajes
        # auto_ack=False significa que Python le avisará a Rabbit cuando termine de guardar en Mongo
        channel.basic_consume(queue='ventas_queue', on_message_callback=procesar_evento_venta, auto_ack=False)

        print('🚀 [GC_KPI_Analytics] Motor analítico iniciado (Python 3.9).')
        print('⏳ Esperando eventos asíncronos de la red retail. Para salir presiona CTRL+C')
        
        # Este método bloquea el script y lo mantiene escuchando para siempre
        channel.start_consuming()

    except pika.exceptions.AMQPConnectionError:
        print("❌ [RabbitMQ] No se pudo conectar al Broker. Asegúrate de que Docker esté corriendo.")
    except KeyboardInterrupt:
        print("\n🛑 Apagando motor analítico...")
        sys.exit(0)

if __name__ == '__main__':
    iniciar_listener()