import { SideNav, SideNavItems } from "@carbon/react";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import "./Sidebar.css";

export default function Sidebar({ basePath = "/choice", homePath, onSearchClick, recentItems = [] }) {
  const navigate = useNavigate();
  const [width, setWidth] = useState(260);
  const minWidth = 200;
  const maxWidth = 400;
  const resolvedHomePath = homePath || basePath || "/choice";

  const handleMouseDown = (e) => {
    e.preventDefault();
    const handleMouseMove = (moveEvent) => {
      let newWidth = moveEvent.clientX;
      if (newWidth < minWidth) newWidth = minWidth;
      if (newWidth > maxWidth) newWidth = maxWidth;
      setWidth(newWidth);
      document.documentElement.style.setProperty('--sidebar-width', `${newWidth}px`);
    };
    const handleMouseUp = () => {
      document.removeEventListener("mousemove", handleMouseMove);
      document.removeEventListener("mouseup", handleMouseUp);
    };
    document.addEventListener("mousemove", handleMouseMove);
    document.addEventListener("mouseup", handleMouseUp);
  };

  return (
    <div 
      className="sidebar-resizable-wrapper" 
      style={{ width: `${width}px`, position: "fixed", top: 0, left: 0, height: "100vh", zIndex: 100 }}
    >
      <SideNav 
        isFixedNav 
        expanded 
        className="custom-side-nav" 
        style={{ background: "var(--sidebar-bg, #1e293b)", height: "100vh", width: "100%", display: "flex", flexDirection: "column" }}
      >
        <SideNavItems>
          <div className="sidebar-buttons">
            <button type="button" className="sidebar-btn" onClick={() => navigate(resolvedHomePath)}>
              Home
            </button>
            
            <button
              type="button"
              className="sidebar-btn" 
              onClick={() => onSearchClick && onSearchClick()}
            >
              Search
            </button>
            
            <div className="recents-section">
              <span className="sidebar-label">Recents</span>
              <div className="recents-list">
                {recentItems.length > 0 ? (
                  recentItems.map(item => (
                    <div 
                      key={item.id} 
                      className="recent-task-item" 
                      onClick={item.onClick}
                    >
                      {item.title}
                    </div>
                  ))
                ) : (
                  <div className="recent-task-empty">No recent items</div>
                )}
              </div>
            </div>
            
            <button type="button" className="sidebar-btn" onClick={() => navigate(`${basePath}/aiagent`)}>
              AI Agent
            </button>
          </div>
        </SideNavItems>
        <div style={{ flexGrow: 1 }} />
      </SideNav>

      <div 
        className="drag-handle" 
        onMouseDown={handleMouseDown} 
        style={{ position: "absolute", top: 0, right: "-3px", width: "6px", cursor: "ew-resize", height: "100%", backgroundColor: "transparent", zIndex: 101 }} 
      />
    </div>
  );
}
