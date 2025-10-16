pipeline {
    agent {
        label 'salve'
    }
    stages {
        stage('GITPULL APP REPO') {
            steps {
                echo 'Pulling the APP Repo'
                git branch: 'dev', url: 'https://github.com/NaveenKumarRB/django-notes-app.git'
            }
        }
        stage('Clean Existing Docker Artifacts') {
            steps {
                echo 'Removing existing Docker container and image if they exist'
                // Stop and remove the container if it exists
                sh '''
                    if [ "$(sudo docker ps -aq -f name=notecontainer)" ]; then
                        echo "Stopping and removing existing container: notecontainer"
                        sudo docker stop notecontainer || true
                        sudo docker rm notecontainer || true
                    fi
                    if [ "$(sudo docker images -q noteapp:v1)" ]; then
                        echo "Removing existing image: noteapp:v1"
                        sudo docker rmi noteapp:v1 || true
                    fi
                    if [ "$(sudo docker images -q naveenkumarrb/notes-app:v1)" ]; then
                        echo "Removing existing tagged image: naveenkumarrb/notes-app:v1"
                        sudo docker rmi naveenkumarrb/notes-app:v1 || true
                    fi
                '''
            }
        }
        stage('change the directory to /home/jenkins/workspace/CI/') {
            steps {
                echo 'Change the directory to /home/jenkins/workspace/CI/'
                sh 'cd /home/jenkins/workspace/CI/'
            }
        }
        stage('Build the Docker image') {
            steps {
                echo 'Building the docker image from the Dockerfile'
                sh 'sudo docker build -t noteapp:v1 .'
            }
        }
        stage('Create the container') {
            steps {
                echo 'Creating and running the Docker container'
                sh 'sudo docker run -it -d -p 8004:8000 --name notecontainer noteapp:v1'
            }
        }
        stage('Tag the Docker image') {
            steps {
                echo 'Tagging the Docker image for Docker Hub'
                sh 'sudo docker tag noteapp:v1 naveenkumarrb/notes-app:v1'
            }
        }
        stage('Login to Hub') {
            steps {
                echo 'Logging in to Docker Hub'
                sh 'sudo docker login -u naveenkumarrb -p K1ngm@k3rs'
            }
        }
        stage('Push to Docker Hub') {
            steps {
                echo 'Pushing the tagged image to Docker Hub'
                sh 'sudo docker push naveenkumarrb/notes-app:v1'
            }
        }
    }
}