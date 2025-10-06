pipeline {
    agent {
        label 'k8smaster'
    }
    stages {
        stage('Clean CD Directory') {
            steps {
                script {
                    def fileCount = sh(script: "ls -A /home/naveen/workspace/CD | wc -l", returnStdout: true).trim()
                    if (fileCount != "0") {
                        sh 'rm -rf /home/naveen/workspace/CD/*'
                        echo "Files deleted from /home/naveen/workspace/CD"
                    } else {
                        echo "No files found in /home/naveen/workspace/CD. Skipping deletion."
                    }
                }
            }
        }

        stage('Pull the infra repo') {
            steps {
                git branch: 'main', url: 'https://github.com/NaveenKumarRB/Jenkins-Project.git'
                dir('/home/naveen/workspace/CD') {
                    echo "Changed working directory to /home/naveen/workspace/CD"
                }
            }
        }
        stage('Create a namespace') {
            steps {
                dir('/home/naveen/workspace/CD') {
                    sh 'kubectl apply -f namespace.yml'
                    sh 'kubectl get namespace' // assuming you meant 'svc' for services
                }
            }
        }
        stage('deploy the service.yml') {
            steps {
                dir('/home/naveen/workspace/CD') {
                    sh 'kubectl apply -f service.yml --namespace=noteappnamespace'
                }
            }
        }
    }
}