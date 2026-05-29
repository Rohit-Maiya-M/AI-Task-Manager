import { useState, useEffect } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import Sidebar from "./Sidebar";
import api from "../utils/api";
import { FaPlus, FaPencilAlt, FaUserPlus, FaUsers, FaSearch, FaTasks, FaTimes } from "react-icons/fa";
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import "./GroupDashBoard.css"; 

export default function GroupAdminDashBoard() {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const groupId = searchParams.get("groupId");

  // Dashboard state hooks matching the Personal structural style
  const [dueTasks, setDueTasks] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  
  // Search state hooks
  const [isSearchOpen, setIsSearchOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [searchResults, setSearchResults] = useState([]);

  // 1. Fetch group tasks dynamically using your 7-day filtering limits
  useEffect(() => {
    if (!groupId) return;

    const fetchGroupData = async () => {
      try {
        setIsLoading(true);
        // Hits @GetMapping("/filter/{groupId}") mirroring personal 7 days implementation
        const response = await api.get(`/group/admin/filter/${groupId}`, {
          params: { 
            page: 0, 
            size: 10, 
            sortBy: "dueDate", 
            sortDir: "asc"
            // If your backend filter service supports status tracking parameters, 
            // you can pass extra fields here smoothly.
          }
        });
        setDueTasks(response.data.content); 
      } catch (error) {
        console.error("Failed to load group deadlines:", error);
      } finally {
        setIsLoading(false);
      }
    };

    fetchGroupData();
  }, [groupId]);

  // 2. Real-time Search Logic inside current Group context
  useEffect(() => {
    if (searchQuery.length > 2 && groupId) {
      const delayDebounceFn = setTimeout(async () => {
        try {
          // Hits @GetMapping("/search/{groupId}")
          const response = await api.get(`/group/admin/search/${groupId}`, {
            params: { keyword: searchQuery, size: 10 }
          });
          setSearchResults(response.data.content);
        } catch (err) {
          console.error("Group task search failed:", err);
        }
      }, 300);
      return () => clearTimeout(delayDebounceFn);
    } else {
      setSearchResults([]);
    }
  }, [searchQuery, groupId]);

  const handleTaskNavigation = (taskId) => {
    navigate(`/group/task/edit/${groupId}/${taskId}`);
  };

  // Safe sidebar mapping structures built via your loaded state queue
  const sidebarRecents = dueTasks.slice(0, 3).map(task => ({
    id: task.id,
    title: task.title,
    onClick: () => handleTaskNavigation(task.id)
  }));

  return (
    <div className="group-dashboard admin-theme" style={{ display: "flex", minHeight: "100vh" }}>
      
      {/* Reusable Sidebar passed with team contextual scope */}
      <Sidebar 
        basePath="/group" 
        onSearchClick={() => setIsSearchOpen(true)} 
        recentItems={sidebarRecents} 
      />

      <div 
        className="dashboard-main-content" 
        style={{ 
          flex: 1, 
          marginLeft: "var(--sidebar-width, 260px)", 
          padding: "2rem",
          position: "relative",
          zIndex: 1
        }}
      >
        <h1 className="group-heading">Group Administration (Workspace #{groupId})</h1>
        <p className="group-subtext">Manage group tasks and member assignments.</p>

        {/* Updated Action Box Grid Structure */}
        <div className="action-buttons">
          {/* Box 1: Add Member */}
          <div className="square-btn admin-btn" onClick={() => navigate(`/group/admin/members?groupId=${groupId}`)}>
            <FaUserPlus size={28} />
            <span>Add Member</span>
          </div>

          {/* Box 2: Create Task For Group */}
          <div className="square-btn admin-btn" onClick={() => navigate(`/group/task/create?groupId=${groupId}`)}>
            <FaPlus size={28} />
            <span>Create Task</span>
          </div>

          {/* Box 3: Edit Task For Group */}
          <div className="square-btn admin-btn" onClick={() => navigate(`/group/task/edit?groupId=${groupId}`)}>
            <FaPencilAlt size={28} />
            <span>Edit Task</span>
          </div>

          {/* Box 4: View Members in Group */}
          <div className="square-btn admin-btn" onClick={() => navigate(`/group/admin/view-members?groupId=${groupId}`)}>
            <FaUsers size={28} /> 
            <span>View Members</span>
          </div>
        </div>

        {/* Due Soon Section styled like Personal dashboard inside admin background theme structure */}
        <div className="group-panel due-soon-section">
          <h2>Due Soon (Next 7 Days)</h2>
          {isLoading ? (
            <div className="loading-state">Fetching team deadlines...</div>
          ) : dueTasks.length > 0 ? (
            dueTasks.map(task => (
              <div 
                key={task.id} 
                className="task-item dynamic-card clickable-card" 
                onClick={() => handleTaskNavigation(task.id)}
              >
                <div className="task-info">
                  <strong className="task-title">{task.title}</strong>
                  <div className="task-desc" style={{ fontSize: "0.85rem", color: "#a0aec0", marginTop: "4px" }}>
                    {task.description || "No description provided."}
                  </div>
                  <span className="task-assignee" style={{ fontSize: "0.8rem", color: "#c084fc", display: "block", marginTop: "6px" }}>
                    Assigned to: {task.assignedUserName || "Unassigned"}
                  </span>
                </div>
                <div className="task-meta">
                  <span className={`priority-tag priority-badge ${task.priority}`}>{task.priority}</span>
                </div>
              </div>
            ))
          ) : (
            <div className="empty-state">No tasks due this week within this group workspace.</div>
          )}
        </div>
      </div>

      {/* Right Column Layout Container */}
      <div className="group-right-section" style={{ width: "350px", padding: "1rem", margin: "2rem", zIndex: 1 }}>
        <div className="calendar-section">
          <h2 style={{ marginBottom: "1rem", color: "#c084fc" }}>Admin Calendar</h2>
          <Calendar />
        </div>

        <div className="group-panel" style={{ marginTop: "2rem" }}>
          <h2>Member Status</h2>
          <div className="member-status current-user">Rohit Maiya (Admin)</div>
          <div className="member-status">Team Member A (Active)</div>
        </div>
      </div>

      {/* --- NOTION-STYLE SEARCH MODAL FOR WORKSPACE GROUP --- */}
      {isSearchOpen && (
        <div className="search-overlay" onClick={() => setIsSearchOpen(false)}>
          <div className="search-modal" onClick={(e) => e.stopPropagation()}>
            <div className="search-header">
              <FaSearch className="modal-search-icon" />
              <input 
                autoFocus
                placeholder="Search group backlog..." 
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
              <div className="close-btn" onClick={() => setIsSearchOpen(false)}><FaTimes /></div>
            </div>
            <div className="search-results-container">
              {searchResults.length > 0 ? (
                searchResults.map(task => (
                  <div key={task.id} className="search-result-item" onClick={() => handleTaskNavigation(task.id)}>
                    <FaTasks className="result-icon" />
                    <div className="result-details">
                      <div className="result-title">{task.title}</div>
                      <div className="result-subtext">Group Workspace / {task.category || "General"}</div>
                    </div>
                  </div>
                ))
              ) : searchQuery.length > 2 ? (
                <div className="search-no-results">No group records matched "{searchQuery}"</div>
              ) : (
                <div className="search-placeholder">Type 3+ characters to query group database...</div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
