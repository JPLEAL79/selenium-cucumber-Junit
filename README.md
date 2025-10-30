# Selenium + Cucumber + JUnit Automation Framework (Dockerized)

Framework de **automatización de pruebas funcionales Web** desarrollado en **Java** y **Maven**, con soporte para **Allure Reports** y ejecución en **Jenkins** dentro de contenedores **Docker**.  
Diseñado para ser **portable**, **modular** y fácilmente integrable en pipelines **CI/CD**.

---

## Stack Tecnológico

| Componente | Descripción |
|-------------|-------------|
| **Lenguaje** | Java 17 |
| **Framework de pruebas** | JUnit 5 + Cucumber |
| **Automatización Web** | Selenium WebDriver |
| **Build Tool** | Maven |
| **Reporting** | Allure Framework |
| **Contenedores** | Docker + Docker Compose |
| **Integración Continua** | Jenkins Pipeline |

---

## Estructura del Proyecto.

selenium-cucumber-Junit/
┣ 📂 src
┃ ┣ 📂 main/java/... # Clases base y utilidades
┃ ┗ 📂 test/java/... # Step Definitions y Hooks
┣ 📂 features/ # Escenarios Cucumber (.feature)
┣ 📂 target/ # Resultados de compilación
┣ 📂 allure-results/ # Resultados Allure (ignorar en control de versiones)
┣ 📜 pom.xml # Configuración Maven
┣ 📜 Dockerfile-jdk-maven # Imagen base para ejecución
┣ 📜 Dockerfile-allure-reports
┣ 📜 docker-compose.yml # Orquestación de contenedores
┗ 📜 README.md


---

## Ejecución Local

1. **Clonar el repositorio**
   ```bash
   git clone https://github.com/JPLEAL79/selenium-cucumber-Junit.git
   cd selenium-cucumber-Junit

Levantar el entorno con Docker Compose.

-docker-compose up --build

Ejecutar las pruebas
- mvn clean test

-


