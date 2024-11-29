import React, { useContext, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { AuthContext } from '../../context/AuthContext';

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const { login } = useContext(AuthContext);
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      await login({ email, password });
      navigate('/');
    } catch (error) {
      alert(error.message);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-logo">IOOB</div>
      <div className="login-container">
        <h2>로그인</h2>
        <form onSubmit={handleSubmit}>
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
          <button type="submit">로그인</button>
        </form>
        <div className="auth-links">
          <hr></hr>
          <small>비밀번호를 잊으셨나요?</small> <button onClick={() => navigate('/password-reset')}>비밀번호 찾기</button>
          <br></br>
          <small>계정이 없으신가요?</small> <button onClick={() => navigate('/register')}>회원가입</button>
        </div>
      </div>
    </div>
  );
};

export default Login;
