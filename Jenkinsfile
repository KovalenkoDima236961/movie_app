pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/KovalenkoDima236961/movie_app.git'
            }
        }

        stage('Test Backend') {
            steps {
                dir('backend') {
                    dir('movie_app') {
                        sh 'mvn test'  // Run backend tests
                    }
                }
            }
        }

        stage('Build Backend') {
            steps {
                dir('backend') {
                   dir('movie_app') {
                        sh 'mvn clean package'  // Build backend after tests pass
                   }
                }
            }
        }

        stage('Build Frontend') {
            steps {
                dir('frontend/movie_app') {
                    sh 'npm install'
                    sh 'npm run build'
                }
            }
        }

        stage('Deploy') {
            steps {
                sh 'docker-compose up -d --build'
            }
        }

    }

    post {
        always {
            cleanWs()
        }
    }

}