pipeline {
  agent any
  options { timestamps() }
  environment {
    GRID_URL = 'http://selenium-hub:4444/wd/hub'
    MAVEN_REPO = '/var/jenkins_home/.m2/repository'
  }
  stages {
    stage('Test (una vez)') {
      steps {
        script {
          def JDK = tool 'jdk-17'
          def M2  = tool 'maven-3.9.11'
          sh "mkdir -p ${MAVEN_REPO}"
          withEnv([
            "JAVA_HOME=${JDK}",
            "PATH=${M2}/bin:${JDK}/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
          ]) {
            sh '''
              rm -rf target/allure-results target/allure-report || true
              mvn -B -U --no-transfer-progress \
                -Dmaven.repo.local=''' + "${MAVEN_REPO}" + ''' \
                -Dsurefire.rerunFailingTestsCount=0 \
                -DfailIfNoTests=false \
                clean test \
                -Dcucumber.features=src/test/resources/features \
                -Dcucumber.glue=definitions \
                -Dbrowser=chrome \
                -DseleniumGridUrl=''' + "${GRID_URL}" + '''
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
