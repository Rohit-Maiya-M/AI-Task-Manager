import { Navigate } from "react-router-dom";

const ProtectedRoute = ({ children }) => {
  const token = localStorage.getItem("token");

  // REINFORCED CHECK:
  // 1. Must exist
  // 2. Must not be the literal string "null" or "undefined"
  // 3. Must be longer than a few characters (JWTs are usually 100+)
  const isAuthenticated = 
    token && 
    token !== "null" && 
    token !== "undefined" && 
    token.length > 20; 

  if (!isAuthenticated) {
    console.log("Access Denied: Redirecting to login...");
    return <Navigate to="/" replace />;
  }

  return children;
};

export default ProtectedRoute;