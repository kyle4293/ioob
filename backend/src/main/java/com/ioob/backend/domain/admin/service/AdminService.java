package com.ioob.backend.domain.admin.service;

import com.ioob.backend.domain.auth.dto.UserInfoDto;
import com.ioob.backend.domain.auth.entity.User;
import com.ioob.backend.domain.auth.repository.UserRepository;
import com.ioob.backend.global.exception.CustomException;
import com.ioob.backend.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class AdminService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<UserInfoDto> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            return users.stream()
                    .map(UserInfoDto::from)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new CustomException(ErrorCode.FETCH_USERS_FAILED);
        }
    }

    public UserInfoDto getUser(Long id) {
        return UserInfoDto.from(userRepository.findById(id).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND)));
    }

    @Transactional
    public void deleteUserById(Long id) {
        try {

            userRepository.deleteById(id);
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {
            throw new CustomException(ErrorCode.DELETE_USER_FAILED);
        }
    }
}
