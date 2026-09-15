import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { assessmentApi } from '../api/assessmentApi';
import { ArrowLeft, Edit2, Clock, Award, BookOpen } from 'lucide-react';
import './ImportAssessment.css'; 

const AssessmentDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [assessment, setAssessment] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchAssessment = async () => {
      try {
        const res = await assessmentApi.getAssessmentById(id);
        setAssessment(res.data);
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to load assessment details');
      } finally {
        setLoading(false);
      }
    };
    fetchAssessment();
  }, [id]);

  if (loading) return <div className="loading-state">Loading...</div>;
  if (error) return <div className="import-container"><div className="error-banner">{error}</div></div>;
  if (!assessment) return null;

  return (
    <div className="import-container">
      <div className="import-header">
        <button className="btn-back" onClick={() => navigate('/assessments')}>
          <ArrowLeft size={18} /> Back
        </button>
        <div style={{display: 'flex', gap: '1rem', alignItems: 'center'}}>
          <h2>{assessment.title}</h2>
          <button className="btn-secondary" onClick={() => navigate(`/assessments/${id}/edit`)}>
            <Edit2 size={16} /> Edit
          </button>
        </div>
      </div>

      <div className="metadata-card" style={{display: 'flex', gap: '2rem', flexWrap: 'wrap'}}>
        <div style={{flex: '1', minWidth: '200px'}}>
          <p style={{color: '#64748b', marginBottom: '8px'}}>Subject</p>
          <div style={{display: 'flex', alignItems: 'center', gap: '8px', fontWeight: '500'}}>
            <BookOpen size={18} color="#3b82f6" /> {assessment.subject} (Grade {assessment.grade})
          </div>
        </div>
        <div style={{flex: '1', minWidth: '200px'}}>
          <p style={{color: '#64748b', marginBottom: '8px'}}>Duration</p>
          <div style={{display: 'flex', alignItems: 'center', gap: '8px', fontWeight: '500'}}>
            <Clock size={18} color="#f59e0b" /> {assessment.durationMinutes} Minutes
          </div>
        </div>
        <div style={{flex: '1', minWidth: '200px'}}>
          <p style={{color: '#64748b', marginBottom: '8px'}}>Total Marks</p>
          <div style={{display: 'flex', alignItems: 'center', gap: '8px', fontWeight: '500'}}>
            <Award size={18} color="#22c55e" /> {assessment.totalMarks} Points
          </div>
        </div>
        {assessment.description && (
          <div style={{width: '100%', marginTop: '1rem', paddingTop: '1rem', borderTop: '1px solid #e2e8f0'}}>
            <p style={{color: '#475569'}}>{assessment.description}</p>
          </div>
        )}
      </div>

      <div className="questions-preview">
        <h3>Questions</h3>
        {(!assessment.questions || assessment.questions.length === 0) ? (
          <p style={{color: '#64748b'}}>No questions added to this assessment yet.</p>
        ) : (
          <div className="table-responsive">
            <table className="preview-table">
              <thead>
                <tr>
                  <th>#</th>
                  <th>Question</th>
                  <th>Type</th>
                  <th>Correct Answer</th>
                </tr>
              </thead>
              <tbody>
                {assessment.questions.map((q, idx) => (
                  <tr key={idx}>
                    <td>{idx + 1}</td>
                    <td>{q.questionText}</td>
                    <td><span className={`badge ${q.questionType.toLowerCase()}`}>{q.questionType}</span></td>
                    <td><strong>{q.correctAnswer}</strong></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  );
};

export default AssessmentDetail;
