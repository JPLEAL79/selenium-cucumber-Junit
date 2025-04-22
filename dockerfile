# Usar la imagen base de OpenJDK 17 slim
FROM openjdk:17-jdk-slim

# Instalar dependencias necesarias
RUN apt-get update && \
    apt-get install -y wget unzip curl && \
    apt-get clean

# Descargar e instalar Maven 3.9.9
RUN wget https://downloads.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz && \
    tar -xvzf apache-maven-3.9.9-bin.tar.gz -C /opt && \
    rm apache-maven-3.9.9-bin.tar.gz

# Configurar variables de entorno para Maven
ENV MAVEN_HOME=/opt/apache-maven-3.9.9
ENV PATH="${MAVEN_HOME}/bin:${PATH}"

# Descargar e instalar Allure 2.24.0
RUN wget https://github.com/allure-framework/allure2/releases/download/2.24.0/allure-2.24.0.zip && \
    unzip allure-2.24.0.zip -d /opt && \
    mv /opt/allure-2.24.0 /opt/allure && \
    rm allure-2.24.0.zip

# Configurar el PATH de Allure
ENV ALLURE_HOME=/opt/allure
ENV PATH="${ALLURE_HOME}/bin:${PATH}"

# Establecer el directorio de trabajo
WORKDIR /app

# Exponer puerto 8082 para Allure (Jenkins usa 8080)
EXPOSE 8082

# Comando por defecto para ejecutar Allure en puerto 8082
CMD ["allure", "serve", "/app/allure-results", "-p", "8082"]




