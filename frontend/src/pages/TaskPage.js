import { useState, useRef, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useParams } from "react-router-dom";
import Sidebar from "./Sidebar";
import api from "../utils/api"; 
import { Button, Select, SelectItem } from "@carbon/react";
import { 
  FaCalendarAlt, FaLayerGroup, FaTag, FaInfoCircle, 
  FaClock, FaExclamationTriangle 
} from "react-icons/fa";
import "./TaskPage.css";


export default function TaskPage({ mode = "create", initialData = {} }) {
  const { taskId } = useParams();
  const navigate = useNavigate();
  const descriptionRef = useRef(null);
  
  const [task, setTask] = useState({
    title: initialData.title || "",
    description: initialData.description || "",
    dueDate: initialData.dueDate ? initialData.dueDate.split('T')[0] : "",
    status: initialData.status || "TODO",
    priority: initialData.priority || "MEDIUM",
    category: initialData.category || "",
    content: initialData.content || "",
    taskType: initialData.taskType || "PERSONAL",
    createdAt: initialData.createdAt || new Date().toISOString()
  });

  // Auto-resize description textarea
  useEffect(() => {
    if (mode === "edit" && taskId) {
      const fetchTaskData = async () => {
        try {
          // Hits your backend: e.g., @GetMapping("/{id}") in TaskPersonalController
          const response = await api.get(`/personal/${taskId}`);
          const data = response.data;

          setTask({
            title: data.title,
            description: data.description,
            dueDate: data.dueDate ? data.dueDate.split('T')[0] : "",
            status: data.status,
            priority: data.priority,
            category: data.category,
            content: data.content,
            taskType: data.taskType,
            createdAt: data.createdAt
          });
        } catch (error) {
          console.error("Failed to fetch task details:", error);
          alert("Could not load task data.");
        }
      };
      fetchTaskData();
    }
  }, [mode, taskId]);

  // Update your handleTaskAction to use the taskId for PUT requests
  const handleTaskAction = async () => {
    const taskPayload = {
      ...task,
      dueDate: task.dueDate ? `${task.dueDate}T23:59:59` : null,
      completed: task.status === "DONE"
    };

    try {
      const response = mode === "create" 
        ? await api.post('/personal/create', taskPayload)
        : await api.put(`/personal/edit/${taskId}`, taskPayload); // Use taskId here

      if (response.status === 200) navigate("/personal");
    } catch (error) {
      console.error("Submission failed", error);
    }
  };

  return (
    <div className="task-page-layout">
      <Sidebar />

      <div className="task-page-container">
        <div className="notion-form">
          <input
            type="text"
            className="notion-title-input"
            placeholder="Untitled Task"
            value={task.title}
            onChange={(e) => setTask({ ...task, title: e.target.value })}
          />

          <div className="notion-properties">
            {/* Description Row */}
            <div className="property-row">
              <div className="property-label"><FaInfoCircle /> Description</div>
              <div className="property-value">
                <textarea 
                  ref={descriptionRef}
                  className="property-value-textarea" 
                  placeholder="Add description..."
                  rows="1"
                  value={task.description}
                  onChange={(e) => setTask({ ...task, description: e.target.value })}
                />
              </div>
            </div>

            {/* Priority Row */}
            <div className="property-row">
              <div className="property-label"><FaExclamationTriangle /> Priority</div>
              <div className="property-value">
                <Select
                  id="priority-select"
                  hideLabel
                  className="notion-select"
                  value={task.priority}
                  onChange={(e) => setTask({ ...task, priority: e.target.value })}
                >
                  <SelectItem value="LOW" text="Low" />
                  <SelectItem value="MEDIUM" text="Medium" />
                  <SelectItem value="HIGH" text="High" />
                </Select>
              </div>
            </div>

            {/* Due Date Row */}
            <div className="property-row">
              <div className="property-label"><FaCalendarAlt /> Due Date</div>
              <div className="property-value">
                 <input 
                  type="date" 
                  className="date-picker-input" 
                  value={task.dueDate}
                  onChange={(e) => setTask({ ...task, dueDate: e.target.value })}
                />
              </div>
            </div>

            {/* Status Row */}
            <div className="property-row">
              <div className="property-label"><FaLayerGroup /> Status</div>
              <div className="property-value">
                <Select
                  id="status-select"
                  hideLabel
                  className="notion-select"
                  value={task.status}
                  onChange={(e) => setTask({ ...task, status: e.target.value })}
                >
                  <SelectItem value="TODO" text="To Do" />
                  <SelectItem value="IN_PROGRESS" text="In Progress" />
                  <SelectItem value="DONE" text="Done" />
                </Select>
              </div>
            </div>

            {/* Category Row */}
            <div className="property-row">
              <div className="property-label"><FaTag /> Category</div>
              <div className="property-value">
                <input 
                  type="text" 
                  className="property-value-input" 
                  placeholder="Empty"
                  value={task.category}
                  onChange={(e) => setTask({ ...task, category: e.target.value })}
                />
              </div>
            </div>

            {/* Created At Row */}
            <div className="property-row">
              <div className="property-label"><FaClock /> Created at</div>
              <div className="property-value read-only-text">
                {new Date(task.createdAt).toLocaleDateString()}
              </div>
            </div>
          </div>

          <hr className="notion-divider" />

          <textarea
            className="notion-body-input"
            placeholder="Start typing notes..."
            value={task.content}
            onChange={(e) => setTask({ ...task, content: e.target.value })}
          />

          <div className="notion-footer">
            <Button kind="primary" onClick={handleTaskAction}>
              {mode === "create" ? "Create Task" : "Save Changes"}
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}