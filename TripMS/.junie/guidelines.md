TripMS – Developer Quick Guidelines

Purpose
- Help you work productively on the Trip Microservice (TripMS) with a fast, practical reference.

Tech Stack
- Java 17, Maven (use module wrapper: ./mvnw)
- Spring Boot (Web, Validation, Data JPA, Actuator)
- Spring Cloud Netflix Eureka Client
- Database: MySQL (runtime), H2 (tests)
- OpenAPI: springdoc-openapi (Swagger UI)

Module Layout
- src/main/java/com/voyzr/tripms
  - controller | service | service/impl | repository | entity | dto | exception | config
- src/main/resources/application.yml (port 9010, DB config, Eureka URL)
- src/test mirrors main packages; H2 config in src/test/resources/application-test.yml

How to Run (local)
1) Start dependencies:
   - MySQL with database `trips`
   - EurekaServer (from repo root: cd ../EurekaServer && ./mvnw spring-boot:run)
2) Start TripMS:
   - cd TripMS && ./mvnw spring-boot:run
   - Or package and run: ./mvnw clean package && java -jar target/tripmS-0.0.1-SNAPSHOT.jar

Quick Smoke
- Status:   curl http://localhost:9010/
- Health:   curl http://localhost:9010/actuator/health
- Swagger:  http://localhost:9010/swagger-ui/index.html

Configuration (override via env vars)
- SPRING_DATASOURCE_URL=jdbc:mysql://localhost:3306/trips
- SPRING_DATASOURCE_USERNAME=root
- SPRING_DATASOURCE_PASSWORD=<your_pwd>
- EUREKA_CLIENT_SERVICE-URL_DEFAULTZONE=http://localhost:8070/eureka/

Tests (H2, no MySQL required)
- All:              ./mvnw test
- Verify + package: ./mvnw clean verify
- Single class:     ./mvnw -Dtest=TripControllerTest test
- Single method:    ./mvnw -Dtest=TripControllerTest#update_returns200 test

Coding Conventions
- Controllers thin; business logic in services.
- Use DTOs in APIs; do not expose JPA entities directly.
- Validate request DTOs with jakarta.validation; rely on GlobalExceptionHandler.
- Throw meaningful RuntimeExceptions in services; map to proper HTTP codes centrally.
- Use @Slf4j for logging; avoid secrets in logs.

Repository & Data
- Spring Data JPA repositories only (no business logic in repositories).
- Local dev DB: create if needed:
  CREATE DATABASE trips CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

Notes
- Default port: 9010; service registers with Eureka at http://localhost:8070/eureka/.
- Actuator endpoints are exposed for local debugging.

Troubleshooting
- DB connection errors: verify MySQL is up, DB `trips` exists, credentials match env.
- Eureka registration: ensure EurekaServer is running and defaultZone URL is correct.
- Swagger 404: wait for app startup and use /swagger-ui/index.html.
