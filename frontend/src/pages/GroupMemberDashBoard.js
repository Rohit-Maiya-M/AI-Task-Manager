import Sidebar from "./Sidebar";
import { FaTasks, FaBook, FaSearch, FaHistory } from "react-icons/fa";
import Calendar from 'react-calendar';
import 'react-calendar/dist/Calendar.css';
import "./GroupDashBoard.css"; 

export default function GroupMemberDashBoard() {
  return (
    <div className="group-dashboard member-theme" style={{ display: "flex", minHeight: "100vh" }}>
      <Sidebar basePath="/group/member" />

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
        <h1 className="group-heading" style={{ color: "#818cf8" }}>Member Workspace</h1>
        <p className="group-subtext" style={{ color: "#a5b4fc" }}>View your assignments and group contributions.</p>

        <div className="action-buttons">
          <div className="square-btn member-btn">
            <FaTasks size={28} />
            <span>My Tasks</span>
          </div>
          <div className="square-btn member-btn">
            <FaBook size={28} />
            <span>Library</span>
          </div>
          <div className="square-btn member-btn">
            <FaSearch size={28} />
            <span>Search</span>
          </div>
          <div className="square-btn member-btn">
            <FaHistory size={28} />
            <span>Recent</span>
          </div>
        </div>

        <div className="group-panel">
          <h2>Assigned To You</h2>
          <div className="task-item" style={{ borderLeftColor: "#818cf8" }}>Complete Backend Auth Module</div>
          <div className="task-item" style={{ borderLeftColor: "#818cf8" }}>Update API Documentation</div>
        </div>
      </div>

      {/* Right Column with Calendar */}
      <div className="group-right-section" style={{ width: "350px", padding: "1rem", margin: "2rem", zIndex: 1 }}>
        <div className="calendar-section">
          <h2 style={{ marginBottom: "1rem", color: "#818cf8" }}>Task Deadlines</h2>
          <Calendar />
        </div>

        <div className="group-panel" style={{ marginTop: "2rem" }}>
          <h2>Today's Progress</h2>
          <div className="activity-note">You completed 2 tasks today.</div>
        </div>
      </div>
    </div>
  );
}