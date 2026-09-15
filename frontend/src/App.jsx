import React from "react";
import { Routes, Route } from "react-router-dom";
import { AuthProvider } from "./auth/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import Navbar from "./components/Navbar";
import Footer from "./components/Footer";
import Landing from "./pages/Landing";
import Login from "./pages/Login";
import Register from "./pages/Register";
import Dashboard from "./pages/Dashboard";
import ImportAssessment from "./pages/ImportAssessment";
import AssessmentsList from "./pages/AssessmentsList";
import AssessmentForm from "./pages/AssessmentForm";
import AssessmentDetail from "./pages/AssessmentDetail";
import "./App.css";

const App = () => {
  return (
    <AuthProvider>
      <div className="app-container">
        <Navbar />
        <main className="main-content">
          <Routes>
            <Route path="/" element={<Landing />} />
            <Route path="/login" element={<Login />} />
            <Route path="/register" element={<Register />} />
            <Route
              path="/dashboard"
              element={
                <ProtectedRoute>
                  <Dashboard />
                </ProtectedRoute>
              }
            />
            <Route
              path="/import"
              element={
                <ProtectedRoute>
                  <ImportAssessment />
                </ProtectedRoute>
              }
            />
            <Route
              path="/assessments"
              element={
                <ProtectedRoute>
                  <AssessmentsList />
                </ProtectedRoute>
              }
            />
            <Route
              path="/assessments/new"
              element={
                <ProtectedRoute>
                  <AssessmentForm />
                </ProtectedRoute>
              }
            />
            <Route
              path="/assessments/:id"
              element={
                <ProtectedRoute>
                  <AssessmentDetail />
                </ProtectedRoute>
              }
            />
            <Route
              path="/assessments/:id/edit"
              element={
                <ProtectedRoute>
                  <AssessmentForm />
                </ProtectedRoute>
              }
            />
          </Routes>
        </main>
        <Footer />
      </div>
    </AuthProvider>
  );
};

export default App;
