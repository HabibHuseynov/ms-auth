# 🔐 Auth Service with Spring Boot & Keycloak

> A lightweight, production-ready authentication and authorization service built with Spring Boot and Keycloak.

---

## ✨ What This Service Does

Your one-stop authentication solution with:

- 👤 **User Management** — Register, login, refresh tokens, user info
- 🔑 **Role & Permission Management** — Keycloak Admin APIs at your fingertips
- 🛡️ **Authorization Services** — Fine-grained resource, scope, and policy control
- ✅ **Access Evaluation** — Check if users have access to specific resources
- 🔄 **Token Management** — Secure JWT tokens with automatic refresh

Perfect for microservices that need a centralized auth backbone!

---

## 🛠️ Tech Stack

| Component | Purpose |
|-----------|---------|
| **Java** | 21+ |
| **Spring Boot** | 4.x Web Framework |
| **Spring Security** | Authentication & Authorization |
| **Keycloak** | Identity Provider & Authorization Server |
| **OpenFeign** | Service-to-service API calls |
| **Gradle** | Build automation |
| **Docker Compose** | Local Keycloak + Postgres setup |

---

## 🚀 Quick Start (5 minutes)

### 1️⃣ Prerequisites

✅ Check you have:
- **Java 21+** installed
- **Docker & Docker Compose** ready
- **Gradle Wrapper** (included in repo)

### 2️⃣ Start Keycloak & Postgres

From your project root:

```bash
docker compose up -d
```

⏳ **Wait for health check** (takes ~30 seconds)

Once ready:
- 🌐 Admin Console: http://localhost:8080
- 📧 Default credentials: `admin` / `SuperSecret2025!`
- 💾 Database: Postgres (auto-provisioned)

### 3️⃣ Run the Auth Service

**Option A: Direct with Gradle**
```bash
./gradlew bootRun
```

**Option B: Build & run as JAR**
```bash
./gradlew clean build
java -jar build/libs/ms-auth-*.jar
```

🎉 **Service is ready at:** http://localhost:8082/api

---

## ⚙️ Configuration

Default values in `application.yaml` (perfect for local dev):

```yaml
server:
  port: 8082
  servlet.context-path: /api

keycloak:
  auth-server-url: http://localhost:8080
  realm: myrealm
  resource: auth_resource
  credentials.secret: my_secret
  admin:
    realm: master
    client-id: admin-service-account
    client-secret: 8f9d2a1c-4b5e-4d3a-9f1e-7d8c6b5a4e3f
```

### 📌 Override with Environment Variables

Perfect for CI/CD and different environments:

| Variable | Purpose |
|----------|---------|
| `KEYCLOACK_SERVER_URL` | Keycloak server address |
| `KEYCLOACK_REALM` | Your realm name |
| `KEYCLOACK_RESOURCE` | Client ID |
| `KEYCLOAK_CLIENT_SECRET` | Client secret |
| `KEYCLOACK_REALM_ADMIN` | Admin realm |
| `KEYCLOACK_ADMIN_CLI` | Admin client ID |
| `KEYCLOAK_ADMIN_SECRET` | Admin client secret |

**Example:**
```bash
export KEYCLOACK_SERVER_URL=https://auth.example.com
export KEYCLOACK_REALM=production
./gradlew bootRun
```

---

## 📡 API Endpoints Overview

### 🔓 Public Endpoints (No Auth Required)

| Method | Endpoint | What It Does |
|--------|----------|--------------|
| `POST` | `/auth/register` | 📝 Create a new user account |
| `POST` | `/auth/login` | 🔓 Get access & refresh tokens |
| `POST` | `/auth/refresh` | 🔄 Get a new access token |

### 🔒 Protected Endpoints (Bearer Token Required)

| Method | Endpoint | What It Does |
|--------|----------|--------------|
| `GET` | `/auth/me` | 👤 Get current user info |
| `POST` | `/check-access` | ✅ Verify access to resource/scope |

### 🛠️ Admin Endpoints (Service Account Required)

| Method | Endpoint | Purpose |
|--------|----------|---------|
| `POST` | `/admin/keycloak/roles` | ➕ Create role |
| `GET` | `/admin/keycloak/roles` | 📋 List all roles |
| `GET` | `/admin/keycloak/roles/{roleName}` | 🔍 Get role details |
| `POST` | `/admin/keycloak/users/{userId}/roles` | 👤 Assign role to user |
| `DELETE` | `/admin/keycloak/users/{userId}/roles/{roleName}` | ❌ Remove role from user |
| `GET` | `/admin/keycloak/users/{userId}/roles` | 📋 List user's roles |
| `GET` | `/admin/keycloak/users?q=<query>` | 🔎 Search users |
| `POST` | `/admin/keycloak/authorization/resources` | 📦 Create resource |
| `POST` | `/admin/keycloak/authorization/scopes` | 🏷️ Create scope |
| `POST` | `/admin/keycloak/authorization/policies/role` | 📋 Create role policy |
| `POST` | `/admin/keycloak/authorization/permissions` | 🔐 Create permission |
| `GET` | `/admin/keycloak/authorization/resource/{name}` | 📖 Get resource |
| `POST` | `/admin/keycloak/authorization/resource/assign/role` | 🔗 Link resource to role |

---

## 🧪 Testing the Service

### Option 1: Use cURL (Quick & Easy) 🚀

**Register a new user:**
```bash
curl -X POST http://localhost:8082/api/auth/register \
  -H 'Content-Type: application/json' \
  -d '{
    "username": "alice",
    "password": "SecurePass123!",
    "email": "alice@example.com",
    "firstName": "Alice",
    "lastName": "Smith"
  }'
```

**Login:**
```bash
curl -X POST http://localhost:8082/api/auth/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"alice","password":"SecurePass123!"}'
```

💾 **Save the `accessToken` from the response!**

**Get user info:**
```bash
curl -X GET http://localhost:8082/api/auth/me \
  -H 'Authorization: Bearer <YOUR_ACCESS_TOKEN>'
```

**Check access to a resource:**
```bash
curl -X POST http://localhost:8082/api/check-access \
  -H 'Authorization: Bearer <YOUR_ACCESS_TOKEN>' \
  -H 'Content-Type: application/json' \
  -d '{"resource":"orders","scope":"read"}'
```

**Refresh token:**
```bash
curl -X POST http://localhost:8082/api/auth/refresh \
  -H 'Content-Type: application/json' \
  -d '{"refreshToken":"<YOUR_REFRESH_TOKEN>"}'
```

### Option 2: Import Postman Collections 📮

Two ready-to-use collections included:
- `postman/ms-auth.postman_collection.json`
- `postman/ms-auth.postman_collection_improved.json`

**Steps:**
1. Open Postman
2. Click "Import"
3. Select one of the collections above
4. Start testing! 🎉

### Option 3: Use VS Code REST Client 🔧

Create a `test.http` file:

```http
### Register
POST http://localhost:8082/api/auth/register
Content-Type: application/json

{
  "username": "bob",
  "password": "Password123!",
  "email": "bob@example.com"
}

### Login
POST http://localhost:8082/api/auth/login
Content-Type: application/json

{
  "username": "bob",
  "password": "Password123!"
}

### Get Me
GET http://localhost:8082/api/auth/me
Authorization: Bearer <ACCESS_TOKEN_HERE>
```

Install the [REST Client](https://marketplace.visualstudio.com/items?itemName=humao.rest-client) extension and click "Send Request" above each endpoint!

---

## 📊 Response Examples

### Login Response ✅

```json
{
  "accessToken": "eyJhbGciOiJSUzI1NiIsInR5cC...",
  "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cC...",
  "expiresIn": 300,
  "tokenType": "Bearer",
  "scope": "openid profile email"
}
```

### User Info Response 👤

```json
{
  "subject": "8f9d2a1c-4b5e-4d3a-9f1e-7d8c6b5a4e3f",
  "username": "alice",
  "email": "alice@example.com",
  "firstName": "Alice",
  "lastName": "Smith",
  "roles": ["user", "admin"],
  "realmAccess": {
    "roles": ["user", "admin"]
  }
}
```

### Access Check Response ✅

```json
{
  "granted": true,
  "resource": "orders",
  "scope": "read"
}
```

---

## 🏗️ Architecture at a Glance

```
┌─────────────────────────────────────────────────┐
│          Your Microservice                      │
│                                                 │
│  AuthController → AccessController             │
│  (Login, Register)   (Permission checks)        │
└────────────────┬────────────────────────────────┘
                 │
                 │ REST API Calls
                 ▼
┌─────────────────────────────────────────────────┐
│       Keycloak (Identity Provider)              │
│                                                 │
│  ├─ User Management (Register, Login)           │
│  ├─ Token Issuance (JWT, Refresh)               │
│  ├─ Authorization Services (Resources/Scopes)   │
│  └─ Role Management (via Admin APIs)            │
└────────────────┬────────────────────────────────┘
                 │
                 ▼
        ┌────────────────────┐
        │   Postgres DB      │
        │  (User, Roles, etc)│
        └────────────────────┘
```

---

## 🔐 Key Request/Response Models

### Register Request
```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "firstName": "string",
  "lastName": "string"
}
```

### Login Request
```json
{
  "username": "string",
  "password": "string"
}
```

### Permission Check Request
```json
{
  "resource": "string",    // e.g., "orders"
  "scope": "string"        // e.g., "read"
}
```

### Refresh Token Request
```json
{
  "refreshToken": "string"
}
```

---

## 🎯 Common Workflows

### Workflow 1️⃣: User Registration & Login

1. User submits registration form
   ```bash
   POST /auth/register
   ```
2. Keycloak creates user account ✅
3. User logs in
   ```bash
   POST /auth/login
   ```
4. Receive `accessToken` and `refreshToken`
5. Store tokens securely (httpOnly cookie / secure storage)

### Workflow 2️⃣: Token Refresh

1. Frontend detects token expiration (check `expiresIn`)
2. Call refresh endpoint
   ```bash
   POST /auth/refresh
   ```
3. Get new `accessToken` (and optionally new `refreshToken`)
4. Resume normal operations 🔄

### Workflow 3️⃣: Permission Check

1. User attempts to access resource (e.g., "View Orders")
2. Frontend calls permission check
   ```bash
   POST /check-access
   {
     "resource": "orders",
     "scope": "read"
   }
   ```
3. Keycloak evaluates policies/roles
4. Returns `granted: true/false` ✅
5. Frontend shows/hides UI accordingly

---

## 🛠️ Build & Development

```bash
# Build the project
./gradlew clean build

# Run tests
./gradlew test

# Run with verbose logging
./gradlew bootRun --info

# Check dependencies
./gradlew dependencies
```

---

## 🐛 Troubleshooting

### ❌ "Connection Refused" on Keycloak

**Problem:** `java.net.ConnectException: Connection refused`

**Solution:**
```bash
# Verify Keycloak is running
docker ps | grep keycloak

# Check logs
docker compose logs -f keycloak

# Wait for health check to pass (green ✓)
```

### ❌ "401 Unauthorized"

**Problem:** API returns 401 despite providing a token

**Possible causes:**
- ❌ Token is expired (check `expiresIn`)
- ❌ Token is from wrong realm/client
- ❌ Token syntax is wrong (should be `Bearer <token>`)

**Solution:**
```bash
# Get a fresh token
POST /auth/login

# Verify token format in header
Authorization: Bearer eyJhbGc...  # ✅ Correct
Authorization: eyJhbGc...        # ❌ Missing "Bearer"
```

### ❌ "403 Forbidden" on Access Check

**Problem:** `check-access` returns `granted: false`

**Likely causes:**
- User doesn't have the required role
- Resource/scope not configured in Keycloak
- Policy not assigned to the role

**Solution:**
1. Visit Keycloak console: http://localhost:8080
2. Go to **Clients** → **auth_resource** → **Authorization** → **Resources**
3. Create the resource (e.g., "orders")
4. Create scope (e.g., "read")
5. Create role policy linking role → resource/scope
6. Try again ✅

### ❌ "Port already in use"

**Problem:** Port 8082 or 8080 is already in use

**Solution:**
```bash
# Change app port
export SERVER_PORT=8083
./gradlew bootRun --server.port=8083

# Or change Keycloak port in docker-compose.yaml
# Edit ports: "8081:8080"  (external:internal)
```

### ❌ Keycloak realm not imported

**Problem:** Keycloak starts but no `myrealm` is available

**Solution:**
```bash
# docker-compose.yaml should have:
# -v ./keycloak-realm.json:/opt/keycloak/data/import/keycloak-realm.json
# -e KEYCLOAK_IMPORT=/opt/keycloak/data/import/keycloak-realm.json

# Restart with fresh volume
docker compose down -v
docker compose up -d
```

---

## 📁 Project Layout

```
auth-service/
├── src/main/java/com/example/auth/
│   ├── controller/
│   │   ├── AuthController.java          ← Login, Register, Refresh
│   │   ├── AccessController.java        ← Permission checks
│   │   └── KeycloakAdminController.java ← Role/User management
│   ├── service/
│   │   ├── AuthService.java
│   │   ├── KeycloakAdminService.java
│   │   └── KeycloakAuthService.java
│   ├── model/
│   │   ├── LoginRequest.java
│   │   ├── LoginResponse.java
│   │   └── PermissionCheckRequest.java
│   ├── config/
│   │   └── SecurityConfig.java
│   └── AuthServiceApplication.java      ← Spring Boot entry point
│
├── src/main/resources/
│   ├── application.yaml                 ← Configuration
│   └── ...
│
├── docker-compose.yaml                  ← Keycloak + Postgres
├── keycloak-realm.json                  ← Sample realm setup
├── postman/
│   ├── ms-auth.postman_collection.json
│   └── ms-auth.postman_collection_improved.json
├── build.gradle                         ← Dependencies
└── README.md                            ← This file!
```

---

## 🚀 Running in Production

### Environment Variables

Before deploying, set these in your environment:

```bash
export KEYCLOACK_SERVER_URL=https://your-keycloak.com
export KEYCLOACK_REALM=production
export KEYCLOACK_RESOURCE=auth-service
export KEYCLOAK_CLIENT_SECRET=<production-secret>
export KEYCLOACK_REALM_ADMIN=master
export KEYCLOACK_ADMIN_CLI=admin-cli
export KEYCLOAK_ADMIN_SECRET=<admin-secret>
export SERVER_PORT=8082
```

### Docker Build

```bash
# Build image
./gradlew bootBuildImage

# Or traditional Docker build
./gradlew build
docker build -t auth-service:latest .
```

### Deployment Checklist

- ✅ Keycloak deployed and healthy
- ✅ Postgres database ready
- ✅ Environment variables set
- ✅ SSL/TLS certificates configured
- ✅ Secrets manager (Vault) integration verified
- ✅ Logs aggregation set up
- ✅ Health checks configured (`/actuator/health`)

---

## 🎓 Learning Path

**Beginner:**
- Start with `/auth/register` and `/auth/login`
- Get an access token and call `/auth/me`
- Understand JWT structure (jwt.io)

**Intermediate:**
- Implement token refresh (`/auth/refresh`)
- Set up roles in Keycloak Admin Console
- Try `/check-access` with different resources

**Advanced:**
- Create custom authorization policies
- Implement scope-based access control
- Integrate with other microservices using tokens
- Build a complete auth flow (register → login → refresh → logout)

---

## 📚 Useful Resources

| Resource | Link |
|----------|------|
| **Keycloak Docs** | https://www.keycloak.org/documentation |
| **Spring Security** | https://spring.io/projects/spring-security |
| **JWT Debugger** | https://jwt.io |
| **Spring Boot Guide** | https://spring.io/guides/gs/spring-boot/ |
| **OpenFeign Docs** | https://spring.io/projects/spring-cloud-openfeign |

---

## 📝 License

This project is provided as-is. Add your license here (MIT, Apache 2.0, etc.).

---

## ❓ Need Help?

- 🔧 **Check the troubleshooting section above**
- 📖 **Read the Keycloak documentation**
- 💬 **Review the controller source code** — it's well-commented!
- 🐛 **Check Docker logs:** `docker compose logs -f`

---

**Happy authenticating! 🔐🚀**
