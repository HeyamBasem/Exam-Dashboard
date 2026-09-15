import React from "react";
import { Link } from "react-router-dom";
import {
  Building2,
  FileEdit,
  Shield,
  ArrowRight,
  Sparkles,
  BarChart3,
  Users,
  CheckCircle2
} from "lucide-react";
import "./Landing.css";

const Landing = () => {
  return (
    <div className="landing">
      {/* Hero Section */}
      <section className="hero">
        <div className="hero-container">
          <div className="hero-content">
            <div className="hero-badge">
              <Sparkles size={14} />
              <span>Enterprise Grade Assessment</span>
            </div>
            <h1 className="hero-title">
              Assessments for modern institutions.
            </h1>
            <p className="hero-subtitle">
              Streamline your entire assessment lifecycle — from district
              management to student evaluation — with a secure, role-based
              testing platform.
            </p>
            <div className="hero-actions">
              <Link
                to="/register"
                className="btn-hero-primary"
              >
                Start for free <ArrowRight size={18} />
              </Link>
              <Link to="/login" className="btn-hero-secondary">
                Sign In
              </Link>
            </div>
          </div>
          
          <div className="hero-visual">
            {/* Dashboard UI Mock */}
            <div className="ui-mockup">
              <div className="mock-header">
                <div className="mock-search">Search assessments...</div>
                <div className="mock-avatar"></div>
              </div>
              <div className="mock-body">
                <div className="mock-stats">
                  <div className="mock-stat-card">
                    <span className="stat-label">Active Exams</span>
                    <span className="stat-value">24</span>
                  </div>
                  <div className="mock-stat-card">
                    <span className="stat-label">Avg. Score</span>
                    <span className="stat-value">87%</span>
                  </div>
                  <div className="mock-stat-card">
                    <span className="stat-label">Completion</span>
                    <span className="stat-value">94%</span>
                  </div>
                </div>
                <div className="mock-chart-area">
                  <div className="mock-chart-header">Recent Activity</div>
                  <div className="mock-bars">
                    <div className="mock-bar" style={{height: '40%'}}></div>
                    <div className="mock-bar" style={{height: '70%'}}></div>
                    <div className="mock-bar" style={{height: '50%'}}></div>
                    <div className="mock-bar" style={{height: '90%'}}></div>
                    <div className="mock-bar" style={{height: '60%'}}></div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="features-section">
        <div className="section-header">
          <h2 className="section-title">Built for scale.</h2>
          <p className="section-subtitle">
            Everything you need to run assessments securely.
          </p>
        </div>
        
        <div className="features-grid">
          <div className="feature-card">
            <div className="feature-icon-wrapper">
              <Building2 size={22} strokeWidth={1.5} />
            </div>
            <h3>District Management</h3>
            <p>
              Organize your entire institutional hierarchy with powerful admin
              tools and reporting access.
            </p>
          </div>
          <div className="feature-card">
            <div className="feature-icon-wrapper">
              <FileEdit size={22} strokeWidth={1.5} />
            </div>
            <h3>Intelligent Creation</h3>
            <p>
              Build comprehensive exams with advanced question types, logic, and automated grading.
            </p>
          </div>
          <div className="feature-card">
            <div className="feature-icon-wrapper">
              <Shield size={22} strokeWidth={1.5} />
            </div>
            <h3>Secure Access</h3>
            <p>
              Role-based secure environments tailored specifically for admins, teachers, and students.
            </p>
          </div>
        </div>
      </section>

      {/* Trust Section */}
      <section className="trust-section">
        <div className="trust-content">
          <h2 className="section-title">Trusted workflow.</h2>
          <div className="trust-list">
            <div className="trust-item">
              <CheckCircle2 className="trust-icon" size={20} />
              <span>Automated RBAC provisioning</span>
            </div>
            <div className="trust-item">
              <CheckCircle2 className="trust-icon" size={20} />
              <span>Real-time analytics and insights</span>
            </div>
            <div className="trust-item">
              <CheckCircle2 className="trust-icon" size={20} />
              <span>End-to-end encryption</span>
            </div>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Landing;
