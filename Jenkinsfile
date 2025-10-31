/**
 * Autor: Juan Pablo Leal
 * Jenkinsfile – Selenium + Cucumber + JUnit + Allure
 * Pipeline declarativo para ejecutar pruebas dentro del contenedor Docker jdk-maven,
 * publicar reportes Allure en Jenkins y compartirlos con el contenedor allure-reports (4040).
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
        ALLURE_TOOL = 'Allure_2.35.1'
        ALLURE_RESULTS = 'allure-results'
        ALLURE_SHARE = '/var/jenkins_home/allure-share/ecommerce-web-automation'
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
                echo 'Compilando proyecto y resolviendo dependencias Maven...'
                sh 'mvn -B -U clean compile -Dmaven.test.failure.ignore=true'
            }
        }

        stage('Run Automated Tests') {
            steps {
                echo 'Ejecutando pruebas (Cucumber + JUnit) contra el Selenium Grid...'
                sh "mvn -B test -DseleniumGridUrl=${SELENIUM_GRID_URL} -Dmaven.test.failure.ignore=true"
            }
            post {
                always {
                    echo 'Guardando resultados JUnit y Allure...'
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'allure-results/**', allowEmptyArchive: true, fingerprint: true
                    stash name: 'allure-results', includes: 'allure-results/**', allowEmpty: true
                }
            }
        }

        stage('Export Allure Results for 4040') {
            agent { label 'built-in' } // Se ejecuta dentro del contenedor Jenkins
            steps {
                echo 'Exportando resultados hacia carpeta compartida persistente...'
                dir('allure-export') { unstash 'allure-results' }
                sh '''
                  echo "Copiando resultados desde workspace a volumen compartido..."
                  mkdir -p /var/jenkins_home/allure-share/ecommerce-web-automation
                  rm -rf /var/jenkins_home/allure-share/ecommerce-web-automation/* || true
                  cp -r allure-export/allure-results/* /var/jenkins_home/allure-share/ecommerce-web-automation/ || true
                  ls -la /var/jenkins_home/allure-share/ecommerce-web-automation/ || true
                '''
            }
        }

        stage('Publish Allure Report in Jenkins') {
            agent { label 'built-in' } // Usa la tool de Allure instalada en Jenkins
            steps {
                echo 'Generando reporte Allure dentro de Jenkins...'
                script {
                    catchError(buildResult: 'UNSTABLE', stageResult: 'UNSTABLE') {
                        allure(
                            includeProperties: false,
                            jdk: '',
                            tool: "${ALLURE_TOOL}",
                            results: [[path: "${ALLURE_RESULTS}"]]
                        )
                    }
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
        unstable { echo 'Pipeline finalizó UNSTABLE: revisa los reportes.' }
        failure { echo 'Pipeline falló: revisar errores de ejecución.' }
    }
}
