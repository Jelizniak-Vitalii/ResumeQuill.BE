# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Run Commands

```bash
# Build and run full dev environment (PostgreSQL, backend, monitoring stack)
./build-dev.sh

# Start only PostgreSQL for local IDE development
./build-dev-local.sh

# Production deployment
./build.sh

# Maven commands
mvn clean package                    # Build fat JAR
mvn clean package -DskipTests        # Build without tests
mvn test                             # Run all tests
mvn test -Dtest=ClassName            # Run specific test class
```

## Architecture Overview

This is a **Spring Framework 6.x backend** (not Spring Boot) with embedded Tomcat. The application manually configures servlet context, filters, and beans without Spring Boot auto-configuration.

### Key Entry Points
- `ResumeQuillApplication.java` - Bootstraps embedded Tomcat server
- `AppInitializer.java` - Servlet context initialization, registers DispatcherServlet
- `AppConfig.java` - Main Spring configuration, imports all other configs

### Module Structure
Each feature module in `src/main/java/com/resumequill/app/modules/` follows a layered pattern:
- `controllers/` - HTTP request handling
- `services/` - Business logic with `@Transactional` support
- `dao/` - Data access using `JdbcTemplate` (no ORM)
- `dto/` - Request/response objects with Jakarta validation
- `models/` - Domain entities

**Modules:**
- `auth/` - JWT authentication with refresh token rotation, `@AuthGuard` annotation
- `users/` - User management
- `resumes/` - Resume CRUD, stored as JSONB in PostgreSQL
- `contactUs/` - Contact form
- `metrics/` - Prometheus metrics endpoint

### Authentication
- Access tokens: JWT in `Authorization: Bearer <token>` header
- Refresh tokens: UUID stored in database, HTTP-only cookies
- Protected endpoints use `@AuthGuard` annotation
- User ID available via `@RequestAttribute("userId")` in controllers

### API Conventions
- All endpoints prefixed with `/api/` (configured in `WebConfig`)
- Use `@NoApiPrefix` annotation to exclude a controller from this prefix
- JSON content type, Jakarta Bean Validation for request bodies

## Database

- **PostgreSQL** with Flyway migrations in `src/main/resources/db/migration/`
- Migration naming: `V{number}__description.sql` (e.g., `V1__create-users_table.sql`)
- Flyway runs automatically on application startup

### Environment Variables
Required in `.env.dev` or `.env`:
```
DB_URL=jdbc:postgresql://host:5432/dbname
DB_USER=username
DB_PASSWORD=password
JWT_SECRET=256-char-hex-string
JWT_EXPIRATION=8600000
FILES_PATH=/path/to/files
NODE_URL=http://node-service:port
```

## Logging

Structured JSON logging via Logback + Logstash encoder. MDC fields include `requestId`, `userId`, `method`, `path`, `status`, `durationMs`.

Docker logs flow: Backend stdout → Promtail → Loki → Grafana

## Docker Compose Services

The stack uses split compose files (`docker-compose.base.yml` + environment overlay):
- `db` - PostgreSQL 16.4
- `backend` - Java application
- `promtail`, `loki` - Log aggregation
- `prometheus`, `grafana` - Metrics and visualization