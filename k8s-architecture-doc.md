# Kubernetes Architecture & Documentation

This document serves as the comprehensive technical reference for the Exam Dashboard's Kubernetes infrastructure.

---

## 1. Architectural Overview

The application has been migrated from a single-machine Docker Compose setup to a distributed Kubernetes architecture. The local cluster is managed by **Minikube**.

### Resource Separation
Unlike Docker Compose, which uses a single YAML file, this architecture adheres to Kubernetes best practices by isolating each resource into its own manifest file within the `k8s/` directory:

1. **`postgres-secret.yml`**: An Opaque Secret storing base64-encoded database credentials and JWT signing keys.
2. **`postgres-pvc.yml`**: A PersistentVolumeClaim requesting 1Gi of storage to ensure database persistence across Pod restarts.
3. **`postgres-deployment.yml`**: Manages the PostgreSQL database Pod. Maps the PVC to `/var/lib/postgresql/data`.
4. **`postgres-service.yml`**: A `ClusterIP` Service providing a stable internal DNS name (`postgres:5432`) for the backend to connect to.
5. **`backend-deployment.yml`**: Manages 2 replicas of the Spring Boot application. Retrieves credentials securely via `secretKeyRef`.
6. **`backend-service.yml`**: A `NodePort` Service exposing the backend API on port 30080.
7. **`frontend-deployment.yml`**: Manages 2 replicas of the React/Nginx frontend.
8. **`frontend-service.yml`**: A `NodePort` Service exposing the frontend application on port 30000.

---

## 2. Core Kubernetes Concepts Implemented

### Deployments & Pods
We utilized **Deployments** rather than raw Pods. The Deployment controller constantly monitors the "Desired State" (e.g., `replicas: 2`). If a Pod is deleted or crashes, the Deployment immediately spawns a replacement (Self-Healing). 

### Networking & Services
Because Pod IP addresses are ephemeral (they change on restart), we implemented **Services**.
* The **Backend** connects to the Database using the internal DNS name provided by the Postgres Service (`jdbc:postgresql://postgres:5432/...`).
* The **Services** route traffic to the correct Pods using label selectors (e.g., `app: backend`).

### Rolling Updates
When a Deployment's image is updated, Kubernetes performs a Rolling Update. It starts the new Pods first, waits for them to become healthy, and only then terminates the old Pods, ensuring zero downtime.

---

## 3. Problems Faced & Resolutions

During the implementation of this architecture, we encountered several real-world DevOps challenges. Here is how they were resolved:

### Problem 1: Images Not Found in Kubernetes
* **Symptom:** Kubernetes could not find `exam-dashboard-backend:v1` even though it was successfully built in Docker Desktop.
* **Root Cause:** Minikube runs its own isolated virtual machine with its own Docker daemon. It cannot see the host machine's Docker image cache. Furthermore, without `imagePullPolicy: Never`, Kubernetes attempted to download the image from the public internet (Docker Hub).
* **Resolution:** We manually transferred the images into the cluster using `minikube image load <image-name>` and explicitly set `imagePullPolicy: Never` in the Deployment manifests.

### Problem 2: Backend CORS and Port Mapping Conflicts
* **Symptom:** The React frontend loaded successfully, but user registration and login failed.
* **Root Cause:** The React frontend was statically built to call `http://localhost:8080/api/v1`. However, the backend Service was running inside the Minikube virtual machine, not on the host Windows machine's `localhost`.
* **Resolution:** We utilized `kubectl port-forward service/backend 8080:8080` to create a secure tunnel from the host's `localhost` directly to the backend Service inside the cluster. We also updated the `CORS_ALLOWED_ORIGINS` environment variable in the backend deployment to explicitly trust the dynamic frontend URL provided by Minikube.

### Problem 3: Typo / Bad Image Name (`ErrImageNeverPull`)
* **Symptom:** When a typo was intentionally introduced into the backend Deployment's image name (`v999`), the new Pod failed to start, hanging in a `Pending` state.
* **Root Cause:** Kubernetes could not locate the specified image locally, and the pull policy forbade downloading it.
* **Resolution:** We used `kubectl describe pod <pod-name>` and investigated the `Events:` section at the bottom of the output. The events clearly stated: *Container image "exam-dashboard-backend:v999" is not present*. We fixed the YAML and re-applied it using `kubectl apply`.

### Problem 4: The Startup Race Condition (Database Readiness)
* **Symptom:** Backend Pods showed `RESTARTS: 1` shortly after deployment.
* **Root Cause:** The backend Spring Boot application starts faster than the PostgreSQL database initializes. The backend attempted to connect to the database before it was ready to accept connections, causing a fatal crash.
* **Resolution:** Kubernetes natively handled this via its self-healing loop. The backend crashed, Kubernetes marked it as failed, and automatically restarted the Pod. By the second attempt, the database was fully initialized, and the connection succeeded without manual intervention.
