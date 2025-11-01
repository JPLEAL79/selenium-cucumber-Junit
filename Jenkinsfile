/**
 * Autor: Juan Pablo Leal
 * Jenkinsfile – Selenium + Cucumber + JUnit + Allure
 * Corre en el contenedor Jenkins (sin agente Docker), publica JUnit,
 * copia JSON al share del host y genera Allure (plugin y/o HTML).
 */

pipeline {

    agent any

    environment {
        // Dentro de Docker:
        SELENIUM_GRID_URL = 'http://selenium-hub:4444/wd/hub'
        // Carpeta de resultados del framework:
        ALLURE_RESULTS = 'allure-results'
        // Share real dentro de Jenkins -> C:\jenkins\data\allure-share\ecommerce-web-automation
        ALLURE_SHARE   = '/var/jenkins_home/allure-share/ecommerce-web-automation'
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
                // Instala Maven/Allure si faltan en el contenedor Jenkins
                sh '''
                  set -e
                  if ! command -v mvn >/dev/null 2>&1; then
                    apt-get update -y && apt-get install -y maven wget unzip curl
                  fi
                  if ! command -v allure >/dev/null 2>&1; then
                    mkdir -p /opt && cd /opt
                    wget -q https://github.com/allure-framework/allure2/releases/download/2.35.1/allure-2.35.1.tgz
                    tar -zxf allure-2.35.1.tgz && mv allure-2.35.1 allure
                    ln -sf /opt/allure/bin/allure /usr/local/bin/allure
                  fi
                  mkdir -p "${ALLURE_SHARE}"
                '''
                sh 'mvn -B -U clean compile -Dmaven.test.failure.ignore=true'
            }
        }

        stage('Run Automated Tests') {
            steps {
                echo 'Ejecutando pruebas contra Selenium Grid...'
                // Hooks exige EXACTO -DseleniumGridUrl
                sh "mvn -B test -DseleniumGridUrl=${SELENIUM_GRID_URL} -Dmaven.test.failure.ignore=true"
            }
            post {
                always {
                    echo 'Publicando JUnit y archivando JSON de Allure...'
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
                  cp -r "${ALLURE_RESULTS}"/* "${ALLURE_SHARE}/" 2>/dev/null || true
                  ls -la "${ALLURE_SHARE}" || true
                '''
            }
        }

        stage('Allure Plugin Integration') {
            steps {
                echo 'Publicando resultados con el plugin Allure...'
                // Requiere tener instalado "Allure Jenkins Plugin" (ya lo tienes)
                allure([
                    includeProperties: false,
                    jdk: '',
                    results: [[path: "allure-results"]],
                    reportBuildPolicy: 'ALWAYS'
                ])
            }
        }

        stage('Generate Allure HTML (fallback)') {
            steps {
                echo 'Generando Allure HTML (fallback) en el mismo agente...'
                sh '''
                  set +e
                  if ! command -v allure >/dev/null 2>&1; then
                    echo "NO_ALLURE" > .allure_miss
                    mkdir -p allure-report
                    cat > allure-report/index.html <<'HTML'
<html><body><h3>Allure CLI no disponible.</h3><p>Se generó placeholder para no fallar el build.</p></body></html>
HTML
                  else
                    set -e
                    rm -rf allure-report || true
                    allure --version
                    allure generate "${ALLURE_RESULTS}" -c -o allure-report
                  fi
                '''
                script {
                    if (fileExists('.allure_miss')) {
                        currentBuild.result = 'UNSTABLE'
                        echo 'Allure CLI ausente → Build marcado UNSTABLE. Se adjunta HTML placeholder.'
                    }
                }
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
        success  { echo 'Pipeline finalizado con éxito.' }
        unstable { echo 'Pipeline UNSTABLE: revisar reportes/errores.' }
        failure  { echo 'Pipeline FALLÓ: revisar consola.' }
    }
}
