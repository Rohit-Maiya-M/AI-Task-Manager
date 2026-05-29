import { useState, useEffect } from "react";
import { useParams, useSearchParams, useNavigate, useLocation } from "react-router-dom";
import Sidebar from "./Sidebar";
import api from "../utils/api";
import { FaPlus, FaArrowLeft, FaTasks, FaCalendarAlt, FaLayerGroup, FaUserAlt } from "react-icons/fa";
import "./GroupTaskPage.css";

export default function GroupTaskPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const [searchParams] = useSearchParams();
  
  // Extract context parameters cleanly
  const groupId = searchParams.get("groupId");
  // Check if an explicit pre-assigned user context was passed down from the drill-down view state
  const preAssignedUserId = location.state?.userId || "";
  const preAssignedUsername = location.state?.username || "";

  // Form Field States matching TaskRequestDTO
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [dueDate, setDueDate] = useState("");
  const [priority, setPriority] = useState("MEDIUM");
  const [category, setCategory] = useState("General");
  const [assignedUserId, setAssignedUserId] = useState(preAssignedUserId ? String(preAssignedUserId) : "");

  // Group Roster state for the dropdown list selection
  const [roster, setRoster] = useState([]);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");

  // Fetch the total team registry so the admin can pick an assignee from a dropdown list
  useEffect(() => {
    if (!groupId) return;

    const fetchRoster = async () => {
      try {
        const response = await api.get(`/group/admin/members/${groupId}`);
        setRoster(response.data);
      } catch (err) {
        console.error("Failed to fetch group roster for assignment context:", err);
      }
    };
    fetchRoster();
  }, [groupId]);

  const handleTaskSubmit = async (e) => {
    e.preventDefault();
    if (!title.trim() || !dueDate) {
      setError("Task title and valid due date parameters are required.");
      return;
    }

    try {
      setIsSubmitting(true);
      setError("");

      const taskPayload = {
        title: title,
        description: description,
        dueDate: `${dueDate}T23:59:59`,
        priority: priority,
        category: category,
        taskType: "GROUP"
      };

      let response;
      
      if (assignedUserId) {
        // Option A: Hits @PostMapping("/create/{groupId}/{assignedUserId}")
        response = await api.post(`/group/admin/create/${groupId}/${assignedUserId}`, taskPayload);
      } else {
        // Option B: Hits @PostMapping("/create/{groupId}")
        response = await api.post(`/group/admin/create/${groupId}`, taskPayload);
      }

      // Mission successful! Route back to the active Admin Workspace Dashboard panel
      navigate(`/group/admin?groupId=${groupId}`);
    } catch (err) {
      console.error("Task injection transaction failed:", err);
      setError(err.response?.data?.message || "Failed to provision group task entry.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="group-task-container admin-theme" style={{ display: "flex", minHeight: "100vh" }}>
      <Sidebar basePath="/group" onSearchClick={() => {}} recentItems={[]} />

      <div className="group-task-main">
        <button className="back-btn" onClick={() => navigate(-1)}>
          <FaArrowLeft /> Cancel & Go Back
        </button>

        <div className="task-creation-card">
          <div className="card-header">
            <FaPlus className="task-icon" />
            <h1>Initialize Group Task</h1>
            <p>Publish a new deliverable milestone inside Group Workspace #{groupId}.</p>
          </div>

          {error && <div className="task-error-alert">{error}</div>}

          <form onSubmit={handleTaskSubmit} className="task-form">
            <div className="form-group">
              <label><FaTasks /> Task Title</label>
              <input
                type="text"
                placeholder="e.g., Implement DB Relational Core Layout Triggers"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
                disabled={isSubmitting}
              />
            </div>

            <div className="form-group">
              <label><FaUserAlt /> Assign Team Member (Optional)</label>
              <select
                value={assignedUserId}
                onChange={(e) => setAssignedUserId(e.target.value)}
                disabled={isSubmitting || !!preAssignedUserId} // Lock selection if launched directly from a specific member's page
              >
                <option value="">-- Leave Unassigned (Group Backlog) --</option>
                {roster.map(member => (
                  <option key={member.userId} value={String(member.userId)}>
                    {member.username} ({member.role})
                  </option>
                ))}
              </select>
              {preAssignedUserId && (
                <span className="locked-assignee-note">
                  Locked to {preAssignedUsername || `User #${preAssignedUserId}`} from the member workspace.
                </span>
              )}
            </div>

            <div className="form-row-split">
              <div className="form-group">
                <label><FaCalendarAlt /> Target Due Date</label>
                <input
                  type="date"
                  value={dueDate}
                  onChange={(e) => setDueDate(e.target.value)}
                  disabled={isSubmitting}
                />
              </div>

              <div className="form-group">
                <label><FaLayerGroup /> Priority Level</label>
                <select 
                  value={priority} 
                  onChange={(e) => setPriority(e.target.value)}
                  disabled={isSubmitting}
                >
                  <option value="LOW">Low Priority</option>
                  <option value="MEDIUM">Medium Priority</option>
                  <option value="HIGH">High Priority</option>
                </select>
              </div>
            </div>

            <div className="form-group">
              <label>Work Domain Category</label>
              <input
                type="text"
                placeholder="e.g., Backend, Database, Frontend"
                value={category}
                onChange={(e) => setCategory(e.target.value)}
                disabled={isSubmitting}
              />
            </div>

            <div className="form-group">
              <label>Technical Specifications / Context</label>
              <textarea
                rows="4"
                placeholder="State specific requirements, endpoints, or rules governing this task allocation package..."
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                disabled={isSubmitting}
              />
            </div>

            <button type="submit" className="submit-task-btn" disabled={isSubmitting}>
              {isSubmitting ? "Committing Core Entry..." : "Publish & Allocate Task"}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
