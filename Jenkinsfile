/**
 *Autor: Juan Pablo Leal
 * Jenkinsfile – Selenium + Cucumber + JUnit + Allure
 * Pipeline declarativo para ejecutar pruebas dentro del contenedor jdk-maven,
 * conectado a la red del Selenium Grid.
 */

pipeline {

    // El job corre dentro del contenedor Docker "jdk-maven"
    agent {
        docker {
            image 'jdk-maven'
            // Conecta a la red del Grid y ejecuta como root
            args '--network=selenium-grid -u root'
        }
    }

    environment {
        // URL del hub accesible desde este contenedor (nombre DNS del servicio en la red del Grid)
        SELENIUM_GRID_URL = 'http://selenium-hub:4444/wd/hub'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '20')) // conserva solo 20 builds
        disableConcurrentBuilds()                     // evita builds en paralelo
        timestamps()                                  // timestamps en logs
    }

    stages {

        stage('Clean previous reports') {
            steps {
                echo 'Limpieza previa de artefactos antiguos...'
                sh '''
                  rm -rf allure-results allure-report target/surefire-reports target/allure-results target/allure-report || true
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
                echo 'Ejecutando pruebas (Cucumber + JUnit) contra el Selenium Grid...'
                sh "mvn -B test -Dselenium.grid.url=${SELENIUM_GRID_URL} -Dgrid.url=${SELENIUM_GRID_URL} -Dbrowser=chrome -Dmaven.test.failure.ignore=true"
            }
            post {
                always {
                    echo 'Publicando resultados JUnit y guardando Allure results...'
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'allure-results/**', allowEmptyArchive: true, fingerprint: true
                }
            }
        }

        stage('Allure Report') {
            steps {
                echo 'Publicando reporte Allure...'
                allure commandline: 'allure-2.35.1',
                       includeProperties: false,
                       jdk: '',
                       results: [[path: 'allure-results']]
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
