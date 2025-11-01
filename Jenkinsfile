/*
 * Jenkinsfile - Pipeline CI/CD para automatización con Selenium Cucumber JUnit
 * Autor: Juan Pablo Leal
 * Descripción:
 * - Ejecuta pruebas contra Selenium Grid en un solo navegador (parametrizable).
 * - Elimina reportes Allure antiguos antes de correr.
 * - Publica Allure usando la herramienta configurada en Jenkins.
 * - Limpia el workspace al final para ahorrar espacio.
 */

pipeline {
  agent any

  parameters {
    choice(name: 'BROWSER', choices: ['chrome', 'firefox'], description: 'Navegador a usar')
  }

  tools {
    jdk 'jdk-17'
    maven 'maven-3.9.11'
  }

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
        echo 'Eliminando reportes Allure anteriores...'
        sh 'rm -rf target/allure-results target/allure-report || true'
      }
    }

    stage('Compilar y ejecutar pruebas') {
      steps {
        echo "Ejecutando pruebas en ${params.BROWSER}..."
        // Si las pruebas fallan, marcamos UNSTABLE pero dejamos continuar para publicar Allure
        catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
          sh "mvn -B clean test -Dbrowser=${params.BROWSER} -DseleniumGridUrl=${GRID_URL}"
        }
      }
    }
  }

  post {
    always {
      echo 'Verificando directorio de resultados Allure...'
      sh 'ls -la target || true; ls -la target/allure-results || true'

      echo 'Publicando reporte Allure...'
      allure([
        // IMPORTANTE: toolName debe coincidir con el configurado en Global Tool Configuration
        toolName: 'Allure_2.35.1',
        results: [[path: 'target/allure-results']],
        reportBuildPolicy: 'ALWAYS'
      ])

      echo 'Limpiando workspace...'
      cleanWs(deleteDirs: true, disableDeferredWipeout: true)
    }
  }
}
