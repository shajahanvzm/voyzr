TripMS — Trip Microservice

Overview
- TripMS is a Spring Boot microservice intended to serve trip-related data within the Voyzr system. It registers itself with the Eureka service registry and is typically accessed via the API Gateway.

Key Facts
- Framework: Spring Boot (Java 17)
- Service name: TripMS
- Default port: 9010
- Service discovery: Eureka Client (defaultZone http://localhost:8070/eureka/)
- Database: MySQL (schema: trips)
- JPA: Hibernate ddl-auto=update (auto-creates/updates tables)
- Observability: Spring Boot Actuator (all endpoints exposed)
- API Docs: springdoc-openapi (Swagger UI)

Prerequisites
- Java 17+
- Maven 3.9+ (or use included mvnw/mvnw.cmd)
- MySQL running locally with a database named trips
  - URL: jdbc:mysql://localhost:3306/trips
  - Username: root
  - Password: (empty by default)
- Eureka Server running on http://localhost:8070 (module EurekaServer in this repo)

Configuration
All main configuration lives in src/main/resources/application.yml. Defaults:
- Server port: 9010
- Spring application name: TripMS
- MySQL connection: jdbc:mysql://localhost:3306/trips
- Actuator endpoints: all exposed

You can override properties at runtime (e.g., using environment variables or application-local.yml). Common overrides:
- spring.datasource.url
- spring.datasource.username
- spring.datasource.password
- eureka.client.service-url.defaultZone

How to Run (locally)
1) Start dependencies (recommended order):
   - MySQL
   - EurekaServer module: cd ../EurekaServer && ./mvnw spring-boot:run
   - Optionally, ApiGateway module
2) Start TripMS:
   - cd TripMS
   - ./mvnw spring-boot:run
   - Or package and run: ./mvnw clean package && java -jar target/tripmS-0.0.1-SNAPSHOT.jar

Endpoints (default port 9010)
- Service status: GET http://localhost:9010/
  - Returns: "TripMS is UP!" when the app is healthy.
- Actuator (all exposed): http://localhost:9010/actuator
  - Health: http://localhost:9010/actuator/health
  - Info:   http://localhost:9010/actuator/info
- OpenAPI/Swagger UI:
  - http://localhost:9010/swagger-ui/index.html
  - (redirect also available at /swagger-ui.html)

Database Setup (local)
- Create database if it doesn’t exist:
  CREATE DATABASE trips CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
- Update credentials in application.yml if your MySQL uses a password.
- Hibernate is configured with ddl-auto=update to evolve schema automatically from entities.

Building
- From this module directory:
  - ./mvnw clean verify
  - Artifacts will be under target/

Troubleshooting
- Cannot connect to MySQL
  - Verify MySQL running on localhost:3306 and database trips exists.
  - Check spring.datasource.* properties.
- Not registering with Eureka
  - Ensure EurekaServer is running at http://localhost:8070/eureka/
  - Check eureka.client.service-url.defaultZone and network connectivity.
- Swagger UI 404
  - Wait for app to finish starting, then use /swagger-ui/index.html
  - Ensure springdoc dependency is present (it is in pom.xml).

License
- Internal/demo project. Refer to repository-level licensing if provided.
