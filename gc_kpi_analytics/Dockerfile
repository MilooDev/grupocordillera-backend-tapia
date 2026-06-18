# Usamos una versión oficial y ligera de Python
FROM python:3.9-slim

# Creamos la carpeta de trabajo dentro del contenedor
WORKDIR /app

# Copiamos primero las dependencias y las instalamos
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copiamos el código de nuestro motor
COPY main.py .

# Ejecutamos el script cuando el contenedor se encienda
CMD ["python", "-u", "main.py"]