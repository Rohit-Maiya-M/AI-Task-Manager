import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Sidebar from "./Sidebar";
import api from "../utils/api"; 
import { FaPlus, FaPencilAlt, FaBook, FaTasks, FaSearch, FaTimes } from "react-icons/fa";
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import "./PersonalDashBoard.css";

export default function PersonalDashBoard() {
  const navigate = useNavigate();
  
  const [dueTasks, setDueTasks] = useState([]);
  const [recentTasks, setRecentTasks] = useState([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isSearchOpen, setIsSearchOpen] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [results, setResults] = useState([]);

  useEffect(() => {
    const loadDashboardData = async () => {
      try {
        setIsLoading(true);
        // Parallel fetching for performance
        const [dueRes, recentRes] = await Promise.all([
          api.get('/personal/due-date', { params: { days: 7 } }),
          api.get('/personal/recents', { params: { days: 7 } })
        ]);
        setDueTasks(dueRes.data);
        setRecentTasks(recentRes.data);
      } catch (error) {
        console.error("Error loading data:", error);
      } finally {
        setIsLoading(false);
      }
    };
    loadDashboardData();
  }, []);

  useEffect(() => {
    if (searchQuery.length > 2) {
      const delayDebounceFn = setTimeout(async () => {
        try {
          const response = await api.get('/personal/search', {
            params: { keyword: searchQuery, size: 10 }
          });
          setResults(response.data.content); 
        } catch (err) { console.error("Search failed:", err); }
      }, 300);
      return () => clearTimeout(delayDebounceFn);
    } else {
      setResults([]);
    }
  }, [searchQuery]);

  const handleTaskClick = async (taskId) => {
    try {
      await api.patch(`/personal/${taskId}/visit`);
      setIsSearchOpen(false);
      navigate(`/personal/task/edit/${taskId}`);
    } catch (error) {
      navigate(`/personal/task/edit/${taskId}`);
    }
  };

  // Format the personal recents for the Sidebar prop
  const sidebarRecents = recentTasks.slice(0, 3).map(task => ({
    id: task.id,
    title: task.title,
    onClick: () => handleTaskClick(task.id)
  }));

  return (
    <div className="personal-dashboard" style={{ display: "flex", minHeight: "100vh" }}>
      
      <Sidebar 
        basePath="/personal" 
        onSearchClick={() => setIsSearchOpen(true)} 
        recentItems={sidebarRecents} 
      />

      <div 
        className="dashboard-main-content" 
        style={{ flex: 1, marginLeft: "var(--sidebar-width, 260px)", padding: "2rem", position: "relative", zIndex: 1 }}
      >
        <h1 style={{ color: "var(--personal-accent)" }}>Personal Dashboard</h1>
        <p>Manage your personal tasks and goals here.</p>

        <div className="action-buttons">
          <div className="square-btn" onClick={() => navigate("/personal/task/create")}><FaPlus size={28} /><span>Create</span></div>
          <div className="square-btn" onClick={() => recentTasks[0] && handleTaskClick(recentTasks[0].id)}><FaPencilAlt size={28} /><span>Edit Latest</span></div>
          <div className="square-btn" onClick={() => navigate("/personal/library")}><FaBook size={28} /><span>Library</span></div>
          <div className="square-btn" onClick={() => setIsSearchOpen(true)}><FaSearch size={28} /><span>Search</span></div>
        </div>

        <div className="due-soon-section">
          <h2>Due Soon (Next 7 Days)</h2>
          {isLoading ? (
            <div className="loading">Fetching deadlines...</div>
          ) : dueTasks.length > 0 ? (
            dueTasks.map((task) => (
              <div key={task.id} className="due-task clickable-card" onClick={() => handleTaskClick(task.id)}>
                <div className="task-info">
                  <strong>{task.title}</strong>
                  <div className="task-desc">{task.description}</div>
                </div>
                <div className="task-meta">
                  <span className={`priority-tag ${task.priority}`}>{task.priority}</span>
                </div>
              </div>
            ))
          ) : (
            <p className="empty-msg">No tasks due this week.</p>
          )}
        </div>
      </div>

      {/* Right Sidebar */}
      <div style={{ width: "350px", padding: "1rem", margin: "2rem", display: "flex", flexDirection: "column", height: "calc(100vh - 4rem)" }}>
        <div className="calendar-section" style={{ marginBottom: "2rem" }}>
          <h2 style={{ marginBottom: "1rem" }}>Calendar</h2>
          <Calendar />
        </div>

        <div className="todays-tasks">
          <h2>Recently Visited</h2>
          {recentTasks.slice(0, 4).map(task => (
            <div key={task.id} className="task recent-item" onClick={() => handleTaskClick(task.id)}>
              {task.title}
            </div>
          ))}
        </div>
      </div>

      {/* Notion Search Modal */}
      {isSearchOpen && (
        <div className="search-overlay" onClick={() => setIsSearchOpen(false)}>
          <div className="search-modal" onClick={(e) => e.stopPropagation()}>
            <div className="search-header">
              <FaSearch className="modal-search-icon" />
              <input 
                autoFocus
                placeholder="Search or ask a question..." 
                value={searchQuery}
                onChange={(e) => setSearchQuery(e.target.value)}
              />
              <div className="close-btn" onClick={() => setIsSearchOpen(false)}><FaTimes /></div>
            </div>
            <div className="search-results-container">
              {results.length > 0 ? (
                results.map(task => (
                  <div key={task.id} className="search-result-item" onClick={() => handleTaskClick(task.id)}>
                    <FaTasks className="result-icon" />
                    <div className="result-details">
                      <div className="result-title">{task.title}</div>
                      <div className="result-subtext">Personal / {task.category || "General"}</div>
                    </div>
                  </div>
                ))
              ) : searchQuery.length > 2 ? (
                <div className="search-no-results">No results found for "{searchQuery}"</div>
              ) : (
                <div className="search-placeholder">Type at least 3 characters to search...</div>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}