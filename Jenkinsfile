// ============================================================================
// Jenkinsfile - Selenium + Cucumber + JUnit + Allure (Docker + Jenkins)
// - Ejecuta tests SIEMPRE en headless (Chrome y Firefox) de forma SECUENCIAL.
// - Usa Maven y JDK configurados como herramientas de Jenkins (no docker exec).
// - Publica Allure via plugin de Jenkins leyendo "allure-results" del workspace.
// - Mantiene solo las últimas 5 builds (logs + artefactos).
// - NO modifica ni depende del contenedor allure-reports (puerto 4040).
// ============================================================================

pipeline {
    // Agente donde se ejecuta el pipeline (tu contenedor Jenkins)
    agent any

    // Herramientas gestionadas por Jenkins (deben existir en Global Tool Configuration)
    tools {
        maven 'Maven_3_9_9'   // Nombre del Maven tool en Jenkins
        jdk   'JDK17'         // Nombre del JDK tool en Jenkins
    }

    options {
        timestamps()                               // timestamps en el log
        disableConcurrentBuilds()                  // evita builds solapados
        buildDiscarder(logRotator(                 // conserva máx. 5 builds
            numToKeepStr: '5',
            artifactNumToKeepStr: '5'
        ))
    }

    // Ejecutar todos los días a las 09:00 (hora del Jenkins)
    triggers {
        cron('0 9 * * *')
    }

    environment {
        // IMPORTANTE:
        // Tu selenium-hub está en Docker y expone el puerto 4444 al HOST.
        // Como Jenkins corre en un contenedor distinto, lo más correcto es
        // que llame al host via host.docker.internal:4444
        //
        // De esta forma:
        //   Jenkins -> host.docker.internal:4444 -> (NAT) -> selenium-hub:4444
        //
        // Si en tu caso Jenkins NO está en Docker, puedes cambiarlo a:
        //   http://localhost:4444/wd/hub
        SELENIUM_GRID_URL = 'http://host.docker.internal:4444/wd/hub'

        // Directorio donde Maven genera los resultados de Allure (POM)
        ALLURE_RESULTS = 'allure-results'

        // Flags de Maven:
        // - Forzamos headless SIEMPRE en Jenkins
        // - Saltamos la integración Docker/Allure del POM (no toca jdk-maven ni allure-reports)
        MAVEN_FLAGS = '-Dskip.docker.allure=true -Dheadless=true'
    }

    stages {

        // --------------------------------------------------------------------
        // 1) Checkout del código fuente (rama feature/ci-jenkins)
        // --------------------------------------------------------------------
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        // --------------------------------------------------------------------
        // 2) Limpieza controlada de resultados Allure en el workspace de Jenkins
        //    (NO toca contenedores ni volumenes de Docker)
        // --------------------------------------------------------------------
        stage('Prepare workspace') {
            steps {
                echo 'Cleaning previous Allure results and reports in workspace...'
                sh '''
                    rm -rf "${ALLURE_RESULTS}" || true
                    rm -rf target/allure-report || true
                    mkdir -p "${ALLURE_RESULTS}"
                '''
            }
        }

        // --------------------------------------------------------------------
        // 3) Ejecutar tests en Chrome y luego en Firefox (SIEMPRE headless)
        //    usando Maven del agente Jenkins (no docker exec)
        // --------------------------------------------------------------------
        stage('Run tests (Chrome & Firefox - headless)') {
            steps {
                script {
                    // Navegadores a ejecutar en orden SECUENCIAL
                    def browsers   = ['chrome', 'firefox']
                    def gridUrl    = env.SELENIUM_GRID_URL
                    def mavenFlags = env.MAVEN_FLAGS

                    // Comando base de Maven (para no duplicar lógica)
                    def mvnBase = "mvn test ${mavenFlags}"

                    for (browser in browsers) {
                        echo 'Running tests on Jenkins agent with Maven tool'
                        echo "Running tests on ${browser.capitalize()} (headless)"

                        sh """
                            ${mvnBase} \
                                -Dbrowser=${browser} \
                                -DseleniumGridUrl=${gridUrl}
                        """
                    }
                }
            }
        }

        // --------------------------------------------------------------------
        // 4) Publicar reporte Allure usando el plugin de Jenkins
        //    (no usa puerto 4040 ni contenedor allure-reports)
        // --------------------------------------------------------------------
        stage('Publish Allure report') {
            steps {
                echo "Publishing Allure report from '${env.ALLURE_RESULTS}'"

                allure includeProperties: false,
                       jdk: '',
                       results: [[path: "${env.ALLURE_RESULTS}"]]

                echo 'Publishing Allure report - done'
            }
        }
    }

    // ------------------------------------------------------------------------
    // Post-actions: archivamos logs y screenshots aunque la build falle
    // ------------------------------------------------------------------------
    post {
        always {
            echo 'Archiving logs and screenshots from target/...'
            archiveArtifacts artifacts: 'target/**/*.log, target/screenshots/**/*',
                             onlyIfSuccessful: false,
                             allowEmptyArchive: true
        }
    }
}
