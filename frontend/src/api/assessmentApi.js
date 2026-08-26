import api from "./axios";

export const assessmentApi = {
  /**
   * Step 1: Upload a CSV or PDF for preview
   * @param {File} file 
   * @returns {Promise<Object>} The preview response
   */
  uploadAssessmentFile: async (file) => {
    const formData = new FormData();
    formData.append("file", file);

    const response = await api.post("/assessments/import", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
    return response.data; // This matches the ApiResponse wrapper
  },

  /**
   * Step 2: Confirm and save the assessment
   * @param {Object} data The ConfirmImportRequest JSON
   * @returns {Promise<Object>} The saved assessment
   */
  confirmAssessmentImport: async (data) => {
    const response = await api.post("/assessments/import/confirm", data);
    return response.data;
  },

  /**
   * Week 3 CRUD Operations
   */
  getAllAssessments: async (page = 0, size = 10) => {
    const response = await api.get(`/assessments?page=${page}&size=${size}`);
    return response.data;
  },
  
  getAssessmentById: async (id) => {
    const response = await api.get(`/assessments/${id}`);
    return response.data;
  },
  
  createAssessment: async (data) => {
    const response = await api.post("/assessments", data);
    return response.data;
  },
  
  updateAssessment: async (id, data) => {
    const response = await api.put(`/assessments/${id}`, data);
    return response.data;
  },
  
  deleteAssessment: async (id) => {
    const response = await api.delete(`/assessments/${id}`);
    return response;
  },
};
