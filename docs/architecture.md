# Orthopedic Surgeon Platform - Phase 5 Documentation

## System Architecture

The enterprise platform is built on a modern distributed architecture ensuring high availability, security, and performance.

### 1. Monorepo Structure (Turborepo)
- `apps/api`: Spring Boot 3.4 Backend (Java 21).
- `apps/admin-dashboard`: Angular 18 Portal for administrative staff.
- `apps/public-site`: Angular 18 Patient portal.
- `packages/ui`: Shared ZardUI component library.
- `packages/auth`: Shared authentication utilities and state.

### 2. Core Security (OWASP Top 10 Hardened)
- **A01: Broken Access Control**: Scoped RBAC using Spring Security and Custom Permission Evaluators for owner-based data isolation.
- **A02: Cryptographic Failures**: Transparent AES encryption for PII using JPA AttributeConverters.
- **A05: Security Misconfiguration**: Rate limiting via Bucket4j and secured Actuator endpoints.
- **A09: Logging & Monitoring**: AOP-based audit logging for all critical mutations (@LogMutation).

### 3. Performance & Resilience
- **Database**: PostgreSQL 16 with optimized B-Tree and GIN indexes for geospatial/JSON data.
- **Caching**: Multi-level Redis caching for high-traffic hospital/doctor listings.
- **Async Processing**: Dedicated thread pool for non-blocking SMS/Email notifications via Twilio & JavaMail.
- **Connection Pooling**: HikariCP tuned for high throughput.

### 4. Infrastructure & Deployment
- **Containerization**: Multi-stage Dockerfiles with Layered JARs for the backend and Nginx for the frontend.
- **Orchestration**: Docker Compose for local development and integration testing.
- **CI/CD**: GitHub Actions pipeline covering unit tests, integration tests, and container registry pushes.
- **Monitoring**: Prometheus scraping via Micrometer registry with custom counters for business events.

## API Integration Guide

See [Swagger UI](http://localhost:8080/swagger-ui.html) for full API documentation.

### Authentication
JWT-based authentication with:
- `HttpOnly` refresh token cookies.
- TOTP-based 2FA for administrative accounts.
- OAuth2.0 support (Google/GitHub).
