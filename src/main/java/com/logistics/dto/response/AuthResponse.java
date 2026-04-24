package com.logistics.dto.response;

/** Returned after successful login or registration. */
public class AuthResponse {

    private String token;
    private String tokenType = "Bearer";
    private String role;
    private Long userId;
    private String name;

    public AuthResponse() {}

    public AuthResponse(String token, String role, Long userId, String name) {
        this.token = token;
        this.role = role;
        this.userId = userId;
        this.name = name;
    }

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
