import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import Header from './components/Layout/Header';
import Footer from './components/Layout/Footer';
import Home from './components/Home';
import ProjectList from './components/Project/ProjectList';
import ProjectDetails from './components/Project/ProjectDetails';
import AdminPage from './components/Admin/AdminPage';
import UserDetails from './components/Admin/UserDetails';
import UserProfile from './components/User/UserProfile';
import Login from './components/Auth/Login';
import Register from './components/Auth/Register';
import TaskDetails from './components/Task/TaskDetails';
import './styles/app.css';

const App = () => {
  return (
    <AuthProvider>
      <Router>
        <div className="page-wrapper">
          <Header />
          <main className="main-content">
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/projects" element={<ProjectList />} />
              <Route path="/projects/:id" element={<ProjectDetails />} />
              <Route path="/projects/:projectId/boards/:boardId/tasks/:taskId" element={<TaskDetails />} />
              <Route path="/admin" element={<AdminPage />} />
              <Route path="/admin/users/:id" element={<UserDetails />} />
              <Route path="/profile" element={<UserProfile />} />
            </Routes>
          </main>
          <Footer />
        </div>
      </Router>
    </AuthProvider>
  );
};

export default App;
