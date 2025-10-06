pipeline {
    agent {
        label 'salve'
    }
    stages {
        stage('GITPULL APP REPO') {
            steps {
                echo 'Pulling the APP Repo'
                // The git step checks out the code directly into the workspace.
                git branch: 'dev', url: 'https://github.com/NaveenKumarRB/django-notes-app.git'
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
                // Shell command for building the Docker image from the current directory
                sh 'sudo docker build -t noteapp:v1 .'
            }
        }
        stage('Create the container') {
            steps {
                echo 'Creating and running the Docker container'
                // Use '--name' to give the container a specific name for easier management
                sh 'sudo docker run -it -d -p 8004:8000 --name notecontainer noteapp:v1'
            }
        }
        stage('Tag the Docker image') {
            steps {
                echo 'Tagging the Docker image for Docker Hub'
                // User tag naveenkumarrb/notes-app:v1 give the specific name for easier management of image taging
                sh 'sudo docker tag noteapp:v1 naveenkumarrb/notes-app:v1'
            }
        }
        stage('Login to Hub') {
            steps {
                echo 'login to to Docker Hub'
                //docker login help you to login to docker hub
                sh 'sudo docker login -u naveenkumarrb -p K1ngm@k3rs'
            }
        }
        stage('Push to Docker Hub') {
            steps {
                echo 'Pushing the tagged image to Docker Hub'
                //docker push help you to push the naveenkumarrb/notes-app:v1 to your registory
                sh 'sudo docker push naveenkumarrb/notes-app:v1'
            }
        }
    }
}