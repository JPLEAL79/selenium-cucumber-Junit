/*
 * Jenkinsfile (DIAGNÓSTICO) — pasos mínimos y claros
 * Autor: Juan Pablo Leal
 */

pipeline {
  agent any

  tools {
    jdk   'jdk-17'
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
    stage('Sanidad: herramientas (java/mvn/allure)') {
      steps {
        script {
          // Inyecta Allure desde Global Tool por NOMBRE EXACTO
          def allureHome = tool 'Allure_2.35.1'
          withEnv(["PATH+ALLURE=${allureHome}/bin"]) {
            sh '''
              echo "== JAVA =="
              java -version || true
              echo "== MAVEN =="
              mvn -v || true; which mvn || true
              echo "== ALLURE =="
              allure --version || true
              echo "== PATH (recortado) =="
              echo "$PATH" | cut -c -200
            '''
          }
        }
      }
    }

    stage('Sanidad: red al Selenium Grid') {
      steps {
        sh '''
          echo "== RESOLUCIÓN DNS del hub =="
          getent hosts selenium-hub || true
          echo "== Probar endpoint /status =="
          (command -v curl >/dev/null 2>&1 || (echo "curl no está, intentando instalar (ignore si falla)"; (apk add --no-cache curl || apt-get update && apt-get install -y curl || true))) >/dev/null 2>&1
          curl -s http://selenium-hub:4444/status || true
        '''
      }
    }

    stage('Limpiar reportes antiguos') {
      steps {
        sh 'rm -rf target/allure-results target/allure-report || true'
      }
    }

    stage('Tests en Chrome (liviano)') {
      steps {
        echo 'Ejecutando pruebas en Chrome...'
        // Publicamos Allure incluso si fallan
        catchError(buildResult: 'UNSTABLE', stageResult: 'FAILURE') {
          sh 'mvn -B clean test -Dbrowser=chrome -DseleniumGridUrl=${GRID_URL}'
        }
      }
    }
  }

  post {
    always {
      echo 'Publicando reporte Allure...'
      script {
        def allureHome = tool 'Allure_2.35.1'
        withEnv(["PATH+ALLURE=${allureHome}/bin"]) {
          sh 'ls -la target || true; ls -la target/allure-results || true'
          allure(
            results: [[path: 'target/allure-results']],
            reportBuildPolicy: 'ALWAYS'
          )
        }
      }
      cleanWs(deleteDirs: true, disableDeferredWipeout: true)
    }
  }
}

