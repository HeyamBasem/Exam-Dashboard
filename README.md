# AssessmentPlatform
ExamDash is an enterprise-grade assessment management platform built to streamline the entire testing lifecycle. From district-wide management to individual student evaluations, ExamDash provides a secure, role-based ecosystem that eliminates the friction of traditional testing methods.

## ✨ Features
- **Role-Based Access Control (RBAC):** Dedicated portals for Admins, Teachers, and Students.
- **Secure Authentication:** JWT-based stateless authentication and secure credential management.
- **Dashboard Analytics:** Quick overview of total exams, completed assessments, and pending tasks.
- **Assessment Builder:** Import assessments via CSV/PDF file parsing.
- **Modern UI:** Sleek, responsive, and intuitive interface built with React and Vite.
## 🛠️ Tech Stack
- **Frontend:** React 19, Vite, React Router, Nginx
- **Backend:** Spring Boot (Java 21), Spring Security, RESTful APIs
- **Database:** PostgreSQL 15
- **Infrastructure:** Docker, Docker Compose, Kubernetes (Minikube)



## 🎯 Purpose & Vision
Educational institutions often struggle with fragmented tools for creating exams, tracking student performance, and managing school districts. ExamDash solves this by providing a single, unified platform where:
- **Educators** can easily author or import assessments and monitor class performance in real-time.
- **Students** experience a secure, distraction-free environment to take their exams.
- **Administrators** gain high-level oversight over district management and role assignments.
By automating the administrative heavy lifting, ExamDash allows teachers to focus on what matters most: student success.
---
## 🧑‍💻 Core Workflows & Usage
### For Teachers & Instructors
* **Dashboard Overview:** Instantly see active exams, average scores, and completion rates.
* **Assessment Builder:** Manually author questions or bulk-import existing tests via CSV and PDF parsers.
* **Analytics:** Track recent activity and identify areas where students are struggling.
### For Students
* **Seamless Testing:** Log in to a clean, intuitive portal to view pending assessments.
* **Instant Feedback:** (Coming Soon) View grades and performance metrics immediately upon exam completion.
### For Administrators
* **District Management:** Oversee multiple schools, user roles, and system-wide metrics from a centralized control panel.
---
## API Documentation
The API documentation is powered by Swagger UI and OpenAPI 3.0.

To view and interact with the API documentation:
1. Start the Spring Boot application (`mvn spring-boot:run`).
2. Open your browser and navigate to: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
3. To test protected Assessment endpoints:
   - Use the existing Authentication API (`POST /api/v1/auth/login`) to obtain a JWT.
   - Click the **Authorize** button at the top of the Swagger UI.
   - Paste your JWT token and click Authorize.
   - You can now test the Assessment endpoints securely.

 ---
 ## Getting Started
You can run this project locally using either **Docker Compose** (recommended for quick testing) or **Kubernetes** (full production-style architecture).
### Prerequisites
- [Docker Desktop](https://www.docker.com/products/docker-desktop)
- [Minikube](https://minikube.sigs.k8s.io/docs/start/) & [kubectl](https://kubernetes.io/docs/tasks/tools/) (for the Kubernetes deployment)
---
### Option A: Run with Docker Compose
1. Clone the repository and navigate to the project root:
   ```bash
   cd Exam-Dashboard
   ```
2. Build and start the containers in detached mode:
   ```bash
   docker compose up --build -d
   ```
3. Open your browser and navigate to:
   ```text
   http://localhost:3000
   ```
4. To stop the environment:
   ```bash
   docker compose down
   ```
---
### Option B: Run with Kubernetes (Minikube)
1. Start your local Kubernetes cluster:
   ```bash
   minikube start
   ```
2. Build the Docker images locally:
   ```bash
   docker build -t exam-dashboard-backend:v1 ./backend
   docker build -t exam-dashboard-frontend:v1 ./frontend
   ```
3. Load the images into Minikube's isolated environment so the cluster can access them:
   ```bash
   minikube image load exam-dashboard-backend:v1
   minikube image load exam-dashboard-frontend:v1
   ```
4. Deploy the infrastructure (Deployments, Services, Secrets, and PVCs):
   ```bash
   kubectl apply -f k8s/
   ```
5. Set up port-forwarding for the backend API (leave this running in a terminal). This is required because the React frontend runs in your local browser and needs to securely communicate with the backend inside the cluster:
   ```bash
   kubectl port-forward service/backend 8080:8080
   ```
6. Open a new terminal and access the frontend application:
   ```bash
   minikube service frontend
   ```
7. To tear down the application (while keeping Minikube running):
   ```bash
   kubectl delete -f k8s/
   ```
---
