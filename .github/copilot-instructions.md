# Copilot Instructions for Jenkins Project

## Project Overview
This repository orchestrates CI/CD for a Django-based notes application using Jenkins pipelines and Kubernetes manifests. The main components are:
- **Jenkins Pipeline Scripts**: `ci.groovy` (Continuous Integration), `cd.groovy` (Continuous Deployment)
- **Kubernetes Manifests**: `deployment.yml`, `service.yml` (for deploying and exposing the app)

## Architecture & Data Flow
- **CI Pipeline (`ci.groovy`)**: Pulls code from GitHub, builds a Docker image, runs a container, tags and pushes the image to Docker Hub.
- **CD Pipeline (`cd.groovy`)**: Triggers Maven build (Java-based, but the main app is Django; this may be legacy or for a supporting service).
- **Kubernetes**: Deploys the Docker image (`naveenkumarrb/notes-app`) as a scalable service (`noteapp-deployment`).

## Developer Workflows
- **Build & Test**: Use Jenkins to run the pipeline scripts. The build step uses Maven in `cd.groovy` and Docker in `ci.groovy`.
- **Docker Image Management**: Images are tagged and pushed to Docker Hub (`naveenkumarrb/notes-app:v1`).
- **Kubernetes Deployment**: Apply `deployment.yml` and `service.yml` to your cluster to deploy the app.

## Project-Specific Conventions
- **Jenkins Agents**: `ci.groovy` uses `label 'salve'`, `cd.groovy` uses `lablel 'master-node'` (note: typo in 'lablel').
- **Branching**: CI pipeline pulls from the `dev` branch of the app repo.
- **Container Naming**: Docker containers are named `notecontainer` for easier management.
- **Port Mapping**: Container port 8000 is mapped to host port 8004 in CI; service exposes 8005 -> 8000 in Kubernetes.

## Integration Points
- **External Repos**: Pulls from `https://github.com/NaveenKumarRB/django-notes-app.git`.
- **Docker Hub**: Pushes images to `naveenkumarrb/notes-app`.
- **Kubernetes**: Uses manifests to deploy and expose the app.

## Patterns & Examples
- **Jenkins Pipeline Example**:
  ```groovy
  pipeline {
      agent { label 'salve' }
      stages {
          stage('GITPULL APP REPO') { ... }
          stage('Build the Docker image') { ... }
          stage('Push to Docker Hub') { ... }
      }
  }
  ```
- **Kubernetes Deployment Example**:
  ```yaml
  apiVersion: apps/v1
  kind: Deployment
  metadata:
    name: noteapp-deployment
    labels:
      app: noteapp
  spec:
    replicas: 3
    ...
  ```

## Key Files
- `ci.groovy`: Main CI pipeline
- `cd.groovy`: CD pipeline (check for agent label typo)
- `deployment.yml`, `service.yml`: Kubernetes manifests

## Notes
- Credentials (e.g., Docker Hub password) are hardcoded in `ci.groovy`—consider using Jenkins credentials for security.
- The Maven build in `cd.groovy` may be legacy; verify its relevance to the Django app.

---
**For AI agents:**
- Always check for hardcoded values and typos in pipeline scripts.
- Follow the CI pipeline for building and deploying the main app.
- Use the provided Kubernetes manifests for deployment.
- Reference this file for conventions and integration points.
