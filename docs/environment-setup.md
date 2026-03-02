# Environment Setup Guide

## 1. Prerequisites
- Docker & Docker Compose
- Node.js 20+ & pnpm 9+
- Java 21 (Temurin recommended)
- Maven 3.9+

## 2. Initial Setup
1. Copy `.env.production.example` to `.env`.
2. Update sensitive values (JWT secrets, API keys).
3. Run `pnpm install` in the root.

## 3. Local Development (Docker)
Run the full stack locally:
```bash
docker-compose up --build
```
Services will be available at:
- Public Site: `http://localhost:4200`
- Admin Dashboard: `http://localhost:4201`
- Backend API: `http://localhost:8080`
- Grafana: `http://localhost:3000`
- Prometheus: `http://localhost:9090`
- MinIO Console: `http://localhost:9001`

## 4. Database Migrations
Flyway migrations are handled automatically by the Spring Boot application on startup.
To manually run migrations:
```bash
cd apps/api && mvn flyway:migrate
```

## 5. Security Notes
- Ensure `PII_ENCRYPTION_KEY` is a 32-character string for AES-256.
- Rotate `JWT_SECRET` every 30 days in production.
