import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from "../utils/api";
import { FaUsers, FaArrowLeft, FaRocket, FaCheckCircle, FaClipboard, FaSignInAlt } from "react-icons/fa";
import "./GroupCreatePage.css";

export default function GroupAdminCreate() {
  const navigate = useNavigate();
  const [groupName, setGroupName] = useState("");
  const [description, setDescription] = useState("");
  const [password, setPassword] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");
  
  // New States to handle post-creation success presentation
  const [createdGroupId, setCreatedGroupId] = useState(null);
  const [isCopied, setIsCopied] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!groupName.trim() || !password) {
      setError("Group name and an administrative password are required.");
      return;
    }

    try {
      setIsSubmitting(true);
      setError("");

      // Hits @PostMapping("/createGroup")
      const response = await api.post("/group/admin/createGroup", {
        name: groupName,
        description: description,
        password: password 
      });

      // Extract the dynamic auto-incremented ID from GroupMemberResponseDTO
      const newGroupId = response.data.groupId;
      setCreatedGroupId(newGroupId);

    } catch (err) {
      setError(err.response?.data?.message || "Failed to initialize group workspace.");
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleCopyToClipboard = () => {
    navigator.clipboard.writeText(createdGroupId);
    setIsCopied(true);
    setTimeout(() => setIsCopied(false), 2000); // Reset toast text after 2s
  };

  // Condition: If group is created, swap form out for the ID Confirmation Card
  if (createdGroupId) {
    return (
      <div className="group-create-container">
        <div className="group-create-main">
          <div className="group-create-card success-card">
            <div className="card-header">
              <FaCheckCircle className="success-icon" />
              <h1>Workspace Deployed!</h1>
              <p>Your team database repository has been initialized successfully.</p>
            </div>

            <div className="id-display-container">
              <label>Your Unique Group ID</label>
              <div className="id-badge-row">
                <span className="generated-id">{createdGroupId}</span>
                <button 
                  className="copy-id-btn" 
                  onClick={handleCopyToClipboard}
                  title="Copy ID to clipboard"
                >
                  <FaClipboard /> {isCopied ? "Copied!" : "Copy"}
                </button>
              </div>
              <p className="warning-note">
                <strong>Important:</strong> Share this Group ID with your team members so they can register and join. You will need it to log in as Admin.
              </p>
            </div>

            <button 
              className="submit-group-btn proceed-btn" 
              onClick={() => navigate("/group/admin-auth")}
            >
              <FaSignInAlt /> Proceed to Login Gateway
            </button>
          </div>
        </div>
      </div>
    );
  }

  // Otherwise render the standard creation form...
  return (
    <div className="group-create-container">
      <div className="group-create-main">
        <button className="back-btn" onClick={() => navigate("/group/admin-choice")}>
          <FaArrowLeft /> Back
        </button>

        <div className="group-create-card">
          <div className="card-header">
            <FaUsers className="group-icon" />
            <h1>Provision New Group</h1>
            <p>Deploy an isolated cooperative space across your relational database.</p>
          </div>

          {error && <div className="error-alert">{error}</div>}

          <form onSubmit={handleSubmit} className="group-form">
            <div className="form-group">
              <label>Group Name</label>
              <input
                type="text"
                placeholder="e.g., VTU Project Team Alpha"
                value={groupName}
                onChange={(e) => setGroupName(e.target.value)}
                disabled={isSubmitting}
              />
            </div>

            <div className="form-group">
              <label>Administrative Entry Password</label>
              <input
                type="password"
                placeholder="Secure access phrase for group logins"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                disabled={isSubmitting}
              />
            </div>

            <div className="form-group">
              <label>Description / Focus</label>
              <textarea
                rows="3"
                placeholder="Define the scope or primary objective of this team space..."
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                disabled={isSubmitting}
              />
            </div>

            <button type="submit" className="submit-group-btn" disabled={isSubmitting}>
              {isSubmitting ? "Deploying Ecosystem..." : <><FaRocket /> Initialize Group</>}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}