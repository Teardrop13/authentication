pipeline {
    agent any

    tools {
        jdk "jdk21"
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn clean install -DskipTests'
                archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
            }
        }
        stage('Test') {
            steps {
                sh 'mvn test || true'
                junit '**/target/*.xml'
            }
        }
        stage('Deploy-m2') {
            when {
                expression {
                    currentBuild.result == null || currentBuild.result == 'SUCCESS'
                }
            }
            steps {
                sh 'mvn install -DskipTests'
            }
        }
    }
}