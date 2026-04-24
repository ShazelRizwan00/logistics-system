package com.logistics.security;

import com.logistics.entity.User;
import com.logistics.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spring Security's bridge between our User entity and its own UserDetails model.
 *
 * Role authority format: "ROLE_ADMIN", "ROLE_CUSTOMER", "ROLE_DELIVERY_AGENT"
 * – the "ROLE_" prefix is required by Spring Security's hasRole() checks.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String contactInfo) throws UsernameNotFoundException {
        User user = userRepository.findByContactInfo(contactInfo)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with contactInfo: " + contactInfo));

        return new UserPrincipal(user);
    }
}
