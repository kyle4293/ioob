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
        if (error.response && error.response.status === 403) {
          alert('접근 권한이 없습니다.');
          navigate('/'); 
        } else {
          console.error('사용자 목록을 가져오는 중 오류 발생:', error);
        }
      }
    };

    fetchUsers();
  }, [navigate]);

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
      <table>
        <thead>
          <tr>
              <th>ID</th>
              <th>이름</th>
              <th>이메일</th>
              <th>권한</th>
              <th>상태</th>
              <th>관리</th>
          </tr>
        </thead>
        <tbody>
            {users.map(user => (
              <tr key={user.id}>
                <td>{user.id}</td>
                <td>{user.name}</td>
                <td onClick={() => handleUserClick(user.id)} style={{ cursor: 'pointer', color: 'blue' }}>{user.email}</td>
                <td>{user.role}</td>
                <td>{user.enabled.toString()}</td>
                <td><button onClick={() => handleDeleteUser(user.id)}>삭제</button></td>
              </tr>
            ))}
        </tbody>
      </table>
    </div>
  );
};

export default AdminPage;
