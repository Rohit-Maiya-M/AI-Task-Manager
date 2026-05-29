import { useState } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import Sidebar from "./Sidebar";
import api from "../utils/api";
import { FaUserPlus, FaArrowLeft, FaIdCard, FaUserCheck } from "react-icons/fa";
import "./AddGroupMember.css"; // Pointing to updated CSS name

export default function AddGroupMember() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const groupId = searchParams.get("groupId");

  // Form Field States
  const [memberUserId, setMemberUserId] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [successMessage, setSuccessMessage] = useState("");
  const [errorMessage, setErrorMessage] = useState("");

  const handleAddMember = async (e) => {
    e.preventDefault();
    
    if (!memberUserId.trim()) {
      setErrorMessage("Please enter a valid User ID.");
      return;
    }

    try {
      setIsSubmitting(true);
      setErrorMessage("");
      setSuccessMessage("");

      // Hits @PostMapping("/createMember") matching GroupMemberRequestDTO
      await api.post("/group/admin/createMember", {
        groupId: parseInt(groupId),
        userId: parseInt(memberUserId)
      });

      setSuccessMessage(`User ID ${memberUserId} successfully added to workspace #${groupId}!`);
      setMemberUserId(""); // Clear form input frame
    } catch (error) {
      console.error("Enrolment execution failed:", error);
      setErrorMessage(
        error.response?.data?.message || "Failed to enrol member. Verify the User ID exists."
      );
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="group-member-container admin-theme" style={{ display: "flex", minHeight: "100vh" }}>
      
      {/* Reusable Sidebar maintaining administration context */}
      <Sidebar basePath="/group" homePath={`/group/admin?groupId=${groupId}`} onSearchClick={() => {}} recentItems={[]} />

      <div className="group-member-main">
        <button className="back-btn" onClick={() => navigate(`/group/admin?groupId=${groupId}`)}>
          <FaArrowLeft /> Return to Dashboard
        </button>

        <div className="member-action-card">
          <div className="card-header">
            <FaUserPlus className="member-icon" />
            <h1>Enrol Workspace Member</h1>
            <p>Add a developer or teammate to Group Workspace #{groupId} using their system identity key.</p>
          </div>

          {successMessage && <div className="alert-box success-alert"><FaUserCheck /> {successMessage}</div>}
          {errorMessage && <div className="alert-box error-alert">{errorMessage}</div>}

          <form onSubmit={handleAddMember} className="member-form">
            <div className="form-group">
              <label htmlFor="userIdInput">
                <FaIdCard /> Target User Database ID
              </label>
              <input
                id="userIdInput"
                type="number"
                placeholder="Enter member's numerical system User ID (e.g., 14)"
                value={memberUserId}
                onChange={(e) => setMemberUserId(e.target.value)}
                disabled={isSubmitting}
              />
            </div>

            <button type="submit" className="submit-member-btn" disabled={isSubmitting}>
              {isSubmitting ? "Enrolling Member..." : "Grant Workspace Access"}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
