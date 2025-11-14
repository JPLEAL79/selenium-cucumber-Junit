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

    triggers {
        cron('0 9 * * *')
    }

    environment {
        SELENIUM_GRID_URL = 'http://host.docker.internal:4444/wd/hub'
        ALLURE_RESULTS    = 'allure-results'
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

                allure commandline: 'Allure_2.35.1',
                       includeProperties: false,
                       jdk: '',
                       results: [[path: "${env.ALLURE_RESULTS}"]]

                echo 'Publishing Allure report - done'
            }
        }
    }

    post {
        // Siempre guardar logs y screenshots (si existen), con retención limitada por Jenkins
        always {
            echo 'Archiving logs and screenshots...'

            // Preparar carpeta temporal con solo lo necesario
            sh '''
                rm -rf ci-artifacts
                mkdir -p ci-artifacts

                # Logs (target/**/*.log)
                if [ -d "target" ]; then
                  find target -type f -name "*.log" -exec cp --parents {} ci-artifacts/ \;
                fi

                # Screenshots (target/screenshots/**)
                if [ -d "target/screenshots" ]; then
                  mkdir -p ci-artifacts/screenshots
                  cp -r target/screenshots/* ci-artifacts/screenshots/ 2>/dev/null || true
                fi
            '''

            // Jenkins ejecuta máx. 5 builds
            archiveArtifacts artifacts: 'ci-artifacts/**/*',
                             allowEmptyArchive: true,
                             onlyIfSuccessful: false

            // Limpiar temporales del workspace
            sh 'rm -rf ci-artifacts'
        }
    }
