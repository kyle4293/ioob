package com.ioob.backend.config;

import com.ioob.backend.entity.RoleName;
import com.ioob.backend.entity.User;
import com.ioob.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 초기 Admin 계정이 존재하는지 확인
        if (userRepository.count() == 0) {
            // Admin 계정 생성 (builder 패턴 사용)
            User adminUser = User.builder()
                    .email("admin@admin.com")
                    .name("Admin")
                    .password(passwordEncoder.encode("admin1234"))  // 비밀번호 암호화
                    .enabled(true)  // 이메일 인증 상태 true
                    .role(RoleName.ROLE_ADMIN)  // 관리자 역할 부여
                    .build();

            userRepository.save(adminUser);

            System.out.println("Admin user initialized with email: admin@admin.com and password: admin1234");
        }
    }
}
