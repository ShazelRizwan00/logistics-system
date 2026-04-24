package com.logistics.enums;

/**
 * Application roles – used as the discriminator value in SINGLE_TABLE
 * inheritance AND as the Spring Security authority (prefixed with "ROLE_").
 */
public enum UserRole {
    ADMIN,
    CUSTOMER,
    DELIVERY_AGENT
}
