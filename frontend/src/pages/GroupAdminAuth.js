import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { FaLock, FaKey, FaArrowLeft } from "react-icons/fa";
import api from "../utils/api";
import "./GroupAuthPage.css";

export default function GroupAdminAuth() {
  const navigate = useNavigate();
  const [groupId, setGroupId] = useState("");
  const [groupPassword, setGroupPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");

  const handleGroupLogin = async (e) => {
    e.preventDefault();
    if (!groupId || !groupPassword) {
      setError("Please input both the Group ID and Admin Password.");
      return;
    }

    try {
      setIsSubmitting(true);
      setError("");

      // Dynamic validation check hitting your backend controller architecture
      await api.post("/group/admin/login", {
        groupId: groupId,
        password: groupPassword
      });

      // Clear checkpoint passed -> load up dashboard with specific group reference
      navigate(`/group/admin?groupId=${groupId}`);
    } catch (err) {
      console.error(err);
      setError(err.response?.data?.message || "Invalid Group ID or Admin Password.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="group-auth-container">
      <button className="back-btn" onClick={() => navigate("/group/admin-choice")}>
        <FaArrowLeft /> Back
      </button>

      <div className="auth-card">
        <div className="auth-header">
          <FaLock className="lock-icon" />
          <h2>Group Admin Gateway</h2>
          <p>Provide workspace credentials to access your administrative tools.</p>
        </div>

        {error && <div className="auth-error">{error}</div>}

        <form onSubmit={handleGroupLogin} className="auth-form">
          <div className="input-group">
            <label><FaKey /> Group ID</label>
            <input 
              type="number" 
              placeholder="e.g., 1024"
              value={groupId}
              onChange={(e) => setGroupId(e.target.value)}
              disabled={isSubmitting}
            />
          </div>

          <div className="input-group">
            <label><FaLock /> Administrative Password</label>
            <input 
              type="password" 
              placeholder="••••••••"
              value={groupPassword}
              onChange={(e) => setGroupPassword(e.target.value)}
              disabled={isSubmitting}
            />
          </div>

          <button type="submit" className="auth-submit-btn" disabled={isSubmitting}>
            {isSubmitting ? "Verifying Keys..." : "Access Dashboard"}
          </button>
        </form>
      </div>
    </div>
  );
}