// ============================================================================
// Jenkinsfile - Selenium + Cucumber + JUnit + Allure (Docker + Jenkins)
// - Ejecuta tests SIEMPRE en headless (Chrome y Firefox) de forma SECUENCIAL.
// - Usa Maven y JDK configurados como herramientas de Jenkins.
// - Publica Allure via plugin de Jenkins leyendo "allure-results" del workspace.
// - Mantiene solo las últimas 5 builds (logs + artefactos).
// - NO depende del contenedor allure-reports (puerto 4040) en Jenkins.
// ============================================================================

pipeline {
    // Agente donde se ejecuta el pipeline (tu contenedor Jenkins)
    agent any

    // Herramientas gestionadas por Jenkins (nombres EXACTOS de tu configuración)
    tools {
        maven 'maven-3.9.11'
        jdk   'jdk-17'
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
        // Si Jenkins está en contenedor separado del Grid, lo normal es usar:
        // http://host.docker.internal:4444/wd/hub
        // Si más adelante vemos error de conexión, ajustamos este valor.
        SELENIUM_GRID_URL = 'http://host.docker.internal:4444/wd/hub'

        // Directorio donde Maven genera los resultados de Allure (POM)
        ALLURE_RESULTS = 'allure-results'

        // Flags de Maven:
        // - Forzamos headless SIEMPRE en Jenkins
        // - Saltamos integración Docker/Allure del POM (no toca jdk-maven ni allure-reports)
        MAVEN_FLAGS = '-Dskip.docker.allure=true -Dheadless=true'
    }

    stages {

        // 1) Checkout del código fuente (rama feature/ci-jenkins)
        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        // 2) Limpieza controlada de resultados Allure en el workspace de Jenkins
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

        // 3) Ejecutar tests en Chrome y luego en Firefox (SIEMPRE headless)
        stage('Run tests (Chrome & Firefox - headless)') {
            steps {
                script {
                    def browsers   = ['chrome', 'firefox']    // orden SECUENCIAL
                    def gridUrl    = env.SELENIUM_GRID_URL
                    def mavenFlags = env.MAVEN_FLAGS

                    def mvnBase = "mvn test ${mavenFlags}"

                    for (browser in browsers) {
                        echo 'Running tests inside jdk-maven'
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

        // 4) Publicar reporte Allure usando el plugin de Jenkins
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

    // Post-actions: archivamos logs y screenshots aunque la build falle
    post {
        always {
            echo 'Archiving logs and screenshots from target/...'
            archiveArtifacts artifacts: 'target/**/*.log, target/screenshots/**/*',
                             onlyIfSuccessful: false,
                             allowEmptyArchive: true
        }
    }
}
