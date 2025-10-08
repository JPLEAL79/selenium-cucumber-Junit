/**
 * Jenkinsfile – Selenium + Cucumber + JUnit + Allure
 * Autor: Juan Pablo Leal
 * Propósito: Pipeline declarativo para ejecución de pruebas automatizadas
 */

pipeline {
    agent any   // Ejecuta el pipeline en el nodo principal (contenedor Jenkins actual)

    environment {
        // Variables globales de entorno
        MAVEN_HOME = '/var/jenkins_home/tools/hudson.tasks.Maven_MavenInstallation/maven-3.9.11'
        JAVA_HOME  = '/usr/lib/jvm/java-17-openjdk-amd64'
        PATH = "${JAVA_HOME}/bin:${MAVEN_HOME}/bin:${PATH}"
        ALLURE_RESULTS = 'target/allure-results'
    }

    stages {

        stage('Checkout Code') {
            steps {
                echo 'Clonando código desde el repositorio GitHub...'
                git branch: 'develop',
                    url: 'https://github.com/JPLEAL79/selenium-cucumber-Junit.git'
            }
        }

        stage('Build & Dependencies') {
            steps {
                echo 'Compilando el proyecto y descargando dependencias Maven...'
                sh 'mvn clean compile -Dmaven.test.failure.ignore=true'
            }
        }

        stage('Run Tests') {
            steps {
                echo 'Ejecutando pruebas automáticas con JUnit y Cucumber...'
                sh 'mvn test -Dmaven.test.failure.ignore=true'
            }
            post {
                always {
                    echo 'Archivando resultados de pruebas...'
                    junit '**/target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'target/allure-results/**', fingerprint: true
                }
            }
        }

        stage('Generate Allure Report') {
            steps {
                echo 'Generando reporte Allure...'
                sh 'mvn allure:report'
            }
            post {
                success {
                    echo 'Reporte Allure generado correctamente.'
                    // Publica el reporte dentro de Jenkins usando la instalación Allure configurada (Allure_2.35.1)
                    allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']], commandline: 'Allure_2.35.1'
                }
            }
        }
    }

    post {
        always {
            echo 'Limpieza del workspace y cierre del pipeline...'
            cleanWs()
        }
    }
}
