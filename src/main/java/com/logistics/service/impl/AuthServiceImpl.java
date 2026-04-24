package com.logistics.service.impl;

import com.logistics.dto.request.LoginRequest;
import com.logistics.dto.request.RegisterRequest;
import com.logistics.dto.response.AuthResponse;
import com.logistics.entity.Admin;
import com.logistics.entity.Customer;
import com.logistics.entity.DeliveryAgent;
import com.logistics.entity.User;
import com.logistics.enums.UserRole;
import com.logistics.exception.BusinessRuleException;
import com.logistics.exception.DuplicateResourceException;
import com.logistics.repository.UserRepository;
import com.logistics.security.JwtTokenProvider;
import com.logistics.service.AuthService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository        userRepository;
    private final PasswordEncoder       passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider      tokenProvider;

    public AuthServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager,
                           JwtTokenProvider tokenProvider) {
        this.userRepository        = userRepository;
        this.passwordEncoder       = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.tokenProvider         = tokenProvider;
    }

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest req) {
        // Guard: email must be unique across the whole users table
        if (userRepository.existsByContactInfo(req.getContactInfo())) {
            throw new DuplicateResourceException(
                    "Email already registered: " + req.getContactInfo());
        }

        UserRole role = parseRole(req.getRole());
        validateRegistrationFields(req, role);
        String hashedPassword = passwordEncoder.encode(req.getPassword());

        User saved = switch (role) {
            case CUSTOMER -> {
                Customer c = new Customer(req.getName(), req.getContactInfo(),
                                         hashedPassword, req.getAddress());
                yield userRepository.save(c);
            }
            case DELIVERY_AGENT -> {
                DeliveryAgent a = new DeliveryAgent(req.getName(), req.getContactInfo(),
                                                   hashedPassword, req.getVehicleInfo(), true);
                yield userRepository.save(a);
            }
            case ADMIN -> {
                Admin admin = new Admin(req.getName(), req.getContactInfo(), hashedPassword);
                yield userRepository.save(admin);
            }
        };

        String token = tokenProvider.generateToken(saved.getContactInfo(), role.name());
        return new AuthResponse(token, role.name(), saved.getUserId(), saved.getName());
    }

    @Override
    public AuthResponse login(LoginRequest req) {
        // Delegates credential check to Spring Security's AuthenticationManager.
        // BadCredentialsException is thrown automatically if credentials are wrong.
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getContactInfo(), req.getPassword()));

        String token = tokenProvider.generateToken(auth);

        // Re-load user to get role and id for the response
        User user = userRepository.findByContactInfo(req.getContactInfo()).orElseThrow();
        return new AuthResponse(token, user.getRole().name(), user.getUserId(), user.getName());
    }

    private UserRole parseRole(String roleStr) {
        try {
            return UserRole.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Invalid role '" + roleStr + "'. Must be ADMIN, CUSTOMER, or DELIVERY_AGENT.");
        }
    }

    private void validateRegistrationFields(RegisterRequest req, UserRole role) {
        if (role == UserRole.ADMIN) {
            throw new BusinessRuleException("Public self-registration for ADMIN is not allowed.");
        }
        if (role == UserRole.CUSTOMER && (req.getAddress() == null || req.getAddress().isBlank())) {
            throw new BusinessRuleException("Address is required for CUSTOMER registration.");
        }
        if (role == UserRole.DELIVERY_AGENT &&
                (req.getVehicleInfo() == null || req.getVehicleInfo().isBlank())) {
            throw new BusinessRuleException("Vehicle info is required for DELIVERY_AGENT registration.");
        }
    }
}
