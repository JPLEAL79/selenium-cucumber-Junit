/**
 * Jenkinsfile – Selenium + Cucumber + JUnit + Allure
 * Author: Juan Pablo Leal
 * Purpose: Declarative pipeline for automated UI testing with Maven and Allure Reports.
 */

pipeline {

    agent any   // Executes the pipeline on the Jenkins controller or an available node

    environment {
        // Global environment variables
        MAVEN_HOME = '/var/jenkins_home/tools/hudson.tasks.Maven_MavenInstallation/maven-3.9.11'
        JAVA_HOME  = '/usr/lib/jvm/java-17-openjdk-amd64'
        PATH = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}"
        ALLURE_RESULTS = 'target/allure-results'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))   // Keep only the last 5 builds
        disableConcurrentBuilds()                       // Prevent concurrent executions
        timestamps()                                    // Add timestamps to console logs
    }

    stages {

        stage('Checkout Code') {
            steps {
                echo 'Checking out source code from GitHub...'
                git branch: 'feature/jenkins-pipeline-integration',
                    url: 'https://github.com/JPLEAL79/selenium-cucumber-Junit.git',
                    credentialsId: 'github-jenkins-ci'
            }
        }

        stage('Build & Resolve Dependencies') {
            steps {
                echo 'Building project and downloading Maven dependencies...'
                sh 'mvn clean compile -Dmaven.test.failure.ignore=true'
            }
        }

        stage('Run Automated Tests') {
            steps {
                echo 'Running automated tests (JUnit + Cucumber)...'
                sh 'mvn test -Dmaven.test.failure.ignore=true'
            }
            post {
                always {
                    echo 'Archiving test results...'
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'target/allure-results/**', fingerprint: true
                }
            }
        }

        stage('Generate Allure Report') {
            steps {
                echo 'Generating Allure report...'
                sh 'mvn allure:report'
            }
            post {
                success {
                    echo 'Allure report generated successfully.'
                    allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']], commandline: 'Allure_2.35.1'
                }
                failure {
                    echo 'Failed to generate Allure report.'
                }
            }
        }
    }

    post {
        always {
            echo 'Cleaning workspace and closing pipeline...'
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed — check the console output for details.'
        }
    }
}
