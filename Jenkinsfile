pipeline {
    agent any
    tools {
        maven 'maven-3.9.11'
        jdk   'jdk-17'
    }
    stages {
        stage('Check Java & Maven') {
            steps {
                sh '''
                    echo "JAVA_HOME=$JAVA_HOME"
                    java -version
                    mvn -version
                '''
            }
        }
    }
}
