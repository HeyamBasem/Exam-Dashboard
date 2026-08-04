import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Eye, EyeOff, Mail, Lock, User, ChevronDown, Check, Loader2 } from 'lucide-react';
import { AuthContext } from '../auth/AuthContext';
import './Login.css'; 
import './Register.css'; 

const Register = () => {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    password: '',
    role: 'STUDENT'
  });
  const [showPassword, setShowPassword] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  
  const navigate = useNavigate();
  const { register } = React.useContext(AuthContext);

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const getPasswordStrength = (password) => {
    let score = 0;
    if (!password) return 0;
    if (password.length > 8) score += 1;
    if (/[a-z]/.test(password) && /[A-Z]/.test(password)) score += 1;
    if (/\d/.test(password)) score += 1;
    if (/[^a-zA-Z\d]/.test(password)) score += 1;
    if (password.length >= 12) score += 1;
    return score;
  };

  const strength = getPasswordStrength(formData.password);
  
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setSuccess('');
    
    if (formData.password.length < 6) {
      setError('Password must be at least 6 characters long');
      return;
    }

    setLoading(true);

    try {
      await register(formData);
      setSuccess('Account created successfully! Redirecting...');
      setTimeout(() => {
        navigate('/login');
      }, 2000);
    } catch (err) {
      if (err.response?.data?.errors && err.response.data.errors.length > 0) {
        // If there are specific field errors, show the first one or combine them
        const firstError = err.response.data.errors[0];
        setError(`${firstError.field}: ${firstError.message}`);
      } else {
        setError(err.response?.data?.message || 'Registration failed. Please try again.');
      }
    } finally {
      setLoading(false);
    }
  };

  const hasLength = formData.password.length >= 6;
  const hasLetterAndNumber = /[a-zA-Z]/.test(formData.password) && /\d/.test(formData.password);

  return (
    <div className="auth-page">
      <div className="auth-card register-card">
        <div className="auth-header">
          <div className="auth-logo">
            <User size={24} />
          </div>
          <h2>Create your account</h2>
          <p>Start your journey with ExamDash</p>
        </div>
        
        {error && <div className="auth-error-banner">{error}</div>}
        {success && <div className="auth-success-banner">{success}</div>}

        <form className="auth-form" onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label>First Name</label>
              <div className="input-wrapper">
                <User className="input-icon" size={18} />
                <input
                  type="text"
                  name="firstName"
                  value={formData.firstName}
                  onChange={handleChange}
                  placeholder="First name"
                  required
                />
              </div>
            </div>
            
            <div className="form-group">
              <label>Last Name</label>
              <div className="input-wrapper">
                <User className="input-icon" size={18} />
                <input
                  type="text"
                  name="lastName"
                  value={formData.lastName}
                  onChange={handleChange}
                  placeholder="Last name"
                  required
                />
              </div>
            </div>
          </div>

          <div className="form-group">
            <label>Email</label>
            <div className="input-wrapper">
              <Mail className="input-icon" size={18} />
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
                placeholder="you@company.com"
                required
              />
            </div>
          </div>
          
          <div className="form-group">
            <label>Role</label>
            <div className="input-wrapper select-wrapper">
              <ChevronDown className="select-icon-right" size={18} />
              <select
                name="role"
                value={formData.role}
                onChange={handleChange}
              >
                <option value="STUDENT">Student</option>
                <option value="TEACHER">Teacher</option>
                <option value="ADMIN">Admin</option>
              </select>
            </div>
          </div>

          <div className="form-group">
            <label>Password</label>
            <div className="input-wrapper">
              <Lock className="input-icon" size={18} />
              <input
                type={showPassword ? "text" : "password"}
                name="password"
                value={formData.password}
                onChange={handleChange}
                placeholder="Create a password"
                required
              />
              <button 
                type="button" 
                className="btn-toggle-password"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
            {formData.password && (
              <div className="password-strength">
                <div className="strength-bars">
                  <div className={`strength-bar-segment ${strength >= 1 ? 'active s1' : ''}`}></div>
                  <div className={`strength-bar-segment ${strength >= 2 ? 'active s2' : ''}`}></div>
                  <div className={`strength-bar-segment ${strength >= 3 ? 'active s3' : ''}`}></div>
                  <div className={`strength-bar-segment ${strength >= 4 ? 'active s4' : ''}`}></div>
                </div>
                <div className="password-reqs">
                  <span className={`req-item ${hasLength ? "req-met" : ""}`}>
                    <Check size={14} /> 6+ chars
                  </span>
                  <span className={`req-item ${hasLetterAndNumber ? "req-met" : ""}`}>
                    <Check size={14} /> Letter & Number
                  </span>
                </div>
              </div>
            )}
          </div>

          <button type="submit" className="btn-submit" disabled={loading || success}>
            {loading ? <Loader2 className="spin" size={18} /> : null}
            {loading ? 'Creating Account...' : 'Create Account'}
          </button>
        </form>

        <div className="auth-footer">
          Already have an account? <Link to="/login">Sign in</Link>
        </div>
      </div>
    </div>
  );
};

export default Register;
