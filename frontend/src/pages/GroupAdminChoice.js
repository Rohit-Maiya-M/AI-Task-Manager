import { Tile } from "@carbon/react";
import { useNavigate } from "react-router-dom";
import { FaUserShield, FaPlusCircle, FaArrowLeft } from "react-icons/fa";
import "./GroupChoice.css"; 

export default function GroupAdminChoice() {
  const navigate = useNavigate();

  return (
    <div className="choice-page-container">
      <button className="back-to-main-btn" onClick={() => navigate("/choice")}>
        <FaArrowLeft /> Back to Main Roles
      </button>

      <div style={{ marginBottom: "40px" }}>
        <h1 style={{ color: "#161616", textAlign: "center" }}>Group Administration</h1>
        <p style={{ textAlign: "center", color: "#525252" }}>Select an administrative action</p>
      </div>

      <div className="choice-row">
        {/* Option 1: Route to Login Page */}
        <Tile
          className="choice-tile secure-tile"
          style={{
            padding: "2rem",
            cursor: "pointer",
            textAlign: "center",
            backgroundColor: "transparent",
            boxShadow: "0 4px 12px rgba(0,0,0,0.6)",
            borderRadius: "12px",
          }}
          onClick={() => navigate("/group/admin-auth")}
        >
          <FaUserShield className="choice-icon" />
          <h2>Login to Group</h2>
          <p>Provide secure keys and workspace IDs to open your management dashboard.</p>
        </Tile>

        {/* Option 2: Route to Creation Page */}
        <Tile
          className="choice-tile create-tile"
          style={{
            padding: "2rem",
            cursor: "pointer",
            textAlign: "center",
            backgroundColor: "transparent",
            boxShadow: "0 4px 12px rgba(0,0,0,0.6)",
            borderRadius: "12px",
          }}
          onClick={() => navigate("/group/admin-create")}
        >
          <FaPlusCircle className="choice-icon" />
          <h2>Create a Group</h2>
          <p>Provision a brand new team database workspace and establish its admin password.</p>
        </Tile>
      </div>
    </div>
  );
}