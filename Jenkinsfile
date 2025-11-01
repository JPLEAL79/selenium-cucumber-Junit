/*
 * Jenkinsfile - Pipeline CI/CD para automatización con Selenium Cucumber JUnit
 * Autor: Juan Pablo Leal
 * Descripción:
 * Ejecuta pruebas en Selenium Grid usando Chrome y Firefox,
 * elimina reportes Allure antiguos, genera nuevos reportes
 * y limpia el workspace después de la ejecución.
 */

pipeline {
  agent any
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

    stage('Compilar y ejecutar pruebas (Chrome y Firefox)') {
      matrix {
        axes {
          axis { name 'BROWSER'; values 'chrome', 'firefox' }
        }
        stages {
          stage('Ejecución de pruebas') {
            steps {
              echo "Ejecutando pruebas en ${BROWSER}..."
              sh 'mvn -B clean test -Dbrowser=${BROWSER} -DseleniumGridUrl=${GRID_URL}'
            }
          }
        }
      }
    }
  }

  post {
    always {
      echo 'Publicando reporte Allure...'
      allure([
        results: [[path: 'target/allure-results']],
        reportBuildPolicy: 'ALWAYS'
      ])
      echo 'Limpiando workspace...'
      cleanWs(deleteDirs: true, disableDeferredWipeout: true)
    }
  }
}
