# Flyway Database Migrations

This document provides a comprehensive guide for managing database migrations using Flyway in the `db/migration` folder for a pure Spring (non-Boot) project with PostgreSQL. It covers naming conventions, best practices, conflict resolution, and troubleshooting to ensure consistent, conflict-free, and maintainable database schema changes.

- **Location**: All migration files must reside in `src/main/resources/db/migration/`.
- **Configuration**: Flyway is configured in `FlywayConfig.java` to use `classpath:db/migration`.
- **This File**: Place `Flyway-migration.md` in `src/main/resources/db/` for easy reference.

## Flyway Setup

### application.properties

```properties
db.url=jdbc:postgresql://localhost:5432/yourdb
db.user=postgres
db.password=yourpassword
flyway.locations=classpath:db/migration
flyway.baseline-on-migrate=true
```

# Naming Conventions

**Flyway** requires migration files to follow the format:

```
V<version>__<description>.sql
```

---

## Community Standards

Use descriptive, consistent naming for clarity and maintainability.

## Rules

- **Version** — starts with `V`, followed by a sequential number (`V1`, `V2`, `V3`, ...).
- **Separator** — use double underscore `__` between version and description.
- **Description** — lowercase, underscore-separated, descriptive.
  Example: `create_user_table` instead of `users`.
- **Extension** — must be `.sql`.

### Examples
```
V1<version>__create_<table_name>_table.sql
V2<version>__create_user_table.sql
V3<version>__alter_<table_name>_<change>.sql
V4<version>__alter_session_token_text.sql
V5<version>__add_<table_name>_<column>.sql
V6<version>__add_user_role_column.sql
V7<version>__drop_<table_name>_<column>.sql
V8<version>__drop_session_ip_column.sql
V9<version>__add_index_<table_name>_<column>.sql
V10<version>__add_index_user_email.sql
V11<version>__insert_<table_name>_data.sql
V12<version>__update_<table_name>_<change>.sql
V13<version>__insert_user_test_data.sql
```

## Use Descriptive Names

Avoid vague names like V1__users.sql.
Use clear, action-based names:

```
V1__create_user_table.sql
V2__alter_session_token_text.sql
V3__add_index_session_userId.sql
```
---
