package com.logistics.config;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;
@Configuration
@OpenAPIDefinition(
    info = @Info(    //telling openapi about  API
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
//API documentation configuration layer
