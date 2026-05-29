import { Tile } from "@carbon/react";
import { useNavigate } from "react-router-dom";
import "./GroupChoice.css";

export default function GroupChoice() {
  const navigate = useNavigate();

  return (
    <div
      className="choice-page-container"
      style={{
        display: "flex",
        height: "100vh",
        justifyContent: "center",
        alignItems: "center",
        background: "linear-gradient(135deg, #872ef5 10%, #c7dce8 50%, #c7dce8 50%, #872ef5 90%)",
        flexDirection: "column"
      }}
    >
      <div style={{ marginBottom: "40px" }}>
        <h1 style={{ color: "#161616" }}>Group Management</h1>
        <p style={{ textAlign: "center", color: "#525252" }}>Select your access role</p>
      </div>

      <div className="choice-row" style={{ display: "flex", gap: "20px" }}>
        {/* FIXED: Group Admin Card now targets the Admin Entry Gateway */}
        <Tile
          className="choice-tile"
          style={{
            padding: "2rem",
            cursor: "pointer",
            textAlign: "center",
            backgroundColor: "transparent",
            boxShadow: "0 4px 12px rgba(0,0,0,0.6)",
            borderRadius: "12px",
            width: "280px"
          }}
          onClick={() => navigate("/group/admin-choice")}
        >
          <h2>Group Admin</h2>
          <p>Create tasks, manage members, and oversee projects.</p>
        </Tile>

        {/* Group Member Card */}
        <Tile
          className="choice-tile"
          style={{
            padding: "2rem",
            cursor: "pointer",
            textAlign: "center",
            backgroundColor: "transparent",
            boxShadow: "0 4px 12px rgba(0,0,0,0.6)",
            borderRadius: "12px",
            width: "280px"
          }}
          onClick={() => navigate("/group/member")}
        >
          <h2>Group Member</h2>
          <p>View assignments and update your task progress.</p>
        </Tile>
      </div>
    </div>
  );
}