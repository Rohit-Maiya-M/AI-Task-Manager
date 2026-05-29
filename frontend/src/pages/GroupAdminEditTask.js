import { useEffect, useMemo, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router-dom";
import Sidebar from "./Sidebar";
import api from "../utils/api";
import {
  FaArrowLeft,
  FaCalendarAlt,
  FaLayerGroup,
  FaPencilAlt,
  FaTasks,
  FaUserAlt
} from "react-icons/fa";
import "./GroupTaskPage.css";
import "./GroupAdminEditTask.css";

export default function GroupAdminEditTask() {
  const navigate = useNavigate();
  const params = useParams();
  const [searchParams] = useSearchParams();

  const groupId = params.groupId || searchParams.get("groupId");
  const routeTaskId = params.taskId || "";

  const [tasks, setTasks] = useState([]);
  const [roster, setRoster] = useState([]);
  const [selectedTaskId, setSelectedTaskId] = useState(routeTaskId);
  const [isLoading, setIsLoading] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState("");

  const [task, setTask] = useState({
    title: "",
    description: "",
    dueDate: "",
    priority: "MEDIUM",
    status: "TODO",
    category: "General",
    assignedUserId: ""
  });

  const selectedTask = useMemo(
    () => tasks.find((item) => String(item.id) === String(selectedTaskId)),
    [tasks, selectedTaskId]
  );

  useEffect(() => {
    if (!groupId) {
      setError("Missing group workspace id.");
      setIsLoading(false);
      return;
    }

    const fetchEditContext = async () => {
      try {
        setIsLoading(true);
        setError("");

        const [tasksResponse, rosterResponse] = await Promise.all([
          api.get(`/group/admin/filter/${groupId}`, {
            params: {
              page: 0,
              size: 100,
              sortBy: "dueDate",
              sortDir: "asc"
            }
          }),
          api.get(`/group/admin/members/${groupId}`)
        ]);

        const loadedTasks = tasksResponse.data?.content || [];
        setTasks(loadedTasks);
        setRoster(rosterResponse.data || []);

        if (!selectedTaskId && loadedTasks.length > 0) {
          setSelectedTaskId(String(loadedTasks[0].id));
        }
      } catch (err) {
        console.error("Failed to load group edit context:", err);
        setError(err.response?.data?.message || "Could not load group tasks for editing.");
      } finally {
        setIsLoading(false);
      }
    };

    fetchEditContext();
  }, [groupId, selectedTaskId]);

  useEffect(() => {
    if (!selectedTask) return;

    setTask({
      title: selectedTask.title || "",
      description: selectedTask.description || "",
      dueDate: selectedTask.dueDate ? selectedTask.dueDate.split("T")[0] : "",
      priority: selectedTask.priority || "MEDIUM",
      status: selectedTask.status || (selectedTask.completed ? "DONE" : "TODO"),
      category: selectedTask.category || "General",
      assignedUserId: selectedTask.assignedUserId ? String(selectedTask.assignedUserId) : ""
    });
  }, [selectedTask]);

  const handleTaskSubmit = async (event) => {
    event.preventDefault();

    if (!selectedTaskId) {
      setError("Choose a task before saving changes.");
      return;
    }

    if (!task.title.trim() || !task.dueDate) {
      setError("Task title and due date are required.");
      return;
    }

    const taskPayload = {
      title: task.title,
      description: task.description,
      dueDate: `${task.dueDate}T23:59:59`,
      priority: task.priority,
      status: task.status,
      category: task.category,
      completed: task.status === "DONE",
      taskType: "GROUP"
    };

    try {
      setIsSubmitting(true);
      setError("");

      if (task.assignedUserId) {
        await api.put(
          `/group/admin/edit/${groupId}/${selectedTaskId}/${task.assignedUserId}`,
          taskPayload
        );
      } else {
        await api.put(`/group/admin/edit/${groupId}/${selectedTaskId}`, taskPayload);
      }

      navigate(`/group/admin?groupId=${groupId}`);
    } catch (err) {
      console.error("Group task edit failed:", err);
      setError(err.response?.data?.message || "Failed to update group task.");
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="group-task-container admin-theme" style={{ display: "flex", minHeight: "100vh" }}>
      <Sidebar basePath="/group" homePath={`/group/admin?groupId=${groupId}`} onSearchClick={() => {}} recentItems={[]} />

      <div className="group-task-main group-edit-main">
        <button className="back-btn" onClick={() => navigate(`/group/admin?groupId=${groupId}`)}>
          <FaArrowLeft /> Back to Admin Dashboard
        </button>

        <div className="task-creation-card group-edit-card">
          <div className="card-header">
            <FaPencilAlt className="task-icon" />
            <h1>Edit Group Task</h1>
            <p>Update an existing deliverable inside Group Workspace #{groupId}.</p>
          </div>

          {error && <div className="task-error-alert">{error}</div>}

          <form onSubmit={handleTaskSubmit} className="task-form">
            <div className="form-group">
              <label><FaTasks /> Select Task</label>
              <select
                value={selectedTaskId}
                onChange={(event) => setSelectedTaskId(event.target.value)}
                disabled={isLoading || isSubmitting}
              >
                <option value="">-- Choose a group task --</option>
                {tasks.map((item) => (
                  <option key={item.id} value={item.id}>
                    {item.title}
                  </option>
                ))}
              </select>
            </div>

            <div className="form-group">
              <label><FaTasks /> Task Title</label>
              <input
                type="text"
                placeholder="e.g., Update sprint planning board"
                value={task.title}
                onChange={(event) => setTask({ ...task, title: event.target.value })}
                disabled={!selectedTaskId || isSubmitting}
              />
            </div>

            <div className="form-group">
              <label><FaUserAlt /> Assign Team Member</label>
              <select
                value={task.assignedUserId}
                onChange={(event) => setTask({ ...task, assignedUserId: event.target.value })}
                disabled={!selectedTaskId || isSubmitting}
              >
                <option value="">-- Leave Unassigned (Group Backlog) --</option>
                {roster.map((member) => (
                  <option key={member.userId} value={member.userId}>
                    {member.username} ({member.role})
                  </option>
                ))}
              </select>
            </div>

            <div className="form-row-split">
              <div className="form-group">
                <label><FaCalendarAlt /> Target Due Date</label>
                <input
                  type="date"
                  value={task.dueDate}
                  onChange={(event) => setTask({ ...task, dueDate: event.target.value })}
                  disabled={!selectedTaskId || isSubmitting}
                />
              </div>

              <div className="form-group">
                <label><FaLayerGroup /> Priority Level</label>
                <select
                  value={task.priority}
                  onChange={(event) => setTask({ ...task, priority: event.target.value })}
                  disabled={!selectedTaskId || isSubmitting}
                >
                  <option value="LOW">Low Priority</option>
                  <option value="MEDIUM">Medium Priority</option>
                  <option value="HIGH">High Priority</option>
                </select>
              </div>
            </div>

            <div className="form-row-split">
              <div className="form-group">
                <label><FaLayerGroup /> Status</label>
                <select
                  value={task.status}
                  onChange={(event) => setTask({ ...task, status: event.target.value })}
                  disabled={!selectedTaskId || isSubmitting}
                >
                  <option value="TODO">To Do</option>
                  <option value="IN_PROGRESS">In Progress</option>
                  <option value="DONE">Done</option>
                </select>
              </div>

              <div className="form-group">
                <label>Work Domain Category</label>
                <input
                  type="text"
                  placeholder="e.g., Backend, Database, Frontend"
                  value={task.category}
                  onChange={(event) => setTask({ ...task, category: event.target.value })}
                  disabled={!selectedTaskId || isSubmitting}
                />
              </div>
            </div>

            <div className="form-group">
              <label>Technical Specifications / Context</label>
              <textarea
                rows="4"
                placeholder="Update requirements, endpoints, or task allocation notes..."
                value={task.description}
                onChange={(event) => setTask({ ...task, description: event.target.value })}
                disabled={!selectedTaskId || isSubmitting}
              />
            </div>

            <button type="submit" className="submit-task-btn" disabled={!selectedTaskId || isSubmitting}>
              {isSubmitting ? "Saving Task Changes..." : "Save Group Task Changes"}
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}
