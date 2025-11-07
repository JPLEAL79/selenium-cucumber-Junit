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

## Estructura del Proyecto

selenium-cucumber-Junit/
┣ 📂 src
┃ ┣ 📂 main/java/... # Clases base y utilidades
┃ ┗ 📂 test/java/... # Step Definitions y Hooks
┣ 📂 features/ # Escenarios Cucumber (.feature)
┣ 📂 target/ # Resultados de compilación
┣ 📂 allure-results/ # Resultados Allure (ignorar en Git)
┣ 📜 pom.xml # Configuración Maven (JUnit + Cucumber + Allure)
┣ 📜 Dockerfile-jdk-maven # Imagen base para ejecución Maven
┣ 📜 Dockerfile-allure-reports
┣ 📜 docker-compose.yaml # Orquestación de contenedores
┗ 📜 README.md


---

##  Ejecución Local

### 1. Clonar el repositorio

```bash
git clone https://github.com/JPLEAL79/selenium-cucumber-Junit.git
cd selenium-cucumber-Junit


Levantar el entorno con Docker Compose.
- docker-compose up --build

Esto iniciará los contenedores:

- selenium-hub (puerto 4444)
- chrome-node / firefox-node
- jdk-maven (ejecución de pruebas)
- allure-reports (servidor Allure en http://localhost:4040)
- jenkins (si está configurado en tu entorno)

Ejecutar las pruebas
Desde Windows o IntelliJ

No es necesario usar clean.
El proyecto limpia automáticamente los resultados viejos de Allure antes de cada ejecución.

- mvn test -Dbrowser=chrome -DseleniumGridUrl=http://localhost:4444/wd/hub
- mvn test -Dbrowser=firefox -DseleniumGridUrl=http://localhost:4444/wd/hub

Desde el contenedor jdk-maven (Docker)

- docker exec -it jdk-maven sh -c "mvn test -Dbrowser=chrome -DseleniumGridUrl=http://selenium-hub:4444/wd/hub"
- docker exec -it jdk-maven sh -c "mvn test -Dbrowser=firefox -DseleniumGridUrl=http://selenium-hub:4444/wd/hub"

4. Visualizar el reporte Allure

- Después de ejecutar las pruebas, el sistema:
- Limpia automáticamente allure-results/
- Copia los resultados nuevos al share /allure-share
- Regenera el reporte en el contenedor allure-reports

 Abre el navegador:
 http://localhost:4040

