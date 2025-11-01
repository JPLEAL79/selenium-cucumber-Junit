pipeline {
  agent any
  options { timestamps() }
  environment { GRID_URL = 'http://selenium-hub:4444/wd/hub' }

  stages {
    stage('Test') {
      steps {
        script {
          def JDK = tool 'jdk-17'
          def M2  = tool 'maven-3.9.11'
          withEnv(["JAVA_HOME=${JDK}", "PATH=${M2}/bin:${JDK}/bin:/usr/bin:/bin"]) {
            sh 'rm -rf target/allure-results target/allure-report || true'
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh 'mvn -B clean test -Dbrowser=chrome -DseleniumGridUrl=${GRID_URL}'
            }
          }
        }
      }
    }

    stage('Allure') {
      steps {
        script {
          def ALLURE = tool 'Allure_2.35.1'
          withEnv(["PATH=${ALLURE}/bin:/usr/bin:/bin"]) {
            sh 'test -d target/allure-results && allure generate target/allure-results -o target/allure-report --clean || echo "sin resultados"'
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
