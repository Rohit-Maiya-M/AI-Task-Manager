import { Tile } from "@carbon/react";
import { useNavigate } from "react-router-dom";
import "./ChoicePage.css";

export default function ChoicePage() {
  const navigate = useNavigate();

  return (
    <div
      style={{
        display: "flex",
        height: "100vh",
        justifyContent: "center",   // centers horizontally
        alignItems: "center",       // centers vertically
        background: "linear-gradient(135deg, #872ef5 10%, #c7dce8 50%, #c7dce8 50%, #872ef5 90%)",
        flexDirection: "column"
      }}
    >
      <div 
        style={{
          marginBottom: "40px",
        }}
      >
        <h1>AI Task Manager</h1>
      </div>

      <div
        className="choice-row"
        style={{
          display: "flex", 
          flexDirection: "row",
          justifyContent: "center"
        }}
      >
        {/* Personal Tile */}
        <Tile
          className="choice-tile"
          style={{ 
            padding: "2rem", 
            cursor: "pointer", 
            textAlign: "center",
            backgroundColor: "transparent",
            boxShadow: "0 4px 12px rgba(0,0,0,0.6)", 
            borderRadius: "12px",
            marginRight: "20px",
          }}
          onClick={() => navigate("/personal")}
        >
          <h2>Personal</h2>
          <p>Manage your personal tasks and goals.</p>
        </Tile>

        {/* Group Tile */}
        <Tile
          className="choice-tile" 
          style={{ 
            padding: "2rem", 
            cursor: "pointer", 
            textAlign: "center",
            backgroundColor: "transparent",
            boxShadow: "0 4px 12px rgba(0,0,0,0.6)", 
            borderRadius: "12px",
          }}
          onClick={() => navigate("/group")}
        >
          <h2>Group</h2>
          <p>Collaborate with your team or friends.</p>
        </Tile>
      </div>
    </div>
  );
}