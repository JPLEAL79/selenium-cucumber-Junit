// ============================================================================
// Jenkinsfile - Selenium + Cucumber + JUnit + Allure (Docker + Jenkins)
// Autor: Juan Pablo Leal
// - Ejecuta tests dentro del contenedor "jdk-maven" SIEMPRE en headless.
// - Corre Chrome y Firefox de forma SECUENCIAL (más estable).
// - Publica Allure en Jenkins usando la carpeta "allure-results" del workspace.
// - Jenkins solo conserva las ÚLTIMAS 5 ejecuciones (builds + artefactos).
// ============================================================================

pipeline {
    agent any

    options {
        timestamps()
        ansiColor('xterm')
        disableConcurrentBuilds()                            // evita builds solapados
        buildDiscarder(logRotator(
            numToKeepStr: '5',                              // solo 5 builds
            artifactNumToKeepStr: '5'                       // solo artefactos de 5 builds
        ))
        timeout(time: 30, unit: 'MINUTES')
    }

    // Parámetros del job
    parameters {
        // Lista de navegadores a ejecutar, separados por espacio
        string(
            name: 'BROWSERS',
            defaultValue: 'chrome firefox',
            description: 'Browsers to run (space-separated): chrome firefox'
        )

        booleanParam(
            name: 'CLEAN',
            defaultValue: true,
            description: 'Run mvn clean before tests'
        )
    }

    environment {
        // Tu proyecto escribe en "allure-results" en la RAÍZ del repo
        // (coincide con la propiedad allure.results.directory del pom.xml)
        ALLURE_RESULTS = 'allure-results'
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Precheck jdk-maven container') {
            steps {
                sh '''
                    echo "[INFO] Checking jdk-maven container..."
                    if ! docker ps --format "{{.Names}}" | grep -q "^jdk-maven$"; then
                      echo "[ERROR] Container jdk-maven is not running."
                      exit 1
                    fi
                '''
            }
        }

        stage('Build & Test (inside jdk-maven, headless)') {
            steps {
                sh """
                    set -e

                    # Si el parámetro viene vacío, por seguridad usamos solo chrome
                    BROWSERS_LIST="\${BROWSERS}"
                    if [ -z "\${BROWSERS_LIST}" ]; then
                      BROWSERS_LIST="chrome"
                    fi

                    echo "[INFO] Browsers to run: \${BROWSERS_LIST}"

                    MVN_BASE="mvn -q"
                    if [ "\${CLEAN}" = "true" ]; then
                      MVN_BASE="\${MVN_BASE} clean"
                    fi

                    for B in \${BROWSERS_LIST}; do
                      echo "[INFO] Running tests in jdk-maven | browser=\${B} | headless=true"

                      CMD="\${MVN_BASE} test \\
                        -Dskip.docker.allure=true \\
                        -Dheadless=true \\
                        -Dbrowser=\${B} \\
                        -DseleniumGridUrl=http://selenium-hub:4444/wd/hub"

                      echo "[INFO] Executing inside jdk-maven: \${CMD}"
                      docker exec jdk-maven sh -lc "\${CMD}"
                    done
                """
            }
            post {
                always {
                    sh '''
                        echo "[INFO] Listing Allure results in ${ALLURE_RESULTS}:"
                        if [ -d "${ALLURE_RESULTS}" ]; then
                          ls -la "${ALLURE_RESULTS}" || true
                          echo "[INFO] Total files:"
                          find "${ALLURE_RESULTS}" -type f | wc -l || true
                        else
                          echo "[WARN] Directory ${ALLURE_RESULTS} does not exist in workspace."
                        fi
                    '''
                }
            }
        }

        stage('Publish Allure') {
            steps {
                echo "[INFO] Publishing Allure report from: ${ALLURE_RESULTS}"
                allure includeProperties: false,
                       jdk: '',
                       results: [[path: "${ALLURE_RESULTS}"]]
            }
        }
    }

    post {
        always {
            // Opcional: logs y screenshots si los generas en target/
            archiveArtifacts artifacts: 'target/**/*.log, target/screenshots/**/*',
                             onlyIfSuccessful: false
        }
    }
}
