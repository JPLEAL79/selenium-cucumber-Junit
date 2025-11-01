/*
 * Jenkinsfile mínimo y estable (Chrome + Allure CLI)
 * Autor: Juan Pablo Leal
 */

pipeline {
  agent any
  options {
    skipDefaultCheckout(false)
    buildDiscarder(logRotator(numToKeepStr: '10'))
    timestamps()
  }
  environment {
    GRID_URL = 'http://selenium-hub:4444/wd/hub'
  }

  stages {
    stage('Limpiar reportes antiguos') {
      steps {
        sh 'rm -rf target/allure-results target/allure-report || true'
      }
    }

    stage('Tests en Chrome (forzando JAVA_HOME/PATH)') {
      steps {
        script {
          def JDK_HOME = tool 'jdk-17'
          def M2_HOME = tool 'maven-3.9.11'
          withEnv([
            "JAVA_HOME=${JDK_HOME}",
            "M2_HOME=${M2_HOME}",
            // PATH limpio y forzado: Maven y Java del contenedor, NO Windows
            "PATH=${M2_HOME}/bin:${JDK_HOME}/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
          ]) {
            echo 'Sanidad: versiones'
            sh '''
              java -version
              mvn -v
              echo "Grid status:"
              (command -v curl >/dev/null 2>&1 || (apt-get update && apt-get install -y curl || true)) >/dev/null 2>&1
              curl -s http://selenium-hub:4444/status | head -n 3 || true
            '''
            // Ejecuta tests (si fallan, igual seguimos a publicar Allure)
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh 'mvn -B clean test -Dbrowser=chrome -DseleniumGridUrl=${GRID_URL}'
            }
          }
        }
      }
    }

    stage('Generar Allure (CLI)') {
      steps {
        script {
          def ALLURE_HOME = tool 'Allure_2.35.1'
          withEnv([
            "PATH=${ALLURE_HOME}/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
          ]) {
            sh '''
              if [ -d target/allure-results ]; then
                allure --version || true
                allure generate target/allure-results -o target/allure-report --clean
              else
                echo "No hay target/allure-results; se omite generación."
              fi
            '''
          }
        }
      }
    }

    stage('Publicar HTML Allure') {
      steps {
        publishHTML(target: [
          reportDir: 'target/allure-report',
          reportFiles: 'index.html',
          reportName: 'Allure Report',
          allowMissing: true,
          alwaysLinkToLastBuild: true,
          keepAll: true
        ])
      }
    }
  }

  post {
    always {
      cleanWs(deleteDirs: true, disableDeferredWipeout: true)
    }
  }
}
