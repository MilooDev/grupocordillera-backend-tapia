# Usamos una imagen ligera de Java 21
FROM eclipse-temurin:21-jdk-alpine

# Definimos la carpeta de trabajo
WORKDIR /app

# Copiamos el archivo JAR generado al contenedor
COPY target/*.jar app.jar

# Comando para arrancar el microservicio
ENTRYPOINT ["java", "-jar", "app.jar"]