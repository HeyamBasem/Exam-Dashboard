# Docker & Kubernetes Deployment Guide for Local Applications

A comprehensive, curated roadmap and resource list designed to take you from zero knowledge to successfully containerizing and deploying your local applications onto a local Kubernetes cluster.

---

## 📋 Learning Roadmap & Core Milestones

To deploy your local application on Kubernetes, you only need to master **4 core milestones**. Do not get overwhelmed by production-level concepts like Ingress Controllers, Helm charts, or Service Meshes until you have mastered these fundamentals.

```
┌─────────────────┐     ┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│ 1. Docker       │ ──> │ 2. Docker        │ ──> │ 3. Local K8s     │ ──> │ 4. K8s Manifest  │
│    Basics       │     │    Compose       │     │    Cluster Setup │     │    Deployment    │
└─────────────────┘     └──────────────────┘     └──────────────────┘     └──────────────────┘
```

| Phase | Core Focus | Key Concepts & Commands | Target Outcome |
| :--- | :--- | :--- | :--- |
| **1. Docker Basics** | Single-container packaging | `Dockerfile`, `docker build`, `docker run`, port mapping (`-p`), environment variables, volumes. | Containerize your local app and run it isolated. |
| **2. Multi-Container** | Multi-service local setup | `docker-compose.yml`, services, networks, volumes, environment files. | Run your application alongside a local database with 1 command (`docker compose up`). |
| **3. Local K8s Environment** | Cluster orchestration setup | Cluster architecture (Control Plane, Nodes), `kubectl` CLI, local cluster provisioning. | Have a lightweight, functional single-node cluster running locally. |
| **4. K8s Manifests** | Declarative deployments | YAML syntax, `Pods`, `Deployments`, `Services` (NodePort, ClusterIP), `ConfigMaps`, `Secrets`. | Deploy and access your containerized app inside Kubernetes. |

---

## 🔗 Best Learning Resources

### 1. Hands-On Interactive & Web Playgrounds (Fastest Start)
* **[Docker Official Getting Started Guide](https://docs.docker.com/get-started/)**: Official hands-on tutorial for building images and running containers step-by-step.
* **[Kubernetes Basics Interactive Tutorial](https://kubernetes.io/docs/tutorials/kubernetes-basics/)**: Browser-based scenario tutorials directly from the Kubernetes project.
* **[Killercoda Kubernetes & Docker Interactive Environments](https://killercoda.com/)**: Free interactive browser terminals with pre-configured Linux, Docker, and Kubernetes environments (no local installation required).
* **[Play with Docker](https://labs.play-with-docker.com/)**: Web-based sandbox to test and execute Docker commands in a live container environment.

### 2. Best Free Video Courses
* **[TechWorld with Nana — Docker Tutorial for Beginners](https://www.youtube.com/watch?v=3c-iBn73dDE)** (~2 hrs): Covers `Dockerfile` creation, image registries, networking, and Docker Compose with clear visual diagrams.
* **[TechWorld with Nana — Kubernetes Tutorial for Beginners](https://www.youtube.com/watch?v=X48VuDVv0do)** (~4 hrs): Detailed breakdown of Pods, Deployments, Services, ConfigMaps, and basic `kubectl` management.
* **[FreeCodeCamp — Docker & Kubernetes Full Course](https://www.youtube.com/watch?v=fqMOX6JJhGo)**: Practical, application-focused walk-through designed specifically for developers.

### 3. Essential Tools to Install
* **[Docker Desktop](https://www.docker.com/products/docker-desktop/)**: Combined UI and engine for managing Docker containers (includes built-in single-click Kubernetes engine).
* **[Minikube](https://minikube.sigs.k8s.io/docs/start/)**: Local Kubernetes cluster optimized for development.
* **[Kind (Kubernetes in Docker)](https://kind.sigs.k8s.io/)**: Tool for running local Kubernetes clusters using Docker container nodes.
* **[kubectl CLI](https://kubernetes.io/docs/tasks/tools/)**: Official command-line utility for communicating with a Kubernetes cluster.

---

## 🛠️ Step-by-Step Local Deployment Guide

### Step 1: Write a `Dockerfile` for Your App
Create a file named `Dockerfile` in the root of your application repository:

```dockerfile
# Use an official lightweight runtime image
FROM node:18-alpine

# Set working directory inside the container
WORKDIR /app

# Copy dependency definitions and install packages
COPY package*.json ./
RUN npm install --production

# Copy remaining source code
COPY . .

# Expose application port
EXPOSE 3000

# Set entry command
CMD ["npm", "start"]
```

### Step 2: Build and Run Your Container Locally
Build the Docker image and test running it on your host machine:

```bash
# 1. Build the image with a tag
docker build -t my-app:v1 .

# 2. Run the container mapping host port 3000 to container port 3000
docker run -d -p 3000:3000 --name my-running-app my-app:v1

# 3. Verify execution
docker ps
```

### Step 3: Start Your Local Kubernetes Cluster
If using **Minikube**:

```bash
# Start the local cluster
minikube start

# Push your local Docker image into Minikube's image cache
minikube image load my-app:v1
```

### Step 4: Create Kubernetes Manifest (`deployment.yaml`)
Create a file named `deployment.yaml` containing both the `Deployment` (runs the app) and `Service` (exposes network access):

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app-deployment
  labels:
    app: my-app
spec:
  replicas: 2
  selector:
    matchLabels:
      app: my-app
  template:
    metadata:
      labels:
        app: my-app
    spec:
      containers:
      - name: my-app-container
        image: my-app:v1
        imagePullPolicy: Never # Forces K8s to use local image instead of pulling from remote registry
        ports:
        - containerPort: 3000
---
apiVersion: v1
kind: Service
metadata:
  name: my-app-service
spec:
  type: NodePort
  selector:
    app: my-app
  ports:
    - port: 3000
      targetPort: 3000
      nodePort: 30080
```

### Step 5: Deploy to Kubernetes & Verify

```bash
# Apply the configuration
kubectl apply -f deployment.yaml

# Check running Pods
kubectl get pods

# Check active Services
kubectl get services

# Access your app in Minikube
minikube service my-app-service
```

---

## 📌 Cheat Sheet: Essential Commands

### Docker Commands
* `docker build -t <image-name>:<tag> .` — Build image from Dockerfile.
* `docker run -p <host-port>:<container-port> <image-name>` — Run container with port forwarding.
* `docker ps` — List active running containers.
* `docker logs <container-id>` — View logs of a running container.
* `docker stop <container-id>` — Stop running container.

### Kubernetes (`kubectl`) Commands
* `kubectl apply -f <filename.yaml>` — Apply/create resources from YAML.
* `kubectl get pods` — List all pods in active namespace.
* `kubectl get services` — List networking services.
* `kubectl logs -f <pod-name>` — Stream logs from a specific pod.
* `kubectl describe pod <pod-name>` — Inspect pod status, events, and debug errors.
* `kubectl delete -f <filename.yaml>` — Delete created resources.
