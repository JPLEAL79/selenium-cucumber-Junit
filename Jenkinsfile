pipeline {
  agent any
  stages {
    stage('Dump env (buscar C:\\ en vars)') {
      steps {
        sh '''
          echo "== ENV suspicious =="
          env | egrep -i 'JAVA|MAVEN|ALLURE|PATH'
          echo "== which =="
          which java || true
          which mvn || true
        '''
      }
    }
    stage('Resolver tools y forzar PATH') {
      steps {
        script {
          def JDK  = tool 'jdk-17'
          def M2   = tool 'maven-3.9.11'
          withEnv([
            "JAVA_HOME=${JDK}",
            "M2_HOME=${M2}",
            "PATH=${M2}/bin:${JDK}/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin"
          ]) {
            sh '''
              echo "== VERSIONES FORZADAS =="
              java -version
              mvn -v
            '''
            // si falla Maven, seguimos viendo el log
            catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
              sh 'mvn -B -q -DskipTests help:effective-settings'
            }
          }
        }
      }
    }
  }
}
