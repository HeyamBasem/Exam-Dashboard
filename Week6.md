# Week 6 — Containerize & Deploy the Application with Docker and Kubernetes

## 🎯 Week Goal

The goal of Week 6 is to take the assessment dashboard application you have built in the previous weeks and make it runnable in containers, then deploy it to a **local Kubernetes cluster**.

By the end of the week, the application should:

1. Run successfully inside a Docker container.
2. Be reproducible using Docker configuration.
3. Run with its required services locally using Docker Compose, where applicable.
4. Be deployed to a local Kubernetes cluster.
5. Be accessible through a Kubernetes Service.
6. Be documented with the commands needed to build, deploy, verify, and clean up the application.

This week focuses on the four deployment milestones from the study material:

**Docker Basics → Docker Compose → Local Kubernetes → Kubernetes Manifests**

> Do not spend time on production-level Kubernetes concepts such as Ingress Controllers, Helm, or Service Meshes. The focus is on understanding and implementing the fundamentals.

---

## 📚 Study Material

Use the provided **Docker & Kubernetes Deployment Guide for Local Applications** as the primary study material.

The material defines four core milestones:

- **Docker Basics:** `Dockerfile`, `docker build`, `docker run`, port mapping, environment variables, and volumes.
- **Docker Compose:** services, networks, volumes, and environment files.
- **Local Kubernetes:** cluster setup and `kubectl`.
- **Kubernetes Manifests:** YAML, Pods, Deployments, Services, ConfigMaps, and Secrets.

The expected deployment flow is:

```text
Application
    ↓
Dockerfile
    ↓
Docker Image
    ↓
Docker Container
    ↓
Local Kubernetes Cluster
    ↓
Deployment + Service
    ↓
Running Application
```

---

# 📝 Task

## Part 1 — Understand the Deployment Architecture

Before implementing anything, review the application and identify:

- Frontend application
- Backend/API application
- Database
- External dependencies, if any
- Ports used by each service
- Environment variables required by each service
- Persistent data requirements

Create a simple deployment diagram showing how the services communicate.

Example:

```text
                    ┌────────────────────┐
                    │      Browser       │
                    └─────────┬──────────┘
                              │
                              ▼
                    ┌────────────────────┐
                    │ Frontend Container │
                    └─────────┬──────────┘
                              │
                              ▼
                    ┌────────────────────┐
                    │ Backend Container  │
                    └─────────┬──────────┘
                              │
                              ▼
                    ┌────────────────────┐
                    │ Database Container │
                    └────────────────────┘
```

Adapt the diagram to the actual application architecture.

---

# Part 2 — Dockerize the Application

## 2.1 Create a Dockerfile

Create a `Dockerfile` for the application.

The Dockerfile should:

- Use an appropriate lightweight base image.
- Set a working directory.
- Copy dependency files before application source where appropriate.
- Install dependencies.
- Copy the application source.
- Expose the application's required port.
- Define the command used to start the application.

Example structure:

```dockerfile
FROM node:18-alpine

WORKDIR /app

COPY package*.json ./
RUN npm install --production

COPY . .

EXPOSE 3000

CMD ["npm", "start"]
```

**Important:** Adapt the example to the actual project. Do not blindly copy it if the application uses a different runtime, build process, port, or start command.

---

## 2.2 Create `.dockerignore`

Create a `.dockerignore` file and exclude unnecessary files from the Docker build context.

At minimum, consider:

```text
node_modules
.git
.env
npm-debug.log
```

Add any other project-specific files that should not be copied into the image.

---

## 2.3 Build the Docker Image

Build the application image:

```bash
docker build -t my-app:v1 .
```

Verify that the image exists:

```bash
docker images
```

---

## 2.4 Run the Container Locally

Run the application:

```bash
docker run -d   -p 3000:3000   --name my-running-app   my-app:v1
```

Verify that the container is running:

```bash
docker ps
```

Check its logs:

```bash
docker logs my-running-app
```

Open the application and verify that it behaves the same as the non-containerized version.

---

# Part 3 — Docker Compose

If the application requires multiple services such as a backend and database, create a `docker-compose.yml` file.

The Compose configuration should define:

- Application services
- Database service, if required
- Service networking
- Environment variables
- Required volumes
- Port mappings

The expected goal is to be able to start the local application stack with:

```bash
docker compose up
```

And stop it with:

```bash
docker compose down
```

### Expected Result

A new developer should be able to clone the project, configure the required environment variables, and start the local application stack without manually starting every service separately.

---

# Part 4 — Set Up a Local Kubernetes Cluster

Choose one local Kubernetes option:

- Minikube
- Kind
- Docker Desktop Kubernetes

The study material demonstrates Minikube, so Minikube is recommended for this task.

Start the cluster:

```bash
minikube start
```

Verify the cluster:

```bash
kubectl get nodes
```

You should have a healthy node before continuing.

---

# Part 5 — Load the Docker Image into Kubernetes

Build the Docker image first:

```bash
docker build -t my-app:v1 .
```

If using Minikube, load the image:

```bash
minikube image load my-app:v1
```

This allows Kubernetes to use the locally built image instead of pulling it from a remote registry.

---

# Part 6 — Create Kubernetes Manifests

Create a Kubernetes manifest file such as:

```text
k8s/
├── deployment.yaml
└── service.yaml
```

You may also combine them into one `deployment.yaml` if preferred.

---

## 6.1 Deployment

Create a Kubernetes `Deployment` that:

- Uses the Docker image created in Part 2.
- Runs at least **2 replicas**.
- Defines the container port.
- Uses appropriate labels/selectors.
- Uses `imagePullPolicy: Never` when relying on the locally loaded image.

Example:

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: my-app-deployment
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
          imagePullPolicy: Never
          ports:
            - containerPort: 3000
```

Adapt this to the actual application.

---

## 6.2 Service

Create a Kubernetes `Service` to expose the application.

For this local deployment, use a `NodePort` service.

Example:

```yaml
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

Understand the difference between:

- `port`
- `targetPort`
- `nodePort`

---

# Part 7 — Deploy to Kubernetes

Apply the Kubernetes configuration:

```bash
kubectl apply -f k8s/
```

Verify the Deployment:

```bash
kubectl get deployments
```

Verify the Pods:

```bash
kubectl get pods
```

Verify the Service:

```bash
kubectl get services
```

If using Minikube, access the application with:

```bash
minikube service my-app-service
```

---

# Part 8 — Troubleshooting Practice

Intentionally investigate at least **one deployment problem** or reproduce a common failure scenario.

Use Kubernetes commands such as:

```bash
kubectl describe pod <pod-name>
kubectl logs <pod-name>
kubectl get pods
kubectl get services
```

Document:

1. What problem occurred.
2. How you identified the problem.
3. Which command helped you investigate it.
4. How you fixed it.

This is important because deployment work is not only about creating YAML files; you should understand how to diagnose a failing Pod or Service.

---

# Part 9 — Clean Up

After testing, practice removing the Kubernetes resources:

```bash
kubectl delete -f k8s/
```

Verify that the resources have been removed:

```bash
kubectl get pods
kubectl get services
```

Stop/remove the local container if one is still running:

```bash
docker stop my-running-app
docker rm my-running-app
```

---

# ✅ Acceptance Criteria

## Docker

- [ ] A working `Dockerfile` exists in the repository.
- [ ] A `.dockerignore` file exists.
- [ ] The application can be built successfully with `docker build`.
- [ ] The Docker image starts successfully.
- [ ] The containerized application is accessible through the expected port.
- [ ] Application logs can be viewed using `docker logs`.

## Docker Compose

- [ ] A `docker-compose.yml` exists if the application requires multiple services.
- [ ] Required services can be started with `docker compose up`.
- [ ] Services can communicate through the Compose network.
- [ ] Required environment variables are configured correctly.
- [ ] Required persistent volumes are configured where appropriate.
- [ ] The stack can be stopped with `docker compose down`.

## Kubernetes

- [ ] A local Kubernetes cluster is running.
- [ ] `kubectl get nodes` shows a healthy node.
- [ ] The Docker image is available to the local Kubernetes cluster.
- [ ] A Kubernetes `Deployment` manifest exists.
- [ ] The Deployment runs at least 2 replicas.
- [ ] A Kubernetes `Service` manifest exists.
- [ ] The Service exposes the application.
- [ ] All expected Pods reach `Running` status.
- [ ] The application is accessible through the Kubernetes Service.
- [ ] You can inspect Pod logs.
- [ ] You can troubleshoot a Pod using `kubectl describe`.
- [ ] Kubernetes resources can be cleaned up successfully.

---

# 📦 Expected Deliverables

Submit a PR containing:

```text
project/
├── Dockerfile
├── .dockerignore
├── docker-compose.yml          # if applicable
├── k8s/
│   ├── deployment.yaml
│   └── service.yaml
└── docs/
    └── week6-deployment.md
```

The `week6-deployment.md` document should contain:

### 1. Architecture

A short explanation and diagram of the application's container/deployment architecture.

### 2. Docker

Explain:

- How to build the image.
- How to run the container.
- Which port is exposed.
- Required environment variables.

### 3. Docker Compose

Explain how to start and stop the complete local stack.

### 4. Kubernetes

Explain:

- Which local Kubernetes solution you selected.
- How to start the cluster.
- How to load/build the image.
- How to deploy the manifests.
- How to access the application.

### 5. Verification

Include the important commands used to verify:

```bash
kubectl get nodes
kubectl get deployments
kubectl get pods
kubectl get services
kubectl logs <pod-name>
```

### 6. Troubleshooting

Document the deployment issue investigated during the task and how it was resolved.

---

# 🎓 Learning Objectives

By the end of Week 6, you should be able to explain:

1. What a Docker image is.
2. What a Docker container is.
3. The purpose of a `Dockerfile`.
4. The difference between an image and a container.
5. How port mapping works with Docker.
6. Why Docker Compose is useful for multi-service applications.
7. What Kubernetes solves compared with running containers manually.
8. What a Kubernetes Pod is.
9. What a Kubernetes Deployment does.
10. What a Kubernetes Service does.
11. The difference between `port`, `targetPort`, and `nodePort`.
12. Why Kubernetes needs labels and selectors.
13. Why a locally built Docker image may need to be loaded into Minikube.
14. How to inspect logs from a failing container or Pod.
15. How to use `kubectl describe` to investigate Kubernetes problems.
16. How to deploy and remove an application using declarative Kubernetes manifests.

---

# ⭐ Stretch Goals

Only attempt these after all acceptance criteria are complete.

- Add Kubernetes environment configuration using a `ConfigMap`.
- Add sensitive configuration using a `Secret`.
- Deploy the database as a separate Kubernetes workload if the application requires one.
- Experiment with changing the Deployment replica count.
- Observe what happens when one Pod is deleted.
- Add a basic health check/readiness configuration if supported by the application.
- Compare the application deployment using Docker Compose versus Kubernetes.

Do **not** prioritize Helm, Ingress, Service Meshes, or production Kubernetes architecture during this week.

---

# 🧪 Final Demo

At the end of the week, demonstrate the following flow:

```text
1. Build Docker image
        ↓
2. Run application with Docker
        ↓
3. Verify application
        ↓
4. Start Kubernetes cluster
        ↓
5. Load image into cluster
        ↓
6. Apply Kubernetes manifests
        ↓
7. Verify 2 running Pods
        ↓
8. Verify Kubernetes Service
        ↓
9. Open application through Kubernetes
        ↓
10. Show logs / troubleshooting
        ↓
11. Clean up deployment
```

The final demonstration should prove that you understand the deployment process rather than simply having working configuration files.
