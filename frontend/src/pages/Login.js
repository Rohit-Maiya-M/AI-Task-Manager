import { useState } from "react";
import { TextInput, PasswordInput, Button, Tile } from "@carbon/react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

export default function Login(){
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const navigate = useNavigate();

    const handleLogin = async () => {
        try{
            const response = await axios.post("http://localhost:8080/auth/login", {
                username,
                password
            });

            console.log(response.data);

            if (response.data.token) {
                localStorage.setItem("token", response.data.token); // Save the token
                navigate("/choice");
            }
            else{
                alert("Invalid credentials!");
            }
        }
        catch(error){
            console.error("Login failed: ", error);
        }
    }

    return (
        <div
            style={{
                display: "flex",
                height: "100vh",
                justifyContent: "center",   // centers horizontally
                alignItems: "center",       // centers vertically
                background: "linear-gradient(135deg, #872ef5 10%, #c7dce8 50%, #c7dce8 50%, #872ef5 90%)"


            }}
            >
            

            <Tile
                style={{
                flex: 1,                // allows the Tile to grow/shrink with available space
                position: "relative",
                overflow: "hidden",
                maxHeight: "700px",
                minHeight: "500px",
                maxWidth: "1300px",      // optional: prevents it from becoming too wide
                minWidth: "300px",      // optional: ensures it doesn’t collapse too small
                backgroundColor: "transparent",
                boxShadow: "0 4px 12px rgba(0,0,0,0.1)", // optional: keep a shadow so it stands out
                color: "#161616",
                padding: "2rem",                
                borderRadius: "12px",
                color: "#161616",
                display: "flex",
                flexDirection: "column",
                justifyContent: "center"
                }}
            >
                <div 
                style={{
                    position: "absolute",

                    top: "-250px",
                    left: "-70px",                            
                    width: "750px",         
                    height: "750px",
                    borderRadius: "50%",
                    background: "linear-gradient(135deg, #872ef5 10%, #c7dce8 50%, #c7dce8 50%, #872ef5 90%)",
                    display: "flex",
                    justifyContent: "center",  // center horizontally
                    alignItems: "center"   ,
                    flexDirection: "column"
                }}  
                
                
            >
                <h1
                style={{
                    backgroundColor: "transparent",
                marginTop: "20%",
                    color: "#872ef5"
                }}
                >Welcome</h1>

                <h2
                    style={{
                        backgroundColor: "transparent",
                        marginTop: "1rem",
                        color: "#872ef5"
                    }}>AI Task Manager</h2>

                <p
                    style={{
                        backgroundColor: "transparent",
                        marginTop: "1rem",
                        width: "60%",
                        color: "#872ef5",
                        marginBottom: "1.5rem",
                        fontSize: "0.8rem",
                        lineHeight: "1.5"
                        
                    }}>Your intelligent companion for organizing tasks, boosting productivity, and staying ahead of deadlines. With smart automation, voice-powered assistance, and AI-driven planning, you’ll spend less time managing work and more time achieving results. Sign in to unlock a workspace designed to help you focus on what truly matters</p>
            </div>
            
                

                <div 
                style={{
                    position: "absolute",

                    top: "375px",
                    left: "1170px",                            
                    minWidth: "150px",         
                    minHeight: "150px",
                    borderRadius: "50%",
                    background: "linear-gradient(135deg, #872ef5 10%, #c7dce8 50%, #c7dce8 50%, #872ef5 90%)"
                }}  
            />

            <div 
                style={{
                    position: "absolute",

                    top: "375px",
                    left: "770px",                            
                    minWidth: "120px",         
                    minHeight: "120px",
                    borderRadius: "50%",
                    background: "linear-gradient(135deg, #872ef5 10%, #c7dce8 50%, #c7dce8 50%, #872ef5 90%)"
                }}  
            />

                <Tile
                    style={{
                flex: 1,                // allows the Tile to grow/shrink with available space
                position: "relative",
                overflow: "hidden",
                left: "800px",

                maxHeight: "400px",
                minHeight: "200px",
                maxWidth: "300px",      // optional: prevents it from becoming too wide
                minWidth: "300px",      // optional: ensures it doesn’t collapse too small
                backgroundColor: "transparent",
                boxShadow: "0 4px 12px rgba(0,0,0,0.6)", // optional: keep a shadow so it stands out
                color: "#161616",
                padding: "2rem",                
                borderRadius: "12px",
                color: "#161616",
                display: "flex",
                flexDirection: "column",
                justifyContent: "flex-start",
                alignItems: "stretch"
                }}
                >
                    <h1
                        style={{           
                            marginBottom: "3rem",                   
                            textAlign: "center",    
                            color: "#872ef5",
                        }}
                    >Sign In</h1>
                    <TextInput id="username" labelText="Username" 
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    style={{
                        marginBottom: "2rem",
                        borderRadius: "12px",
                        width: "95%",
                        padding: "6px",
                        backgroundColor: "transparent",
                        borderColor: "#1b3bd9",
                        borderWidth: "2px",                        
                        
                    }}/>


                    <PasswordInput id="password" labelText="Password" 
                    value={password}
                    onChange = {(e) => setPassword(e.target.value)}
                    style={{
                        marginBottom: "1rem",
                        borderRadius: "12px",
                        width: "95%",
                        padding: "6px",
                        backgroundColor: "transparent",
                        borderColor: "#1b3bd9",
                        borderWidth: "2px",  
                    }}
                    hidePasswordLabel="Hide"   // custom text
                    showPasswordLabel="Show" 
                     />

                     <Button kind="primary"
                     onClick={handleLogin}
                     style={{ 
                        marginTop: "1rem",
                        borderRadius: "12px",
                        width: "100%",
                        padding: "6px",
                        backgroundColor: "transparent",
                        borderColor: "#1b3bd9",
                        borderWidth: "2px",      
                        transition: "background 0.3s ease, transform 0.2s ease"                    
                        }}
                        >
    Sign in
  </Button>

                </Tile>


                {/* inputs and buttons go here */}
            </Tile>
</div>
    );

}

