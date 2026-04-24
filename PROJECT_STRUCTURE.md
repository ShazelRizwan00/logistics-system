# Project Structure

This repository contains a Spring Boot logistics backend plus a static frontend served from Spring resources.

```text
logistics-system/
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
├── README.md
├── PROJECT_STRUCTURE.md
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
└── src/
    └── main/
        ├── java/com/logistics/
        │   ├── LogisticsApplication.java
        │   ├── config/            # Security, JPA, JWT, OpenAPI
        │   ├── controller/        # Auth, Users, Orders, Shipments, Deliveries, Admin
        │   ├── dto/
        │   │   ├── request/
        │   │   └── response/
        │   ├── entity/            # JPA entities
        │   ├── enums/             # Domain enums
        │   ├── exception/         # Custom exceptions + global handler
        │   ├── repository/        # Spring Data repositories
        │   ├── security/          # JWT filter/provider/principal/details service
        │   └── service/
        │       ├── *.java         # Service interfaces
        │       └── impl/          # Service implementations
        └── resources/
            ├── application.yml
            ├── data.sql
            └── static/
                ├── index.html
                ├── login.html
                ├── register.html
                ├── dashboard.html
                ├── css/
                │   └── styles.css
                └── js/
                    ├── api.js
                    ├── login.js
                    ├── register.js
                    └── dashboard.js
```

## Notes
- `src/main/resources/static` contains the browser UI for registration, login, and dashboard workflows.
- API endpoints remain under `/api/**` and are JWT-protected except explicitly public routes.
- Use `./gradlew` to run Gradle tasks (wrapper jar is committed in `gradle/wrapper`).
