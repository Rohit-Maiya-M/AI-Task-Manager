import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import Sidebar from "./Sidebar";
import api from "../utils/api";
import { FaChevronLeft, FaChevronRight, FaFilter, FaSortAmountDown } from "react-icons/fa";
import "./Library.css";

export default function Library() {
  const navigate = useNavigate();
  const [tasks, setTasks] = useState([]);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [sortConfig, setSortConfig] = useState({ sortBy: "dueDate", sortDir: "asc" });
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    fetchLibraryData();
  }, [currentPage, sortConfig]);

  const fetchLibraryData = async () => {
    try {
      setIsLoading(true);
      // Hits @GetMapping("/library")
      const response = await api.get('/personal/library', {
        params: {
          page: currentPage,
          size: 6, // Showing 6 tasks per page
          sortBy: sortConfig.sortBy,
          sortDir: sortConfig.sortDir
        }
      });
      // Page object returns results in 'content'
      setTasks(response.data.content);
      setTotalPages(response.data.totalPages);
    } catch (error) {
      console.error("Failed to load library:", error);
    } finally {
      setIsLoading(false);
    }
  };

  const handleTaskClick = async (taskId) => {
    try {
      await api.patch(`/personal/${taskId}/visit`);
      navigate(`/personal/task/edit/${taskId}`);
    } catch (error) {
      navigate(`/personal/task/edit/${taskId}`);
    }
  };

  return (
    <div className="library-page">
      <Sidebar basePath="/personal" />
      
      <div className="library-main-content">
        <header className="library-header">
          <div>
            <h1>Task Library</h1>
            <p>Your complete archive of tasks and projects.</p>
          </div>
          
          <div className="library-controls">
            <div className="sort-selector">
              <FaSortAmountDown />
              <select 
                value={sortConfig.sortBy} 
                onChange={(e) => setSortConfig({...sortConfig, sortBy: e.target.value})}
              >
                <option value="dueDate">Due Date</option>
                <option value="priority">Priority</option>
                <option value="createdAt">Created At</option>
              </select>
            </div>
          </div>
        </header>

        <div className="library-grid">
          {isLoading ? (
            <div className="loading-spinner">Organizing library...</div>
          ) : tasks.map(task => (
            <div key={task.id} className="library-card" onClick={() => handleTaskClick(task.id)}>
              <div className="card-header">
                <span className={`priority-dot ${task.priority}`}></span>
                <span className="category-tag">{task.category || "General"}</span>
              </div>
              <h3>{task.title}</h3>
              <p>{task.description}</p>
              <div className="card-footer">
                <span>{new Date(task.dueDate).toLocaleDateString()}</span>
                <span className="status-label">{task.status}</span>
              </div>
            </div>
          ))}
        </div>

        {/* Pagination Controls */}
        <div className="pagination">
          <button 
            disabled={currentPage === 0} 
            onClick={() => setCurrentPage(p => p - 1)}
          >
            <FaChevronLeft /> Previous
          </button>
          <span className="page-info">Page {currentPage + 1} of {totalPages}</span>
          <button 
            disabled={currentPage >= totalPages - 1} 
            onClick={() => setCurrentPage(p => p + 1)}
          >
            Next <FaChevronRight />
          </button>
        </div>
      </div>
    </div>
  );
}