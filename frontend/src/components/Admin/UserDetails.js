import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { adminService } from '../../services/adminService';

const UserDetails = () => {
  const { id } = useParams();  // 컴포넌트 최상단에서 useParams 호출
  const [profile, setProfile] = useState({});

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const profileData = await adminService.getUser(id);
        setProfile(profileData);
      } catch (error) {
        console.error('프로필 정보를 가져오는 중 오류 발생:', error);
      }
    };

    fetchProfile();
  }, [id]);  // id를 의존성 배열에 추가하여 변경 시 재실행

  return (
    <div className="user-profile">
      <h2>프로필</h2>
      <p>id: {profile.id}</p>
      <p>이름: {profile.name}</p>
      <p>이메일: {profile.email}</p>
      <p>role: {profile.role}</p>
      <p>enabled: {profile.enabled ? "True" : "False"}</p>
    </div>
  );
};

export default UserDetails;
