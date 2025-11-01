/*
 * Jenkinsfile - Ejecuta pruebas (Chrome) una sola vez + Allure
 * Autor: Juan Pablo Leal
 */
pipeline {
  agent any
  options { timestamps() }
  environment {
    GRID_URL = 'http://selenium-hub:4444/wd/hub'
  }

  stages {
    stage('Test') {
      steps {
        script {
          def JDK = tool 'jdk-17'
          def M2  = tool 'maven-3.9.11'
          withEnv([
            "JAVA_HOME=${JDK}",
            "PATH=${M2}/bin:${JDK}/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
          ]) {
            sh '''
              rm -rf target/allure-results target/allure-report || true
              mvn -B clean test \
                -Dcucumber.features=src/test/resources/features \
                -Dcucumber.glue=definitions \
                -Dbrowser=chrome \
                -DseleniumGridUrl=${GRID_URL} \
                -Dsurefire.rerunFailingTestsCount=0
            '''
          }
        }
      }
    }

    stage('Allure') {
      steps {
        script {
          def ALLURE = tool 'Allure_2.35.1'
          withEnv([ "PATH=${ALLURE}/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin" ]) {
            sh '''
              if [ -d target/allure-results ]; then
                allure generate target/allure-results -o target/allure-report --clean
              else
                echo "sin resultados"
              fi
            '''
          }
        }
      }
    }
  }

  post {
    always {
      archiveArtifacts artifacts: 'target/allure-report/**', allowEmptyArchive: true
      cleanWs(deleteDirs: true, disableDeferredWipeout: true)
    }
  }
}

