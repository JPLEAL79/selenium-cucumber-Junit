# Usa una imagen oficial de OpenJDK 21 como base
FROM openjdk:21-jdk-slim

# Instala Maven
RUN apt-get update && \
    apt-get install -y maven && \
    apt-get clean;

# Establece el directorio de trabajo en /app
WORKDIR /app

# Copia el contenido de tu proyecto en el contenedor
COPY . /app

# Exponer el puerto (si fuera necesario, por ejemplo, para un servidor)
# EXPOSE 8080

# Establece el comando por defecto para ejecutar el contenedor (puedes cambiarlo si tienes otro objetivo)
CMD ["bash"]
