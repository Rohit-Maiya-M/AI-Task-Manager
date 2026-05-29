import { useState, useEffect } from "react";
import { useSearchParams, useNavigate, useLocation } from "react-router-dom";
import Sidebar from "./Sidebar";
import api from "../utils/api";
import { FaArrowLeft, FaTasks, FaPencilAlt, FaUserAlt, FaIdCard, FaShieldAlt } from "react-icons/fa";
import "./AssignedTasksToUser.css";

export default function AssignedTasksToUser() {
  const navigate = useNavigate();
  const location = useLocation();
  const [searchParams] = useSearchParams();
  const groupId = searchParams.get("groupId");

  // Fallback safe extraction if state isn't provided via link redirection directly
  const { userId, username, role } = location.state || { userId: "N/A", username: "Unknown User", role: "MEMBER" };

  const [tasks, setTasks] = useState([]);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    if (!groupId || userId === "N/A") return;

    const fetchUserTasks = async () => {
      try {
        setIsLoading(true);
        // Hits @GetMapping("/filter/{groupId}")
        const response = await api.get(`/group/admin/filter/${groupId}`, {
          params: { page: 0, size: 50, sortBy: "dueDate", sortDir: "asc" }
        });

        // Filter assignments matching this member's explicit ID parameter state
        const groupBacklog = response.data.content || [];
        const personalAssignments = groupBacklog.filter(task => String(task.assignedUserId) === String(userId));
        
        setTasks(personalAssignments);
      } catch (error) {
        console.error("Failed to compile user tasks:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchUserTasks();
  }, [groupId, userId]);

  return (
    <div className="user-tasks-container admin-theme" style={{ display: "flex", minHeight: "100vh" }}>
      <Sidebar basePath="/group" homePath={`/group/admin?groupId=${groupId}`} onSearchClick={() => {}} recentItems={[]} />

      <div className="user-tasks-main">
        <button className="back-btn" onClick={() => navigate(`/group/admin/view-members?groupId=${groupId}`)}>
          <FaArrowLeft /> Back to Team Registry
        </button>

        {/* Member Profile Banner Widget */}
        <div className="member-profile-banner">
          <div className="profile-badge-info">
            <div className="profile-avatar-frame">
              <FaUserAlt size={24} />
            </div>
            <div className="profile-text-nodes">
              <h2>{username}</h2>
              <p>Reviewing active sprint backlog and task metrics</p>
            </div>
          </div>
          
          <div className="profile-metadata-pills">
            <div className="meta-pill"><FaIdCard /> ID: {userId}</div>
            <div className="meta-pill role"><FaShieldAlt /> Role: {role}</div>
          </div>
        </div>

        {/* Member Assigned Task Panel Display Grid */}
        <div className="tasks-panel">
          <h3><FaTasks /> Allocated Work Packages</h3>
          
          {isLoading ? (
            <div className="tasks-loading">Querying active task metrics...</div>
          ) : tasks.length > 0 ? (
            <div className="tasks-grid">
              {tasks.map(task => (
                <div 
                  key={task.id} 
                  className="task-drilldown-card"
                  onClick={() => navigate(`/group/task/edit/${groupId}/${task.id}`)}
                >
                  <div className="card-top-row">
                    <h4>{task.title}</h4>
                    <span className={`priority-badge-pill ${task.priority}`}>{task.priority}</span>
                  </div>
                  <p className="task-card-desc">{task.description || "No descriptions appended to this target row item."}</p>
                  <div className="card-bottom-row">
                    <span className="task-status-label">Status: <strong>{task.status || "PENDING"}</strong></span>
                    <span className="edit-action-link"><FaPencilAlt /> Edit</span>
                  </div>
                </div>
              ))}
            </div>
          ) : (
            <div className="tasks-empty-state">
              <p>No tasks currently assigned to this member.</p>
              <button
                className="assign-btn"
                onClick={() => navigate(`/group/task/create?groupId=${groupId}`, {
                  state: { userId, username, role }
                })}
              >
                Create & Assign First Task
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
