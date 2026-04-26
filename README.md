# Logistics and Delivery Tracking System

A Spring Boot application for order, shipment, and delivery lifecycle management with JWT-based authentication and a simple built-in web frontend.

## Features
- Authentication and registration (`CUSTOMER`, `DELIVERY_AGENT`, `ADMIN`)
- Order placement and order status workflows
- Shipment management and tracking timeline updates
- Delivery assignment and delivery status updates
- Static frontend pages for quick manual usage (`/`, `/login.html`, `/register.html`, `/dashboard.html`)

## Tech Stack
- Java 21
- Spring Boot 
- Spring Security (JWT)
- Spring Data JPA
- H2 / PostgreSQL drivers
- Gradle

## Run Locally
In bash :
   ./gradlew bootRun

Frontend pages
   - `http://localhost:8080/`
   - `http://localhost:8080/login.html`
   - `http://localhost:8080/register.html`
   - `http://localhost:8080/dashboard.html`




