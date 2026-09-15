import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { UploadCloud, CheckCircle, AlertCircle, ArrowLeft, FileText } from "lucide-react";
import { assessmentApi } from "../api/assessmentApi";
import "./ImportAssessment.css";

const ImportAssessment = () => {
  const navigate = useNavigate();
  
  // Phase 1: Upload
  const [file, setFile] = useState(null);
  const [isUploading, setIsUploading] = useState(false);
  const [uploadError, setUploadError] = useState("");
  
  // Phase 2: Preview & Confirm
  const [previewData, setPreviewData] = useState(null);
  const [metadata, setMetadata] = useState({
    title: "",
    description: "",
    subject: "",
    grade: "",
    totalMarks: "",
    durationMinutes: "",
  });
  const [isConfirming, setIsConfirming] = useState(false);
  const [confirmError, setConfirmError] = useState("");
  const [success, setSuccess] = useState(false);

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
      setUploadError("");
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!file) {
      setUploadError("Please select a file to upload.");
      return;
    }
    
    try {
      setIsUploading(true);
      setUploadError("");
      const response = await assessmentApi.uploadAssessmentFile(file);
      setPreviewData(response.data);
      setMetadata({ ...metadata, title: response.data.title || "" });
    } catch (error) {
      setUploadError(error.response?.data?.message || "Failed to upload file");
    } finally {
      setIsUploading(false);
    }
  };

  const handleMetadataChange = (e) => {
    const { name, value } = e.target;
    setMetadata({ ...metadata, [name]: value });
  };

  const handleConfirm = async (e) => {
    e.preventDefault();
    try {
      setIsConfirming(true);
      setConfirmError("");
      
      const payload = {
        title: metadata.title,
        description: metadata.description,
        subject: metadata.subject,
        grade: parseInt(metadata.grade, 10),
        totalMarks: parseInt(metadata.totalMarks, 10),
        durationMinutes: parseInt(metadata.durationMinutes, 10),
        questions: previewData.questions
      };

      await assessmentApi.confirmAssessmentImport(payload);
      setSuccess(true);
      setTimeout(() => navigate("/dashboard"), 2000);
    } catch (error) {
      setConfirmError(error.response?.data?.message || "Failed to save assessment");
    } finally {
      setIsConfirming(false);
    }
  };

  if (success) {
    return (
      <div className="import-container success-state">
        <CheckCircle size={64} className="success-icon" />
        <h2>Assessment Saved Successfully!</h2>
        <p>Redirecting to dashboard...</p>
      </div>
    );
  }

  return (
    <div className="import-container">
      <div className="import-header">
        <button className="btn-back" onClick={() => navigate("/dashboard")}>
          <ArrowLeft size={18} /> Back
        </button>
        <h2>Import Assessment</h2>
      </div>

      {!previewData ? (
        <div className="upload-card">
          <div className="upload-icon-wrapper">
            <UploadCloud size={48} />
          </div>
          <h3>Upload a CSV or PDF File</h3>
          <p>The file should contain questions formatted correctly for the parser.</p>
          
          <form onSubmit={handleUpload} className="upload-form">
            <input 
              type="file" 
              accept=".csv,.pdf,application/pdf,text/csv" 
              onChange={handleFileChange} 
              className="file-input"
              id="file-upload"
            />
            <label htmlFor="file-upload" className="file-label">
              {file ? <><FileText size={16}/> {file.name}</> : "Choose File"}
            </label>
            
            {uploadError && <div className="error-banner"><AlertCircle size={16} /> {uploadError}</div>}
            
            <button 
              type="submit" 
              className="btn-primary" 
              disabled={!file || isUploading}
            >
              {isUploading ? "Uploading..." : "Upload & Preview"}
            </button>
          </form>
        </div>
      ) : (
        <div className="preview-container">
          <div className="metadata-card">
            <h3>Assessment Details</h3>
            <form onSubmit={handleConfirm} className="metadata-form">
              <div className="form-group">
                <label>Title *</label>
                <input type="text" name="title" value={metadata.title} onChange={handleMetadataChange} required />
              </div>
              <div className="form-group">
                <label>Description</label>
                <textarea name="description" value={metadata.description} onChange={handleMetadataChange} rows="2" />
              </div>
              <div className="form-group">
                <label>Subject *</label>
                <input type="text" name="subject" value={metadata.subject} onChange={handleMetadataChange} required />
              </div>
              <div className="form-row">
                <div className="form-group">
                  <label>Grade *</label>
                  <input type="number" name="grade" min="1" value={metadata.grade} onChange={handleMetadataChange} required />
                </div>
                <div className="form-group">
                  <label>Total Marks *</label>
                  <input type="number" name="totalMarks" min="1" value={metadata.totalMarks} onChange={handleMetadataChange} required />
                </div>
                <div className="form-group">
                  <label>Duration (mins) *</label>
                  <input type="number" name="durationMinutes" min="1" max="480" value={metadata.durationMinutes} onChange={handleMetadataChange} required />
                </div>
              </div>
              
              {confirmError && <div className="error-banner"><AlertCircle size={16} /> {confirmError}</div>}
              
              <div className="action-buttons">
                <button type="button" className="btn-secondary" onClick={() => setPreviewData(null)}>Cancel</button>
                <button type="submit" className="btn-primary" disabled={isConfirming}>
                  {isConfirming ? "Saving..." : "Confirm & Save"}
                </button>
              </div>
            </form>
          </div>

          <div className="questions-preview">
            <h3>Extracted Questions ({previewData.totalQuestions})</h3>
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
                  {previewData.questions.map((q, idx) => (
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
          </div>
        </div>
      )}
    </div>
  );
};

export default ImportAssessment;
