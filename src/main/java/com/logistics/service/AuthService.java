package com.logistics.service;

import com.logistics.dto.request.LoginRequest;
import com.logistics.dto.request.RegisterRequest;
import com.logistics.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
