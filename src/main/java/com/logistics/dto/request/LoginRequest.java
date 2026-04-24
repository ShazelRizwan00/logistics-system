package com.logistics.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** Request body for POST /api/auth/login */
public class LoginRequest {

    @NotBlank(message = "Email is required")
    @Email
    private String contactInfo;

    @NotBlank(message = "Password is required")
    private String password;

    // ─── Getters & Setters ───────────────────────────────────────────────────

    public String getContactInfo() { return contactInfo; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
