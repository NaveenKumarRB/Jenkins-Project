pipeline {
    agent {
        label 'k8smaster'
    }

    environment {
        WORKSPACE_DIR = '/home/naveen/workspace/CD'
        NAMESPACE = 'noteappnamespace'
    }

    stages {
        stage('Clean CD Directory') {
            steps {
                script {
                    def fileCount = sh(script: "ls -A ${env.WORKSPACE_DIR} | wc -l", returnStdout: true).trim()
                    if (fileCount != "0") {
                        sh "rm -rf ${env.WORKSPACE_DIR}/*"
                        echo "Files deleted from ${env.WORKSPACE_DIR}"
                    } else {
                        echo "No files found in ${env.WORKSPACE_DIR}. Skipping deletion."
                    }
                }
            }
        }

        stage('Pull the infra repo') {
            steps {
                git branch: 'main', url: 'https://github.com/NaveenKumarRB/Jenkins-Project.git'
                echo "Infra repo pulled into ${env.WORKSPACE_DIR}"
            }
        }

        stage('Create a namespace') {
            steps {
                dir("${env.WORKSPACE_DIR}") {
                    sh 'kubectl apply -f namespace.yml'
                    sh 'kubectl get namespace'
                }
            }
        }

        stage('Deploy service.yml') {
            steps {
                dir("${env.WORKSPACE_DIR}") {
                    sh "kubectl apply -f service.yml --namespace=${env.NAMESPACE}"
                }
            }
        }

        stage('Deploy deployment.yml') {
            steps {
                dir("${env.WORKSPACE_DIR}") {
                    sh "kubectl apply -f deployment.yml --namespace=${env.NAMESPACE}"
                }
            }
        }
        
        stage('Port Forwarding 8000:8000') {
            steps {
                dir("${env.WORKSPACE_DIR}") {
                    // Corrected command and syntax
                    sh 'sudo kubectl port-forward svc/noteapp-service -n noteappnamespace 8000:8000 --address=0.0.0.0 &'
                }
            }
        }
    }
}