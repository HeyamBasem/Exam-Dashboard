import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { assessmentApi } from '../api/assessmentApi';
import { ArrowLeft, AlertCircle } from 'lucide-react';
import './ImportAssessment.css'; // Reusing forms styles

const AssessmentForm = () => {
  const { id } = useParams();
  const isEditing = !!id;
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    subject: '',
    grade: '',
    totalMarks: '',
    durationMinutes: ''
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (isEditing) {
      loadAssessment();
    }
  }, [id]);

  const loadAssessment = async () => {
    try {
      setLoading(true);
      const res = await assessmentApi.getAssessmentById(id);
      const data = res.data;
      setFormData({
        title: data.title || '',
        description: data.description || '',
        subject: data.subject || '',
        grade: data.grade || '',
        totalMarks: data.totalMarks || '',
        durationMinutes: data.durationMinutes || ''
      });
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load assessment details');
    } finally {
      setLoading(false);
    }
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);
    
    const payload = {
      title: formData.title,
      description: formData.description,
      subject: formData.subject,
      grade: parseInt(formData.grade, 10),
      totalMarks: parseInt(formData.totalMarks, 10),
      durationMinutes: parseInt(formData.durationMinutes, 10)
    };

    try {
      if (isEditing) {
        await assessmentApi.updateAssessment(id, payload);
      } else {
        await assessmentApi.createAssessment(payload);
      }
      navigate('/assessments');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save assessment');
      setLoading(false);
    }
  };

  return (
    <div className="import-container">
      <div className="import-header">
        <button className="btn-back" onClick={() => navigate('/assessments')}>
          <ArrowLeft size={18} /> Back
        </button>
        <h2>{isEditing ? 'Edit Assessment' : 'Create New Assessment'}</h2>
      </div>

      <div className="metadata-card">
        <h3>Assessment Details</h3>
        <form onSubmit={handleSubmit} className="metadata-form">
          <div className="form-group">
            <label>Title *</label>
            <input type="text" name="title" value={formData.title} onChange={handleChange} required />
          </div>
          <div className="form-group">
            <label>Description</label>
            <textarea name="description" value={formData.description} onChange={handleChange} rows="2" />
          </div>
          <div className="form-group">
            <label>Subject *</label>
            <input type="text" name="subject" value={formData.subject} onChange={handleChange} required />
          </div>
          <div className="form-row">
            <div className="form-group">
              <label>Grade *</label>
              <input type="number" name="grade" min="1" value={formData.grade} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Total Marks *</label>
              <input type="number" name="totalMarks" min="1" value={formData.totalMarks} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Duration (mins) *</label>
              <input type="number" name="durationMinutes" min="1" max="480" value={formData.durationMinutes} onChange={handleChange} required />
            </div>
          </div>
          
          {error && <div className="error-banner"><AlertCircle size={16} /> {error}</div>}
          
          <div className="action-buttons">
            <button type="button" className="btn-secondary" onClick={() => navigate('/assessments')}>Cancel</button>
            <button type="submit" className="btn-primary" disabled={loading}>
              {loading ? 'Saving...' : 'Save Assessment'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AssessmentForm;
