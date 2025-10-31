/**
 * Autor: Juan Pablo Leal
 * Jenkinsfile – Selenium + Cucumber + JUnit + Allure
 * Ejecuta pruebas en jdk-maven, copia JSON al share de host y genera HTML de Allure en el mismo agent.
 */

pipeline {

    agent {
        docker {
            image 'jdk-maven'
            // Red del Grid y usuario root (sin tocar)
            args '--network=selenium-grid -u root -v C:/jenkins/data/allure-share/ecommerce-web-automation:/allure-share'
        }
    }

    environment {
        SELENIUM_GRID_URL = 'http://selenium-hub:4444/wd/hub'
        ALLURE_RESULTS = 'allure-results'   // carpeta generada por tests
        ALLURE_SHARE   = '/allure-share'    // punto de montaje del host dentro del agent
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '20'))
        disableConcurrentBuilds()
        timestamps()
    }

    stages {

        stage('Clean previous reports') {
            steps {
                echo 'Limpieza previa de reportes antiguos...'
                sh '''
                  rm -rf allure-results allure-report target || true
                  mkdir -p allure-results
                '''
            }
        }

        stage('Build & Dependencies') {
            steps {
                echo 'Compilando y resolviendo dependencias...'
                sh 'mvn -B -U clean compile -Dmaven.test.failure.ignore=true'
            }
        }

        stage('Run Automated Tests') {
            steps {
                echo 'Ejecutando pruebas contra Selenium Grid...'
                sh "mvn -B test -DseleniumGridUrl=${SELENIUM_GRID_URL} -Dmaven.test.failure.ignore=true"
            }
            post {
                always {
                    echo 'Publicando JUnit y artefactos Allure JSON...'
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'allure-results/**', allowEmptyArchive: true, fingerprint: true
                }
            }
        }

        stage('Export JSON to shared volume (4040)') {
            steps {
                echo 'Copiando JSON a volumen compartido del host...'
                sh '''
                  mkdir -p "${ALLURE_SHARE}"
                  rm -rf "${ALLURE_SHARE:?}"/* || true
                  cp -r "${ALLURE_RESULTS}"/* "${ALLURE_SHARE}/" || true
                  ls -la "${ALLURE_SHARE}" || true
                '''
            }
        }

        stage('Generate Allure HTML (inside jdk-maven)') {
            steps {
                echo 'Generando Allure HTML en el mismo agent...'
                sh '''
                  allure --version
                  rm -rf allure-report || true
                  allure generate "${ALLURE_RESULTS}" -c -o allure-report
                  ls -la allure-report || true
                '''
            }
            post {
                always {
                    echo 'Archivando Allure HTML...'
                    archiveArtifacts artifacts: 'allure-report/**', allowEmptyArchive: false, fingerprint: true
                }
            }
        }
    }

    post {
        always {
            echo 'Limpieza final del workspace...'
            cleanWs()
        }
        success { echo 'Pipeline finalizado con éxito.' }
        unstable { echo 'Pipeline UNSTABLE: revisar reportes/errores.' }
        failure { echo 'Pipeline FALLÓ: revisar consola.' }
    }
}
