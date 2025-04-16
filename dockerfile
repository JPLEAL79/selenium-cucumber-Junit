# Usa una imagen oficial de OpenJDK 21 como base
FROM openjdk:21-jdk-slim

# Instala Maven 3.9.9
RUN apt-get update && \
    apt-get install -y wget unzip && \
    wget https://downloads.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz && \
    tar -xvzf apache-maven-3.9.9-bin.tar.gz -C /opt && \
    rm apache-maven-3.9.9-bin.tar.gz

# Establece el directorio de trabajo en /app
WORKDIR /app

# Copia el contenido de tu proyecto en el contenedor
COPY . /app

# Configura Maven
ENV MAVEN_HOME=/opt/apache-maven-3.9.9
ENV PATH=$MAVEN_HOME/bin:$PATH

# Instala Allure
RUN wget https://github.com/allure-framework/allure2/releases/download/2.19.0/allure-2.19.0.zip && \
    unzip allure-2.19.0.zip -d /opt && \
    mv /opt/allure-2.19.0 /opt/allure && \
    rm allure-2.19.0.zip

# Exponer puertos si es necesario (en este caso no se requiere)
# EXPOSE 8080

# Establece el comando por defecto para ejecutar el contenedor
CMD ["bash"]
