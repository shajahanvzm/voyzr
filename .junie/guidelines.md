Voyzr Monorepo – Developer Quick Guidelines

Purpose
- Help you get productive fast across these Spring Boot services: ApiGateway, EurekaServer, TripMS, UserMs.

Tech Stack
- Java 17, Maven (use module-level wrappers: ./mvnw or mvnw.cmd)
- Spring Boot, Spring Cloud (Eureka Client/Server, Gateway)
- JPA/Hibernate, MySQL (runtime), H2 (tests)
- springdoc-openapi (Swagger UI), Actuator

Repository Layout
- Each service lives in its own top-level folder (not a single multi-module Maven build):
  - ApiGateway (port 8080)
  - EurekaServer (port 8070)
  - TripMS (port 9010, DB: trips)
  - UserMs (default Spring Boot port unless overridden)
- Standard Spring structure per module:
  - src/main/java … package per bounded context
    - controller | service | service/impl | repository | entity | dto | exception | config
  - src/main/resources/application.yml (override via env vars or profiles)
  - src/test mirrors main packages; module-specific application-test.yml where needed

Conventions & Best Practices
- Layering: Controller → Service → Repository. Keep controllers thin; put business logic in services.
- DTO vs Entity: Do not expose entities on API. Map to DTOs in controllers/services.
- Validation: Use jakarta.validation annotations on DTOs; let GlobalExceptionHandler translate errors.
- Exceptions: Throw meaningful RuntimeExceptions in services; ensure centralized handling returns proper HTTP codes.
- Logging: Use slf4j @Slf4j; prefer structured, actionable logs; avoid logging secrets.
- Testing: Prefer unit tests with Mockito/MockMvc; for data access use H2 and slice tests. Avoid hitting MySQL in tests.
- OpenAPI: Keep API docs accurate; check Swagger at /swagger-ui/index.html when app is running.

Local Setup
Prereqs: Java 17+, MySQL (for TripMS) with database trips.
- Create DB (if needed): CREATE DATABASE trips CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
- Default MySQL URL: jdbc:mysql://localhost:3306/trips (user: root, no password by default). Override via env vars or application-local.yml.

Run Order (new terminal per service is fine)
1) EurekaServer
   - cd EurekaServer && ./mvnw spring-boot:run
   - Dashboard: http://localhost:8070/
2) ApiGateway (optional during backend-only dev)
   - cd ApiGateway && ./mvnw spring-boot:run
   - Gateway: http://localhost:8080/
3) TripMS
   - cd TripMS && ./mvnw spring-boot:run
   - Service: http://localhost:9010/
   - Actuator: http://localhost:9010/actuator
   - Swagger:  http://localhost:9010/swagger-ui/index.html
4) UserMs (if needed for your feature)
   - cd UserMs && ./mvnw spring-boot:run

Build & Package (per module)
- ./mvnw clean package
- Run JAR (example): java -jar target/<artifactId>-<version>.jar

Running Tests (per module)
- All tests: ./mvnw test
- Verify + package: ./mvnw clean verify
- Single test class: ./mvnw -Dtest=TripControllerTest test
- Single method: ./mvnw -Dtest=TripControllerTest#update_returns200 test

Quick API Smoke (TripMS)
- Status: curl http://localhost:9010/
- Health:  curl http://localhost:9010/actuator/health
- Swagger UI: open http://localhost:9010/swagger-ui/index.html

Configuration Tips
- Override via env vars, e.g. SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD,
  EUREKA_CLIENT_SERVICE-URL_DEFAULTZONE.
- Keep secrets out of git; prefer env vars or a local, ignored application-local.yml.

Code Organization Checklist (when adding features)
- Controller: request/response DTOs, validation annotations, proper HTTP codes.
- Service: business logic, input validation, domain rules, exceptions.
- Repository: Spring Data interfaces only; no business logic.
- Tests: unit tests for service/controller, repository tests with H2.
- Docs: update READMEs/OpenAPI descriptions if endpoints change.

Contribution Workflow
- Small, focused PRs; include tests and a brief rationale.
- Branch naming: feature/<short-name>, fix/<short-name>, chore/<short-name>.
- Commit messages: imperative mood, reference context (e.g., TripMS: add trip update validation).

Troubleshooting
- Can’t register with Eureka: ensure EurekaServer is running at http://localhost:8070/eureka/
- Swagger 404: wait for app start, use /swagger-ui/index.html
- DB errors in TripMS: verify MySQL is up and credentials/URL match your env.
