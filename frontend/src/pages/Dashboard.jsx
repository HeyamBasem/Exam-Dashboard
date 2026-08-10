import React from "react";
import { LayoutDashboard, LogOut, Shield, BookOpen, Users, Clock, Plus } from "lucide-react";
import { AuthContext } from "../auth/AuthContext";
import "./Dashboard.css";

const Dashboard = () => {
  const { user, logout } = React.useContext(AuthContext);

  const getRoleIcon = () => {
    switch (user?.role) {
      case "ADMIN":
        return <Shield size={16} />;
      case "TEACHER":
        return <BookOpen size={16} />;
      case "STUDENT":
        return <Users size={16} />;
      default:
        return <Users size={16} />;
    }
  };

  return (
    <div className="dashboard">
      <div className="dashboard-header">
        <div className="header-content">
          <div>
            <h1 className="welcome-title">Welcome back, {user?.firstName}</h1>
            <p className="welcome-email">{user?.email}</p>
          </div>
          <div className="header-actions">
            <div className={`role-badge role-${user?.role || "STUDENT"}`}>
              {getRoleIcon()}
              {user?.role || "STUDENT"}
            </div>
            <button className="btn-header-primary">
              <Plus size={16} /> New Assessment
            </button>
          </div>
        </div>
      </div>

      <div className="dashboard-content">
        <div className="metrics-grid">
          <div className="metric-card">
            <div className="metric-header">
              <LayoutDashboard size={20} className="metric-icon" />
              <span>Total Exams</span>
            </div>
            <div className="metric-value">12</div>
          </div>
          <div className="metric-card">
            <div className="metric-header">
              <BookOpen size={20} className="metric-icon" />
              <span>Completed</span>
            </div>
            <div className="metric-value">8</div>
          </div>
          <div className="metric-card">
            <div className="metric-header">
              <Clock size={20} className="metric-icon" />
              <span>Pending</span>
            </div>
            <div className="metric-value">4</div>
          </div>
        </div>

        <div className="coming-soon-grid">
          <div className="coming-soon-card">
            <div className="card-icon-wrapper">
              <LayoutDashboard size={24} />
            </div>
            <div className="card-info">
              <h4>District Management</h4>
              <span>Feature coming soon</span>
            </div>
          </div>
          <div className="coming-soon-card">
            <div className="card-icon-wrapper">
              <BookOpen size={24} />
            </div>
            <div className="card-info">
              <h4>Assessments Builder</h4>
              <span>Feature coming soon</span>
            </div>
          </div>
          <div className="coming-soon-card">
            <div className="card-icon-wrapper">
              <Users size={24} />
            </div>
            <div className="card-info">
              <h4>Student Management</h4>
              <span>Feature coming soon</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
