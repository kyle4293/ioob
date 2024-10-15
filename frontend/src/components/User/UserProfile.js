import React, { useEffect, useState } from 'react';
import { authService } from '../../services/authService';

const UserProfile = () => {
  const [profile, setProfile] = useState({});

  useEffect(() => {
    const fetchProfile = async () => {
      try {
        const profileData = await authService.getProfile();
        setProfile(profileData);
      } catch (error) {
        console.error('프로필 정보를 가져오는 중 오류 발생:', error);
      }
    };

    fetchProfile();
  }, []);

  return (
    <div className="user-profile">
      <h2>프로필</h2>
      <p>이름: {profile.name}</p>
      <p>이메일: {profile.email}</p>
    </div>
  );
};

export default UserProfile;
