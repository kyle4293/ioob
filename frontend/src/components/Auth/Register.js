import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authService } from '../../services/authService';

const Register = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await authService.register({ email, password, name });
      alert('회원가입 성공');
      navigate('/login');
    } catch (error) {
      alert('회원가입 실패');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-logo">IOOB</div>
      <div className="register-container">
        <h2>회원가입</h2>
        <form onSubmit={handleSubmit}>
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            placeholder="이름"
            required
          />
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="이메일"
            required
          />
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="비밀번호"
            required
          />
          <button type="submit" disabled={loading}>회원가입</button>
          {loading && <div className="loading-spinner">로딩 중...</div>}
        </form>
        <div className="auth-links">
          <hr></hr>
          <small>계정이 있으신가요?</small> <button onClick={() => navigate('/login')}>로그인</button>
        </div>
      </div>
    </div>
  );
};

export default Register;
