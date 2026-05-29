import { useState } from "react";
import { useNavigate } from "react-router-dom";
import Sidebar from "./Sidebar";
import api from "../utils/api";
import { FaUsers, FaArrowLeft, FaRocket } from "react-icons/fa";
import "./GroupCreatePage.css";

export default function GroupCreatePage() {
  const navigate = useNavigate();
  const [groupName, setGroupName] = useState("");
  const [description, setDescription] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!groupName.trim()) {
      setErrorMessage("Group name is required.");
      return;
    }

    try {
      setIsSubmitting(true);
      setErrorMessage("");

      // Hits @PostMapping("/createGroup")
      const response = await api.post("/group/admin/createGroup", {
        name: groupName,
        description: description,
      });

      // On success, backend returns a GroupMemberResponseDTO containing the new groupId
      const targetGroupId = response.data.groupId || response.data.id;
      
      // Send them directly to their brand-new Admin Dashboard space
      navigate(`/group/admin?groupId=${targetGroupId}`);
    } catch (error) {
      console.error("Failed to create group:", error);
      setErrorMessage(
        error.response?.data?.message || "Failed to create group. Please try again."
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="group-create-container">
      {/* Reusing your generic Sidebar without passing items yet */}
      <Sidebar basePath="/group" onSearchClick={() => {}} recentItems={[]} />

      <div className="group-create-main">
        <button className="back-btn" onClick={() => navigate("/choice")}>
          <FaArrowLeft /> Back to Choices
        </button>

        <div className="group-create-card">
          <div className="card-header">
            <FaUsers className="group-icon" />
            <h1>Establish Group Workspace</h1>
            <p>Set up a shared environment for tracking milestones with your team.</p>
          </div>

          {errorMessage && <div className="error-alert">{errorMessage}</div>}

          <form onSubmit={handleSubmit} className="group-form">
            <div className="form-group">
              <label htmlFor="groupName">Group Name</label>
              <input
                id="groupName"
                type="text"
                placeholder="e.g., VTU Project Team Alpha"
                value={groupName}
                onChange={(e) => setGroupName(e.target.value)}
                disabled={isSubmitting}
                maxLength={50}
              />
            </div>

            <div className="form-group">
              <label htmlFor="description">Description / Objective</label>
              <textarea
                id="description"
                rows="4"
                placeholder="Briefly state the scope or core targets of this group workspace..."
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                disabled={isSubmitting}
                maxLength={255}
              />
            </div>

            <button type="submit" className="submit-group-btn" disabled={isSubmitting}>
              {isSubmitting ? (
                "Initializing Workspace..."
              ) : (
                <>
                  <FaRocket /> Launch Group
                </>
              )}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}