pipeline {
    agent any

    tools {
        jdk   'jdk-17'
        maven 'maven-3.9.11'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
        buildDiscarder(logRotator(
            numToKeepStr: '5',
            artifactNumToKeepStr: '5'
        ))
    }

    // Ejecutar todos los días a las 09:00
    triggers {
        cron('0 9 * * *')
    }

    environment {
        // Selenium Grid desde Jenkins
        SELENIUM_GRID_URL = 'http://host.docker.internal:4444/wd/hub'
        // Carpeta de resultados Allure
        ALLURE_RESULTS    = 'allure-results'
        // Flags Maven para Jenkins (headless y sin Allure Docker)
        MAVEN_FLAGS       = '-Dskip.docker.allure=true -Dheadless=true'
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
                    def browsers   = ['chrome', 'firefox']
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

        stage('Publish Allure report') {
            steps {
                echo "Publishing Allure report from '${env.ALLURE_RESULTS}'"

                // Usa la instalación Allure_2.35.1 configurada en Jenkins
                allure commandline: 'Allure_2.35.1',
                       includeProperties: false,
                       jdk: '',
                       results: [[path: "${env.ALLURE_RESULTS}"]]

                echo 'Publishing Allure report - done'
            }
        }
    }

    post {
        // Guardar logs y screenshots sin acumular basura
        always {
            echo 'Archiving logs and screenshots...'

            // Copia solo target completo de la carpeta temporal
            sh '''
                rm -rf ci-artifacts
                mkdir -p ci-artifacts

                if [ -d "target" ]; then
                  cp -r target ci-artifacts/target
                fi
            '''

            // Jenkins ejecuta máx 5 builds
            archiveArtifacts artifacts: 'ci-artifacts/**/*',
                             allowEmptyArchive: true,
                             onlyIfSuccessful: false

            // Limpiamos temporales
            sh 'rm -rf ci-artifacts'
        }
    }
}
