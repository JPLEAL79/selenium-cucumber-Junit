/**
 *Autor: Juan Pablo Leal
 * Jenkinsfile – Selenium + Cucumber + JUnit + Allure
 * Pipeline declarativo para ejecutar pruebas dentro del contenedor jdk-maven,
 * conectado a la red del Selenium Grid.
 */

pipeline {

    agent {
        docker {
            image 'jdk-maven'
            args '--network=selenium-grid -u root'
        }
    }

    environment {
        SELENIUM_GRID_URL = 'http://selenium-hub:4444/wd/hub'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '20'))
        disableConcurrentBuilds()
        timestamps()
    }

    stages {

        stage('Clean previous reports') {
            steps {
                echo 'Limpieza previa de artefactos antiguos...'
                sh '''
                  rm -rf target/surefire-reports target/allure-results target/allure-report allure-results allure-report || true
                  mkdir -p target/allure-results
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
                echo 'Ejecutando pruebas (Cucumber + JUnit) contra el Selenium Grid...'
                sh "mvn -B test -Dselenium.grid.url=${SELENIUM_GRID_URL} -Dgrid.url=${SELENIUM_GRID_URL} -Dbrowser=chrome -Dmaven.test.failure.ignore=true"
            }
            post {
                always {
                    echo 'Publicando resultados JUnit y guardando Allure results...'
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'target/allure-results/**', allowEmptyArchive: true, fingerprint: true
                    stash name: 'allure-results', includes: 'target/allure-results/**', allowEmpty: true
                }
            }
        }

        // Exporta resultados para el contenedor Allure (4040)
        stage('Export Allure for 4040 (controller)') {
            agent { label 'built-in' }
            steps {
                echo 'Exportando target/allure-results a carpeta persistente del controlador...'
                dir('allure-export') { unstash 'allure-results' }
                sh """
                  mkdir -p "\${JENKINS_HOME}/allure-share/ecommerce-web-automation"
                  rm -rf "\${JENKINS_HOME}/allure-share/ecommerce-web-automation/*" || true
                  cp -r allure-export/target/allure-results/* "\${JENKINS_HOME}/allure-share/ecommerce-web-automation/" || true
                  ls -la "\${JENKINS_HOME}/allure-share/ecommerce-web-automation/" || true
                """
            }
        }

        stage('Allure Report (Jenkins)') {
            steps {
                echo 'Publicando reporte Allure en Jenkins...'
                allure commandline: 'allure-2.35.1',
                       includeProperties: false,
                       jdk: '',
                       results: [[path: 'target/allure-results']]
            }
        }
    }

    post {
        always {
            echo 'Limpieza final del workspace...'
            cleanWs()
        }
        success { echo 'Pipeline finalizado OK.' }
        failure { echo 'Pipeline falló. Revisa la consola.' }
    }
}
