// ============================================================================
// Jenkinsfile - Selenium + Cucumber + JUnit + Allure (Docker + Jenkins)
// - Corre Chrome y Firefox de forma SECUENCIAL (más estable).
// - Ejecuta tests con Maven en el agente Jenkins SIEMPRE en headless.
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
        // URL del Selenium Grid; ajusta si tu Jenkins no ve el host "selenium-hub"
        // Por ejemplo, podrías necesitar: http://host.docker.internal:4444/wd/hub
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
                    def browsers   = ['chrome', 'firefox']
                    def gridUrl    = env.SELENIUM_GRID_URL
                    def mavenFlags = env.MAVEN_FLAGS

                    for (browser in browsers) {
                        echo 'Running tests inside jdk-maven'            // mensaje requerido
                        echo "Running tests on ${browser.capitalize()} (headless)"

                        // Ejecuta Maven DIRECTO en el agente Jenkins
                        // - Usa el workspace actual de Jenkins
                        // - Usa Selenium Grid (URL por env var)
                        // - Fuerza headless y desactiva Allure-Docker
                        sh """
                            mvn test ${mavenFlags} \
                                -Dbrowser=${browser} \
                                -DseleniumGridUrl=${gridUrl}
                        """
                    }
                }
            }
        }

        stage('Publish Allure report') {
            steps {
                echo "Publishing Allure report from '${env.ALLURE_RESULTS}'"

                // El plugin de Allure en Jenkins lee directamente desde allure-results
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
            archiveArtifacts artifacts: 'target/**/*.log, target/screenshots/**/*',
                             onlyIfSuccessful: false,
                             allowEmptyArchive: true
        }
    }
}
