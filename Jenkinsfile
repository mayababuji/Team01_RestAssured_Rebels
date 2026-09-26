pipeline {
    agent any

    tools {
        maven 'mvn_home'
    }

    stages {
        stage('Run API Tests') {
            steps {
                withCredentials([
                    string(
                        credentialsId: 'admin.email',
                        variable: 'LMS_ADMIN_EMAIL'
                    ),
                    string(
                        credentialsId: 'admin.password',
                        variable: 'LMS_ADMIN_PASSWORD'
                    )
                ]) {
                    sh '''
                        java -version
                        mvn -version

                        mvn clean test \
                          -Denv=UAT \
                          -Dadmin.email="$LMS_ADMIN_EMAIL" \
                          -Dadmin.password="$LMS_ADMIN_PASSWORD"
                    '''
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'target/ExtentReports*/**, target/cucumber-reports/**', allowEmptyArchive: true

            allure includeProperties: false, jdk: '', results: [[path: 'target/allure-results']]
        }
    }
}