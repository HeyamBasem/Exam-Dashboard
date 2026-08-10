import React from "react";
import "./LoadingSpinner.css";

export const LoadingSpinner = () => (
  <div className="spinner-wrapper" data-testid="loading-spinner">
    <div className="spinner" />
  </div>
);
