// ============================================================================
// Jenkinsfile - selenium-cucumber-Junit
// Entorno: Jenkins + Selenium Grid (Docker) + Allure
//
// - Ejecuta tests SIEMPRE en headless (Chrome y Firefox) de forma SECUENCIAL.
// - Usa Maven configurado como herramienta de Jenkins (maven-3.9.11).
// - Publica Allure via plugin de Jenkins leyendo "allure-results" del workspace.
// - Mantiene solo las últimas 5 builds (logs + artefactos).
// - No toca tu Allure local ni el contenedor allure-reports (puerto 4040).
// ============================================================================

pipeline {
    // Agente donde se ejecuta el pipeline (contenedor/VM de Jenkins)
    agent any

    // Herramientas definidas en Manage Jenkins → Global Tool Configuration
    tools {
        maven 'maven-3.9.11'  // nombre EXACTO de tu Maven en Jenkins
        // Si más adelante ajustas bien jdk-17, puedes agregarlo aquí:
        // jdk 'jdk-17'
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
        // URL del Selenium Grid visto desde el contenedor de Jenkins.
        // Jenkins (en Docker) → host.docker.internal:4444 → selenium-hub:4444 (tu docker-compose)
        SELENIUM_GRID_URL = 'http://host.docker.internal:4444/wd/hub'

        // Carpeta donde Maven deja los resultados Allure (desde pom.xml)
        ALLURE_RESULTS = 'allure-results'

        // Flags de Maven:
        // - Forzamos headless SIEMPRE en Jenkins
        // - Desactivamos integración Docker/Allure del pom en Jenkins
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
        // 2) Limpieza controlada de resultados Allure en el workspace Jenkins
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
        // --------------------------------------------------------------------
        stage('Run tests (Chrome & Firefox - headless)') {
            steps {
                script {
                    def browsers   = ['chrome', 'firefox']    // orden SECUENCIAL
                    def gridUrl    = env.SELENIUM_GRID_URL
                    def mavenFlags = env.MAVEN_FLAGS

                    for (browser in browsers) {
                        echo 'Running tests on Jenkins agent with Maven'
                        echo "Running tests on ${browser.capitalize()} (headless)"

                        sh """
                            mvn test ${mavenFlags} \
                                -Dbrowser=${browser} \
                                -DseleniumGridUrl=${gridUrl}
                        """
                    }
                }
            }
        }

        // --------------------------------------------------------------------
        // 4) Publicar reporte Allure usando el plugin de Jenkins
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
