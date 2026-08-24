# 🛡️ Spring Security RBAC

Fine-grained **Role-Based Access Control** built with **Java 21**, **Spring Boot 3** and **Spring Security**.  
Goes beyond simple role checks — each role carries a specific set of **granular permissions** (e.g. `product:write`, `user:delete`) enforced via `@PreAuthorize` at the method level.

---

## 🏗️ Permission Hierarchy

```
ROLE_VIEWER
  └── user:read, product:read, order:read

ROLE_USER  (inherits VIEWER + adds)
  └── product:write, order:write

ROLE_MODERATOR  (inherits USER + adds)
  └── user:write, report:read

ROLE_ADMIN  (all permissions)
  └── user:write, user:delete
      product:write, product:delete
      order:write, order:delete
      report:read, report:export
```

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Language |
| Spring Boot 3.5 | Framework |
| Spring Security 6 | Authentication & Authorization |
| JJWT 0.12 | JWT |
| PostgreSQL 16 | Persistence |
| Docker Compose | Local database |

---

## ▶️ Running Locally

```bash
docker-compose up -d
./mvnw spring-boot:run
```

Four users are created automatically on startup (password: `password123`):

| Email | Role |
|---|---|
| admin@example.com | ROLE_ADMIN |
| moderator@example.com | ROLE_MODERATOR |
| user@example.com | ROLE_USER |
| viewer@example.com | ROLE_VIEWER |

---

## 📮 Endpoints

### Auth
```
POST /api/auth/register   → register with optional role
POST /api/auth/login      → returns token + role + permissions list
```

### Users (requires `user:read` / `user:delete`)
```
GET    /api/users         → user:read
GET    /api/users/{id}    → user:read
DELETE /api/users/{id}    → user:delete
GET    /api/users/me      → any authenticated user
```

### Products (requires `product:*`)
```
GET    /api/products      → product:read
GET    /api/products/{id} → product:read
POST   /api/products      → product:write
DELETE /api/products/{id} → product:delete
```

### Reports (requires `report:*`)
```
GET /api/reports          → report:read
GET /api/reports/export   → report:export
```

### Audit (requires `ROLE_ADMIN`)
```
GET /api/audit                  → all access logs
GET /api/audit/user/{email}     → logs by user
GET /api/audit/denied           → all 403 accesses
```

---

## 🔄 How It Works

1. User logs in → receives JWT
2. Every request goes through `JwtAuthenticationFilter`
3. Filter loads user → calls `role.getAuthorities()` → sets all permissions in `SecurityContext`
4. `@PreAuthorize("hasAuthority('product:write')")` checks the permission directly
5. `AuditFilter` logs every `/api/**` request to the database after it completes

---

## 💡 Key Concepts Demonstrated

| Concept | Where |
|---|---|
| Granular permissions | `Permission` enum with 11 specific authorities |
| Role → Permission mapping | `Role.getAuthorities()` returns role + all its permissions |
| Method-level security | `@PreAuthorize("hasAuthority('...')")` on every endpoint |
| Permission vs Role check | `hasAuthority('product:write')` vs `hasRole('ADMIN')` |
| Access audit | `AuditFilter` persists every request to `audit_logs` table |
| Error standardization | `GlobalExceptionHandler` returns consistent JSON for 401/403/400 |

---

## 🧪 Tests

| Test | What it covers |
|---|---|
| `RolePermissionTest` | Each role has correct permissions, ADMIN has all, hierarchy is respected |
| `AuthServiceTest` | Register with default/specified role, duplicate email, login returns permissions list |
