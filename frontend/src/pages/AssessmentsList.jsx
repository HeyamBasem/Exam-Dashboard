import React, { useState, useEffect, useContext } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { Plus, Edit2, Trash2, FileText, UploadCloud, Search } from 'lucide-react';
import { assessmentApi } from '../api/assessmentApi';
import { AuthContext } from '../auth/AuthContext';
import './AssessmentsList.css';

const AssessmentsList = () => {
  const [assessments, setAssessments] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  
  const { user } = useContext(AuthContext);
  const navigate = useNavigate();

  const fetchAssessments = async () => {
    try {
      setLoading(true);
      const res = await assessmentApi.getAllAssessments();
      // The API returns ApiResponse<PagedResponse<AssessmentResponse>>
      setAssessments(res.data?.content || []);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load assessments');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchAssessments();
  }, []);

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this assessment?')) {
      try {
        await assessmentApi.deleteAssessment(id);
        setAssessments(assessments.filter(a => a.id !== id));
      } catch (err) {
        alert(err.response?.data?.message || 'Failed to delete assessment');
      }
    }
  };

  const canEdit = (assessment) => {
    if (user?.role === 'ADMIN') return true;
    // For teachers, we'd normally check if they created it, 
    // but the backend delete/put endpoints currently restrict via roles/permissions.
    return user?.role === 'TEACHER';
  };

  return (
    <div className="list-container">
      <div className="list-header">
        <div>
          <h2>Assessments Library</h2>
          <p>Manage and view all your assessments</p>
        </div>
        {(user?.role === 'ADMIN' || user?.role === 'TEACHER') && (
          <div className="header-actions">
            <button className="btn-secondary" onClick={() => navigate('/import')}>
              <UploadCloud size={16} /> Import
            </button>
            <button className="btn-primary" onClick={() => navigate('/assessments/new')}>
              <Plus size={16} /> Create Manual
            </button>
          </div>
        )}
      </div>

      {error && <div className="error-banner">{error}</div>}

      {loading ? (
        <div className="loading-state">Loading assessments...</div>
      ) : assessments.length === 0 ? (
        <div className="empty-state">
          <FileText size={48} className="empty-icon" />
          <h3>No Assessments Found</h3>
          <p>Get started by creating or importing your first assessment.</p>
        </div>
      ) : (
        <div className="table-responsive">
          <table className="data-table">
            <thead>
              <tr>
                <th>Title</th>
                <th>Subject</th>
                <th>Grade</th>
                <th>Duration</th>
                <th>Marks</th>
                <th>Actions</th>
              </tr>
            </thead>
            <tbody>
              {assessments.map(assessment => (
                <tr key={assessment.id}>
                  <td className="font-medium">{assessment.title}</td>
                  <td><span className="badge-subject">{assessment.subject}</span></td>
                  <td>Grade {assessment.grade}</td>
                  <td>{assessment.durationMinutes} min</td>
                  <td>{assessment.totalMarks}</td>
                  <td>
                    <div className="action-cell">
                      <button 
                        className="btn-icon view"
                        onClick={() => navigate(`/assessments/${assessment.id}`)}
                        title="View Details"
                      >
                        <Search size={16} />
                      </button>
                      {canEdit(assessment) && (
                        <>
                          <button 
                            className="btn-icon edit"
                            onClick={() => navigate(`/assessments/${assessment.id}/edit`)}
                            title="Edit"
                          >
                            <Edit2 size={16} />
                          </button>
                          <button 
                            className="btn-icon delete"
                            onClick={() => handleDelete(assessment.id)}
                            title="Delete"
                          >
                            <Trash2 size={16} />
                          </button>
                        </>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </div>
  );
};

export default AssessmentsList;
