# Project Structure

This project is a `Spring Boot` logistics and delivery tracking backend built with `Gradle` and `Java 21`.

## Root Layout

```text
logistics-system/
|-- build.gradle
|-- settings.gradle
|-- README.md
|-- .gitignore
|-- gradle/
|   `-- wrapper/
|       `-- gradle-wrapper.properties
|-- src/
|   `-- main/
|       |-- java/
|       |   `-- com/
|       |       `-- logistics/
|       |           |-- LogisticsApplication.java
|       |           |-- config/
|       |           |-- controller/
|       |           |-- dto/
|       |           |   |-- request/
|       |           |   `-- response/
|       |           |-- entity/
|       |           |-- enums/
|       |           |-- exception/
|       |           |-- repository/
|       |           |-- security/
|       |           `-- service/
|       |               `-- impl/
|       `-- resources/
|           |-- application.yml
|           `-- data.sql
|-- build/
|-- .gradle/
|-- .idea/
|-- .vscode/
|-- app.err.log
|-- app.out.log
|-- bootrun.err.log
`-- bootrun.out.log
```

## Java Package Structure

### `com.logistics`
- `LogisticsApplication.java`: Spring Boot entry point.

### `com.logistics.config`
- Application configuration classes.
- Includes security, JPA, JWT properties, and OpenAPI setup.

### `com.logistics.controller`
- REST API layer.
- Handles endpoints for auth, admin, users, orders, shipments, and deliveries.

### `com.logistics.dto.request`
- Request payload models received from clients.
- Examples: login, registration, order creation, shipment creation, status updates.

### `com.logistics.dto.response`
- Response payload models returned by the API.
- Includes API wrappers, paged responses, and entity-specific response objects.

### `com.logistics.entity`
- JPA domain models and database-mapped entities.
- Includes users, customers, admins, orders, shipments, packages, payments, deliveries, and tracking info.

### `com.logistics.enums`
- Shared enum types for business states.
- Includes user role, order status, shipment status, delivery status, and payment status.

### `com.logistics.exception`
- Custom exceptions and centralized exception handling.

### `com.logistics.repository`
- Spring Data JPA repositories for persistence access.

### `com.logistics.security`
- Authentication and authorization support classes.
- Includes JWT provider, authentication filter, entry point, principal, and custom user details service.

### `com.logistics.service`
- Service interfaces for application business logic.

### `com.logistics.service.impl`
- Concrete implementations of service interfaces.

## Resources

### `src/main/resources/application.yml`
- Main application configuration.

### `src/main/resources/data.sql`
- Seed or initialization SQL script.

## Build And Tooling

### `build.gradle`
- Gradle build configuration.
- Uses Spring Boot `3.3.4`, dependency management, and Java `21`.

### `settings.gradle`
- Gradle project settings.

### `gradle/wrapper`
- Gradle wrapper configuration for consistent builds.

## Generated And Local-Only Folders

### `build/`
- Generated build outputs.

### `.gradle/`
- Local Gradle cache and metadata.

### `.idea/`, `.vscode/`
- IDE/editor configuration files.

### `*.log`
- Runtime and boot logs generated during local execution.
