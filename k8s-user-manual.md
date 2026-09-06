# Kubernetes User Manual

This manual provides the everyday commands you need to manage your local Kubernetes deployment.

## 1. Start the Environment (Daily Routine)

When you sit down to work on the project, run these commands in order:

**Step 1: Start the cluster**

```powershell
minikube start
```

**Step 2: Deploy the application**
_(Note: If you didn't delete the app yesterday, it might already be running. Running this again is safe and will just say "unchanged" if it's already there)._

```powershell
kubectl apply -f 'D:\ASPIRE Training\Exam-Dashboard\k8s'
```

**Step 3: Open the tunnel for the Backend (Leave this terminal running)**
Because the React frontend runs in your browser but tries to talk to `localhost:8080`, you must port-forward the backend service to your local machine:

```powershell
kubectl port-forward service/backend 8080:8080
```

**Step 4: Open the Frontend (Open a new terminal for this)**

```powershell
minikube service frontend
```

_(This will automatically open the React app in your default web browser)._

---

## 2. Check the Status

If something isn't working, use these commands to check the system's health:

**Check if all Pods are running:**

```powershell
kubectl get pods
```

**Check the Services and Ports:**

```powershell
kubectl get services
```

**Read the logs of a specific Pod:**

```powershell
kubectl logs <insert-pod-name-here>
```

---

## 3. Stop the Environment (End of Day)

When you are done working for the day, you can pause the virtual machine to save RAM.

```powershell
minikube stop
```

_(Your database data and deployments are safely saved. You do NOT need to run `kubectl apply` again tomorrow)._

---

## 4. Wipe the Application (Clean Slate)

If you messed up your YAML files, or want to completely destroy the database and application to start from scratch, run this:

```powershell
kubectl delete -f 'D:\ASPIRE Training\Exam-Dashboard\k8s'
```

_(This deletes the Deployments, Services, Secrets, and Database Storage. You will need to run `kubectl apply` next time to rebuild it)._

---

## 5. Update the Application (After rebuilding a Docker image)

If you change your Java or React code, you need to update Kubernetes:

1. Build your new Docker image:
   ```powershell
   docker build -t exam-dashboard-backend:v1 ./backend
   ```
2. Load the new image into Minikube:
   ```powershell
   minikube image load exam-dashboard-backend:v1
   ```
3. Tell Kubernetes to restart the Pods to pick up the new image:
   ```powershell
   kubectl rollout restart deployment/backend
   ```
