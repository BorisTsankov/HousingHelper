import React from "react";
import { Navigate } from "react-router-dom";
import { useAuth } from "../../context/AuthContext";

interface Props {
  children: React.ReactElement;
}

const ProtectedRoute: React.FC<Props> = ({ children }) => {
  const { isAuthenticated, loading } = useAuth();

  if (loading) {
    return <div>Loading...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/register" replace />;
  }

  return children;
};

export default ProtectedRoute;
