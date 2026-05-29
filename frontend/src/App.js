import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import Login from "./pages/Login";
import ChoicePage from "./pages/ChoicePage";
import PersonalDashBoard from "./pages/PersonalDashBoard";
import GroupChoice from "./pages/GroupChoice";
import GroupAdminDashBoard from "./pages/GroupAdminDashBoard";
import GroupMemberDashBoard from "./pages/GroupMemberDashBoard";
import TaskPage from "./pages/TaskPage";
import ProtectedRoute from "./components/ProtectedRoute"; 
import Library from "./pages/Library";
import GroupCreatePage from "./pages/GroupCreatePage";
import GroupAdminAuth from "./pages/GroupAdminAuth";
import GroupAdminChoice from "./pages/GroupAdminChoice";
import GroupAdminCreate from "./pages/GroupAdminCreate";
import AddGroupMember from "./pages/AddGroupMember";
import ViewGroupMembers from "./pages/ViewGroupMembers";
import AssignedTasksToUser from "./pages/AssignedTasksToUser";
import GroupTaskPage from "./pages/GroupTaskPage";
import GroupAdminEditTask from "./pages/GroupAdminEditTask";
import './App.css';

function App() {
  return (
    <Router>
      <Routes>
        {/* Public Route */}
        <Route path="/" element={<Login />} />
        
        {/* Protected Individual Routes */}
        <Route path="/choice" element={<ProtectedRoute><ChoicePage /></ProtectedRoute>} />
        <Route path="/personal" element={<ProtectedRoute><PersonalDashBoard /></ProtectedRoute>} />
        
        {/* Task Management Routes */}
        <Route path="/personal/task/create" element={
          <ProtectedRoute>
            <TaskPage mode="create" />
          </ProtectedRoute>
        } />

        <Route 
          path="/personal/task/edit/:taskId" 
          element={
            <ProtectedRoute>
              <TaskPage mode="edit" />
            </ProtectedRoute>
          } 
        />

        <Route 
          path="/personal/library" 
          element={
            <ProtectedRoute>
              <Library/>
            </ProtectedRoute>
          } 
        />        
        
        {/* Nested Group Routes */}
        <Route path="/group">
          <Route index element={<ProtectedRoute><GroupChoice /></ProtectedRoute>} />
          <Route path="admin-choice" element={<ProtectedRoute><GroupAdminChoice /></ProtectedRoute>} />
          <Route path="admin-auth" element={<ProtectedRoute><GroupAdminAuth /></ProtectedRoute>} />
          <Route path="admin-create" element={<ProtectedRoute><GroupAdminCreate /></ProtectedRoute>} />
          <Route path="admin" element={<ProtectedRoute><GroupAdminDashBoard /></ProtectedRoute>} />
          <Route path="member" element={<ProtectedRoute><GroupMemberDashBoard /></ProtectedRoute>} />
          <Route path="admin/members" element={<ProtectedRoute><AddGroupMember /></ProtectedRoute>} />
          <Route path="admin/view-members" element={<ProtectedRoute><ViewGroupMembers /></ProtectedRoute>} />
          <Route path="admin/member-tasks" element={<ProtectedRoute><AssignedTasksToUser /></ProtectedRoute>} />
          <Route path="task/create" element={<ProtectedRoute><GroupTaskPage /></ProtectedRoute>} />
          <Route path="task/edit" element={<ProtectedRoute><GroupAdminEditTask /></ProtectedRoute>} />
          <Route path="task/edit/:groupId/:taskId" element={<ProtectedRoute><GroupAdminEditTask /></ProtectedRoute>} />
        </Route>        
      </Routes>
    </Router>
  );
}

export default App;
