package com.logistics.service.impl;

import com.logistics.dto.response.UserResponse;
import com.logistics.entity.User;
import com.logistics.enums.UserRole;
import com.logistics.exception.ResourceNotFoundException;
import com.logistics.repository.UserRepository;
import com.logistics.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(UserRole role, Pageable pageable) {
        Page<User> page = (role == null)
                ? userRepository.findAll(pageable)
                : userRepository.findByRole(role, pageable);
        return page.map(UserResponse::from);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        return UserResponse.from(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(String contactInfo) {
        User user = userRepository.findByContactInfo(contactInfo)
                .orElseThrow(() -> new ResourceNotFoundException("User", "contactInfo", contactInfo));
        return UserResponse.from(user);
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User", "id", id);
        }
        userRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String keyword, Pageable pageable) {
        Page<User> page = userRepository.searchByKeyword(keyword, pageable);
        return page.map(UserResponse::from);
    }
}
