RUN apt-get update && apt-get install -y \
     curl \
     unzip \
     wget \
     && rm -rf /var/lib/apt/lists/*

 # Instalar Allure 2.24.0
 ENV ALLURE_VERSION=2.24.0
 RUN wget https://github.com/allure-framework/allure2/releases/download/${ALLURE_VERSION}/allure-${ALLURE_VERSION}.tgz && \
     tar -zxvf allure-${ALLURE_VERSION}.tgz && \
     mv allure-${ALLURE_VERSION} /opt/allure && \
     ln -s /opt/allure/bin/allure /usr/bin/allure

 # Crear directorio de trabajo
 WORKDIR /usr/src/app

 # Copiar el contenido del proyecto al contenedor
 COPY . .

 # El contenedor iniciará en bash para que lo puedas ejecutar manualmente si quieres
 CMD ["bash"]
