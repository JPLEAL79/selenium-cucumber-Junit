// ============================================================================
// Jenkinsfile - Selenium + Cucumber + JUnit + Allure
// Autor: Juan Pablo Lea
// Ejecuta tests dentro del contenedor existente "jdk-maven" y publica Allure.
// ============================================================================

pipeline {
  agent any
  options {
    timestamps()
    ansiColor('xterm')
    disableConcurrentBuilds()       // evita loops por builds solapados
    buildDiscarder(logRotator(numToKeepStr: '15'))
    timeout(time: 30, unit: 'MINUTES')
  }

  parameters {
    string(name: 'BROWSER', defaultValue: 'chrome', description: 'chrome|firefox')
    string(name: 'GRID_URL', defaultValue: 'http://selenium-hub:4444/wd/hub', description: 'URL Selenium Grid')
    booleanParam(name: 'CLEAN', defaultValue: true, description: 'mvn clean antes de test')
  }

  environment {
    ALLURE_RESULTS = 'target/allure-results'    // definido también en tu POM :contentReference[oaicite:1]{index=1}
  }

  stages {
    stage('Checkout') {
      steps { checkout scm }
    }

    stage('Prechequeo contenedor') {
      steps {
        sh '''
          docker ps --format "{{.Names}}" | grep -q "^jdk-maven$" || {
            echo "[ERROR] Falta contenedor jdk-maven"; exit 1;
          }
        '''
      }
    }

    stage('Build & Test (en jdk-maven)') {
      steps {
        sh '''
          set -e
          if [ "${CLEAN}" = "true" ]; then
            docker exec jdk-maven sh -lc "mvn -q -DskipTests=true clean package"
          fi

          # Ejecuta tests con plugin de Allure para Cucumber ACTIVADO
          docker exec jdk-maven sh -lc '\
            mvn -q test \
              -Dbrowser="${BROWSER}" \
              -DseleniumGridUrl="${GRID_URL}" \
              -Dcucumber.plugin="pretty, summary, io.qameta.allure.cucumber7jvm.AllureCucumber7Jvm" \
          '
        '''
      }
      post {
        always {
          sh '''
            echo "[INFO] Archivos en target/allure-results:"
            ls -la ${ALLURE_RESULTS} || true
            find ${ALLURE_RESULTS} -type f | wc -l || true
          '''
        }
      }
    }

    stage('Publicar Allure') {
      steps {
        // Requiere plugin Allure en Jenkins y path de results del workspace
        allure includeProperties: false, jdk: '', results: [[path: "${ALLURE_RESULTS}"]]
      }
    }
  }

  post {
    always {
      archiveArtifacts artifacts: 'target/**/*.log, target/screenshots/**/*', onlyIfSuccessful: false
    }
  }
}
