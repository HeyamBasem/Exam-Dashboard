import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { BrowserRouter } from 'react-router-dom';
import { describe, it, expect, vi, beforeEach } from 'vitest';
import ImportAssessment from '../ImportAssessment';
import { assessmentApi } from '../../api/assessmentApi';

// Mock the API client and router navigation
vi.mock('../../api/assessmentApi', () => ({
  assessmentApi: {
    uploadAssessmentFile: vi.fn(),
    confirmAssessmentImport: vi.fn(),
  },
}));

const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate,
  };
});

describe('ImportAssessment Component', () => {
  
  beforeEach(() => {
    vi.clearAllMocks();
  });

  const renderComponent = () => {
    return render(
      <BrowserRouter>
        <ImportAssessment />
      </BrowserRouter>
    );
  };

  it('renders the initial upload state correctly', () => {
    renderComponent();
    
    // Check for main title
    expect(screen.getByText('Import Assessment')).toBeInTheDocument();
    
    // Check for upload section
    expect(screen.getByText('Upload a CSV or PDF File')).toBeInTheDocument();
    
    // Check if the upload button is disabled initially
    const uploadButton = screen.getByRole('button', { name: /upload & preview/i });
    expect(uploadButton).toBeDisabled();
  });

  it('enables the upload button when a file is selected', async () => {
    renderComponent();
    
    // Find the file input
    const fileInput = screen.getByLabelText(/choose file/i);
    
    // Create a mock file
    const file = new File(['dummy content'], 'test.csv', { type: 'text/csv' });
    
    // Simulate file selection
    fireEvent.change(fileInput, { target: { files: [file] } });
    
    // The button should now be enabled and the label should show the filename
    const uploadButton = screen.getByRole('button', { name: /upload & preview/i });
    expect(uploadButton).not.toBeDisabled();
    expect(screen.getByText('test.csv')).toBeInTheDocument();
  });

  it('transitions to preview state on successful upload', async () => {
    // Mock a successful API response
    assessmentApi.uploadAssessmentFile.mockResolvedValueOnce({
      data: {
        title: "Parsed Title",
        totalQuestions: 1,
        questions: [
          { questionText: "What is 2+2?", questionType: "MULTIPLE_CHOICE", correctAnswer: "C" }
        ]
      }
    });

    renderComponent();
    
    // Select file
    const fileInput = screen.getByLabelText(/choose file/i);
    const file = new File(['content'], 'test.csv', { type: 'text/csv' });
    fireEvent.change(fileInput, { target: { files: [file] } });
    
    // Click upload
    const uploadButton = screen.getByRole('button', { name: /upload & preview/i });
    fireEvent.click(uploadButton);
    
    // Wait for the component to transition to the preview state
    await waitFor(() => {
      expect(screen.getByText('Assessment Details')).toBeInTheDocument();
    });

    // Check if parsed questions are rendered
    expect(screen.getByText('Extracted Questions (1)')).toBeInTheDocument();
    expect(screen.getByText('What is 2+2?')).toBeInTheDocument();
  });
});
