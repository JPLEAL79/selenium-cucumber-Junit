pipeline {
    agent any

    tools {
        // Usar las herramientas definidas en "Global Tool Configuration"
        jdk    'jdk-17'
        maven  'maven-3.9.11'
    }

    options {
        // Mantener solo las últimas 5 ejecuciones
        buildDiscarder(logRotator(numToKeepStr: '5'))
        timestamps()
    }

    // Ejecutar todos los días a las 09:00
    triggers {
        cron('0 9 * * *')
    }

    environment {
        // URL del Selenium Grid accesible desde Jenkins
        SELENIUM_GRID_URL = 'http://host.docker.internal:4444/wd/hub'
        // Siempre headless en Jenkins
        HEADLESS = 'true'
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
                    rm -rf allure-results
                    rm -rf target/allure-report
                    mkdir -p allure-results
                '''
            }
        }

        stage('Run tests (Chrome & Firefox - headless)') {
            steps {
                script {
                    echo 'Running tests on Jenkins agent with Maven'

                    // Secuencia: primero Chrome, luego Firefox (no paralelo)
                    def browsers = ['chrome', 'firefox']

                    browsers.each { browserName ->
                        echo "Running tests on ${browserName.capitalize()} (headless)"

                        sh """
                            mvn test \
                              -Dskip.docker.allure=true \
                              -Dheadless=${HEADLESS} \
                              -Dbrowser=${browserName} \
                              -DseleniumGridUrl=${SELENIUM_GRID_URL}
                        """
                    }
                }
            }
        }

        stage('Publish Allure report') {
            steps {
                echo "Publishing Allure report from 'allure-results'"

                // Usa el plugin de Allure en Jenkins, leyendo directamente desde allure-results
                allure commandline: 'Allure_2.35.1',
                       results: [[path: 'allure-results']]
            }
        }
    }

    post {
        always {
            echo 'Archiving logs and screenshots from target/...'
            archiveArtifacts artifacts: 'target/**/*.*', allowEmptyArchive: true
        }
    }
}
