import { useState, useEffect } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import Sidebar from "./Sidebar";
import api from "../utils/api";
import { FaUsers, FaArrowLeft, FaTrashAlt, FaUserShield, FaUser, FaChevronRight } from "react-icons/fa";
import "./ViewGroupMembers.css";

export default function ViewGroupMembers() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const groupId = searchParams.get("groupId");

  const [members, setMembers] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    if (!groupId) return;

    const fetchMembers = async () => {
      try {
        setIsLoading(true);
        setError("");
        const response = await api.get(`/group/admin/members/${groupId}`);
        const mappedMembers = response.data.map(member => ({
          id: member.userId,
          username: member.username,
          role: member.role
        }));
        setMembers(mappedMembers);
      } catch (err) {
        console.error("Failed to load group roster:", err);
        setError("Could not retrieve workspace membership details from the database.");
      } finally {
        setIsLoading(false);
      }
    };

    fetchMembers();
  }, [groupId]);

  const handleDeleteMember = async (e, targetUserId) => {
    e.stopPropagation(); // Prevents row selection click from firing when clicking delete
    setError("");
    setSuccess("");

    if (!window.confirm(`Are you sure you want to remove User ID ${targetUserId} from this group?`)) {
      return;
    }

    try {
      await api.delete("/group/admin/deleteMember", {
        data: {
          groupId: parseInt(groupId, 10),
          userId: parseInt(targetUserId, 10)
        }
      });

      setSuccess("Deleted Member Successfully!");
      setMembers(prev => prev.filter(member => member.id !== targetUserId));
    } catch (err) {
      console.error("Eviction call failed:", err);
      setError(err.response?.data || "Failed to remove member from workspace registry.");
    }
  };

  // Pushes user data into router state context cleanly
  const handleRowClick = (member) => {
    navigate(`/group/admin/member-tasks?groupId=${groupId}`, {
      state: { 
        userId: member.id, 
        username: member.username, 
        role: member.role 
      }
    });
  };

  return (
    <div className="group-view-container admin-theme" style={{ display: "flex", minHeight: "100vh" }}>
      <Sidebar basePath="/group" onSearchClick={() => {}} recentItems={[]} />

      <div className="group-view-main">
        <button className="back-btn" onClick={() => navigate(`/group/admin?groupId=${groupId}`)}>
          <FaArrowLeft /> Return to Dashboard
        </button>

        <div className="view-members-card">
          <div className="card-header">
            <FaUsers className="view-icon" />
            <h1>Workspace Team Registry</h1>
            <p>Review active permissions. Click any member's row to view their assigned backlog.</p>
          </div>

          {success && <div className="alert-box success-alert">{success}</div>}
          {error && <div className="alert-box error-alert">{error}</div>}

          {isLoading ? (
            <div className="loading-roster">Loading workspace directory database...</div>
          ) : (
            <div className="members-list-wrapper">
              <table className="members-table">
                <thead>
                  <tr>
                    <th>Identity Profile</th>
                    <th>System Role</th>
                    <th style={{ textAlign: "right" }}>Management Actions</th>
                  </tr>
                </thead>
                <tbody>
                  {members.map((member) => (
                    <tr key={member.id} className="member-row clickable-row" onClick={() => handleRowClick(member)}>
                      <td>
                        <div className="member-meta-block">
                          {member.role === "ADMIN" ? <FaUserShield className="role-icon admin" /> : <FaUser className="role-icon member" />}
                          <div className="text-details">
                            <span className="member-name">{member.username}</span>
                            <span className="member-subtext">User ID Reference: {member.id}</span>
                          </div>
                        </div>
                      </td>
                      <td>
                        <span className={`role-badge ${member.role?.toLowerCase()}`}>
                          {member.role}
                        </span>
                      </td>
                      <td style={{ textAlign: "right" }}>
                        <div style={{ display: "flex", alignItems: "center", justifyContent: "flex-end", gap: "12px" }}>
                          <button 
                            className="evict-btn"
                            disabled={member.role === "ADMIN"}
                            onClick={(e) => handleDeleteMember(e, member.id)}
                            title={member.role === "ADMIN" ? "Administrators cannot be evicted" : "Revoke membership"}
                          >
                            <FaTrashAlt /> Revoke Access
                          </button>
                          <FaChevronRight style={{ color: "#475569" }} />
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}