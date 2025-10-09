/**
 * Jenkinsfile – Selenium + Cucumber + JUnit + Allure
 * Autor: Juan Pablo Leal
 * Propósito: Pipeline declarativo para ejecutar pruebas automatizadas
 * usando Maven, Cucumber y Allure Reports dentro del contenedor jdk-maven.
 */

pipeline {

    // Ejecuta todo dentro de la imagen que ya trae JDK + Maven (tu "jdk-maven")
    agent {
        docker {
            image 'jdk-maven'
            args '-v /var/jenkins_home:/home/jenkins -u root'
        }
    }

    // Variables de entorno globales
    environment {
        ALLURE_RESULTS = 'target/allure-results'
    }

    options {
        // Mantener solo las últimas 5 ejecuciones
        buildDiscarder(logRotator(numToKeepStr: '5'))
        // No permitir builds concurrentes
        disableConcurrentBuilds()
        // Marcas de tiempo en consola
        timestamps()
        // Evita el checkout automático por defecto; lo haremos en nuestro stage
        skipDefaultCheckout(true)
    }

    stages {

        // 1) Checkout del código fuente con credenciales
        stage('Checkout Code') {
            steps {
                echo 'Clonando código desde el repositorio GitHub...'
                git branch: 'feature/jenkins-pipeline-integration',
                    url: 'https://github.com/JPLEAL79/selenium-cucumber-Junit.git',
                    credentialsId: 'github-jenkins-ci'
            }
        }

        // 2) Limpieza previa para evitar mezclar reportes viejos
        stage('Clean Workspace') {
            steps {
                echo 'Eliminando reportes y artefactos de ejecuciones anteriores...'
                sh 'rm -rf target/allure-results || true'
                sh 'rm -rf target/allure-report || true'
                sh 'rm -rf target/surefire-reports || true'
            }
        }

        // 3) Compilación y resolución de dependencias
        stage('Build & Dependencies') {
            steps {
                echo 'Compilando el proyecto y descargando dependencias Maven...'
                sh 'mvn clean compile -Dmaven.test.failure.ignore=true'
            }
        }

        // 4) Ejecución de pruebas
        stage('Run Automated Tests') {
            steps {
                echo 'Ejecutando pruebas automatizadas con JUnit y Cucumber...'
                sh 'mvn test -Dmaven.test.failure.ignore=true'
            }
            post {
                always {
                    echo 'Archivando los resultados de las pruebas...'
                    // Publica resultados JUnit en Jenkins (no falla si están vacíos)
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    // Archiva resultados de Allure para la publicación posterior
                    archiveArtifacts artifacts: 'target/allure-results/**', fingerprint: true
                }
            }
        }

        // 5) Generación y publicación del reporte Allure en Jenkins
        stage('Generate Allure Report') {
            steps {
                echo 'Generando reporte Allure...'
                sh 'mvn allure:report'
            }
            post {
                success {
                    echo 'Reporte Allure generado correctamente.'
                    // Publica el reporte Allure usando la instalación Allure_2.35.1 configurada en "Manage Jenkins → Tools"
                    allure includeProperties: false,
                           jdk: '',
                           results: [[path: 'target/allure-results']],
                           commandline: 'Allure_2.35.1'
                }
            }
        }
    }

    // Limpieza final del workspace y mensajes de estado
    post {
        always {
            echo 'Limpieza final del workspace...'
            cleanWs()
        }
        success {
            echo 'Pipeline finalizado correctamente.'
        }
        failure {
            echo 'El pipeline ha fallado. Revisar logs de consola.'
        }
    }
}
