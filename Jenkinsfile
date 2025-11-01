/*
 * Jenkinsfile - Pipeline CI/CD para Selenium Cucumber JUnit
 * Autor: Juan Pablo Leal
 * Descripción:
 * Ejecuta pruebas en Selenium Grid usando Chrome,
 * elimina reportes antiguos, publica Allure y limpia el workspace.
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

    stage('Compilar y ejecutar pruebas en Chrome') {
      steps {
        echo 'Ejecutando pruebas en Chrome...'
        catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
          sh 'mvn -B clean test -Dbrowser=chrome -DseleniumGridUrl=${GRID_URL}'
        }
      }
    }
  }

  post {
    always {
      echo 'Publicando reporte Allure...'
      allure([
        config: [allureInstallationName: 'Allure_2.35.1'],
        results: [[path: 'target/allure-results']],
        reportBuildPolicy: 'ALWAYS'
      ])
      echo 'Limpiando workspace...'
      cleanWs(deleteDirs: true, disableDeferredWipeout: true)
    }
  }
