Selenium + Cucumber + JUnit Automation Framework (Dockerized)

Framework de automatización Web con Java 17, Selenium, Cucumber, JUnit 5, Allure Reports y ejecución en Docker / Jenkins.
Incluye captura automática de screenshots solo en caso de fallo, administrada por la clase ScreenshotUtil.

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
┣ src/
┃ ┣ main/java/...      # Utilidades base
┃ ┗ test/java/...      # Hooks, Steps, ScreenshotUtil
┣ features/            # Escenarios Cucumber
┣ target/              # Compilación + evidencia (ci-artifacts)
┣ allure-results/      # Resultados Allure (ignorar en Git)
┣ pom.xml
┣ Dockerfile-jdk-maven
┣ Dockerfile-allure-reports
┣ docker-compose.yaml
┗ README.md

---
##  Ejecución Local

### Clonar el repositorio

```bash
git clone https://github.com/JPLEAL79/selenium-cucumber-Junit.git
cd selenium-cucumber-Junit

 Ejecutar con Docker (entorno completo)
  Levantar todos los contenedores
  
- docker-compose down -v
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

- mvn test -Dbrowser=chrome  -DseleniumGridUrl=http://localhost:4444/wd/hub
- mvn test -Dbrowser=firefox -DseleniumGridUrl=http://localhost:4444/wd/hub

Desde el contenedor jdk-maven (Docker)

- docker exec -it jdk-maven sh -c "mvn test -Dskip.docker.allure=true -Dbrowser=chrome -DseleniumGridUrl=http://selenium-hub:4444/wd/hub"
- docker exec -it jdk-maven sh -c "mvn test -Dskip.docker.allure=true -Dbrowser=firefox -DseleniumGridUrl=http://selenium-hub:4444/wd/hub"

Actualizar el informe Allure (si es necesario Docker)
- docker exec -it allure-reports sh -c "rm -rf /app/allure-report/* && cp -r /allure-share/* /app/allure-results/ && allure generate /app/allure-results -o /app/allure-report --clean"

Reiniciar todo el entorno
- docker-compose down -v
- docker system prune -f
- docker-compose up --build

Esto:
-Elimina contenedores
-Elimina volúmenes
-Limpia red
-Reconstruye todo desde cero


Visualizar el reporte Allure

- Después de ejecutar las pruebas, el sistema:
- Limpia automáticamente allure-results/
- Copia los resultados nuevos al share /allure-share
- Regenera el reporte en el contenedor allure-reports

 Abre el navegador:
 http://localhost:4040

