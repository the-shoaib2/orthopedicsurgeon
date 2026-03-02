# Orthopedic Surgeon Enterprise Platform

A comprehensive, production-ready healthcare management system built with Spring Boot 3.4 and Angular 18.

## 🚀 Quick Start
```bash
# Clone and setup
pnpm install
# Run full stack
docker-compose up --build
```

## 🏗️ Architecture
- **Backend**: Spring Boot 3.4 (Java 21), PostgreSQL, Redis, Flyway.
- **Frontend**: Angular 18, NGRX, ZardUI Component Library.
- **Infrastructure**: Docker, GitHub Actions, Prometheus, Grafana, Loki.
- **Security**: JWT + 2FA, AES-256 PII Encryption, RBAC, Rate Limiting.

## 📁 Modules
- **Appointments**: Booking and slot management with Redis locks.
- **Lab Reports**: Integrated report management with S3 storage.
- **Payments**: Transaction processing and audit trails.
- **Doctors/Patients**: Comprehensive profile and medical history management.
- **Audit**: AOP-based mutation logging for compliance.

## 🛠️ Tools
- **Swagger**: `http://localhost:8080/swagger-ui.html`
- **Grafana**: `http://localhost:3000` (Metrics & Logs)
- **MinIO**: `http://localhost:9001` (File storage console)

## 📄 Documentation
- [Architecture Overview](docs/architecture.md)
- [Environment Setup](docs/environment-setup.md)
- [Deployment Guide](docs/deployment.md)

---
*Built for excellence in Orthopedic Healthcare.*
