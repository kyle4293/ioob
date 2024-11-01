import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { adminService } from '../../services/adminService';

const AdminPage = () => {
  const [users, setUsers] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        const data = await adminService.getUsers();
        setUsers(data);
      } catch (error) {
        console.error('사용자 목록을 가져오는 중 오류 발생:', error);
      }
    };

    fetchUsers();
  }, []);

  const handleDeleteUser = async (id) => {
    try {
      await adminService.deleteUser(id);
      setUsers(users.filter((user) => user.id !== id));
    } catch (error) {
      console.error('사용자 삭제 중 오류 발생:', error);
      alert('사용자 삭제 실패');
    }
  };

  const handleUserClick = (id) => {
    navigate(`./users/${id}`);
  };

  return (
    <div className="admin-page">
      <h2>관리자 페이지</h2>
      <ul>
        {users.map((user) => (
          <li key={user.id}>
            <span onClick={() => handleUserClick(user.id)} style={{ cursor: 'pointer', color: 'blue' }}>
              {user.email}
            </span>
            <button onClick={() => handleDeleteUser(user.id)}>삭제</button>
          </li>
        ))}
      </ul>
    </div>
  );
};

export default AdminPage;
