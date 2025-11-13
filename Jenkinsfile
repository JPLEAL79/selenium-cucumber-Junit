// ============================================================================
// Jenkinsfile - Selenium + Cucumber + JUnit + Allure (Docker + Jenkins)
// Autor: Juan Pablo Leal
// - Ejecuta tests dentro del contenedor "jdk-maven" SIEMPRE en headless.
// - Publica Allure en Jenkins usando la carpeta "allure-results" del workspace.
// - Jenkins solo conserva las ÚLTIMAS 5 ejecuciones (builds + artefactos).
// ============================================================================

pipeline {
    agent any

    options {
        timestamps()                                    // timestamps en el log
        disableConcurrentBuilds()                       // evita builds solapados
        buildDiscarder(logRotator(                      // conserva máx. 5 builds
            numToKeepStr: '5',
            artifactNumToKeepStr: '5'
        ))
    }

    // Ejecutar todos los días a las 09:00 (hora del Jenkins)
    triggers {
        cron('0 9 * * *')
    }

    environment {
        // URL del Selenium Grid ya existente (NO tocar)
        SELENIUM_GRID_URL = 'http://selenium-hub:4444/wd/hub'

        // Carpeta donde Maven deja los resultados Allure (desde pom.xml)
        ALLURE_RESULTS = 'allure-results'

        // Flags comunes para Maven: no usar Allure Docker y siempre headless
        MAVEN_FLAGS    = '-Dskip.docker.allure=true -Dheadless=true'
    }

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('Prepare workspace') {
            steps {
                echo 'Cleaning previous Allure results and reports in workspace...'

                // Limpieza controlada para no mezclar builds:
                // - Jenkins workspace, NO toca contenedores ni imágenes
                sh '''
                    rm -rf "${ALLURE_RESULTS}" || true
                    rm -rf target/allure-report || true
                    mkdir -p "${ALLURE_RESULTS}"
                '''
            }
        }

        stage('Run tests (Chrome & Firefox - headless)') {
            steps {
                script {
                    // Lista de navegadores a ejecutar de forma SECUENCIAL
                    def browsers = ['chrome', 'firefox']
                    def gridUrl = env.SELENIUM_GRID_URL
                    def mavenFlags = env.MAVEN_FLAGS

                    for (browser in browsers) {
                        echo "Running tests inside jdk-maven"
                        echo "Running tests on ${browser.capitalize()} (headless)"

                        // Ejecuta Maven DENTRO del contenedor jdk-maven
                        // - Usa /workspace (mapeado al repo en el host/Jenkins)
                        // - Usa Selenium Grid http://selenium-hub:4444/wd/hub
                        // - Fuerza headless y desactiva Allure-Docker del pom (skip.docker.allure=true)
                        sh """
                            docker exec jdk-maven sh -lc '
                                cd /workspace && \
                                mvn test ${mavenFlags} \
                                    -Dbrowser=${browser} \
                                    -DseleniumGridUrl=${gridUrl}
                            '
                        """
                    }
                }
            }
        }

        stage('Publish Allure report') {
            steps {
                echo "Publishing Allure report from '${env.ALLURE_RESULTS}'"
                // El plugin de Allure en Jenkins lee directamente desde allure-results
                // NO usa el servidor Docker en http://localhost:4040
                allure includeProperties: false,
                       jdk: '',
                       results: [[path: "${env.ALLURE_RESULTS}"]]

                echo 'Publishing Allure report - done'
            }
        }
    }

    post {
        always {
            echo 'Archiving logs and screenshots from target/...'
            // Deja artefactos básicos aunque la build falle
            archiveArtifacts artifacts: 'target/**/*.log, target/screenshots/**/*',
                             onlyIfSuccessful: false,
                             allowEmptyArchive: true
        }
    }
}
