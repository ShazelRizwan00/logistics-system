package com.logistics.service;

import com.logistics.dto.response.UserResponse;
import com.logistics.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponse> getAllUsers(UserRole role, Pageable pageable);
    UserResponse getUserById(Long id);
    UserResponse getCurrentUser(String contactInfo);
    void deleteUser(Long id);
    Page<UserResponse> searchUsers(String keyword, Pageable pageable);
}
