package com.logistics.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Configures the OpenAPI / Swagger UI documentation.
 *
 * The @SecurityScheme annotation tells Swagger UI to show a "Authorize"
 * button where users can paste their JWT. All controllers annotated with
 * @SecurityRequirement(name = "bearerAuth") will then include the header.
 *
 * Access Swagger UI at: http://localhost:8080/swagger-ui.html
 */
@Configuration
@OpenAPIDefinition(
    info = @Info(
        title       = "Logistics & Delivery Tracking API",
        version     = "1.0.0",
        description = "Production-grade backend for managing orders, shipments, deliveries, and tracking",
        contact     = @Contact(name = "Logistics Team", email = "dev@logistics.com")
    )
)
@SecurityScheme(
    name   = "bearerAuth",
    type   = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description  = "Enter your JWT token (without the 'Bearer ' prefix)"
)
public class OpenApiConfig {
    // Configuration is entirely annotation-driven.
}
