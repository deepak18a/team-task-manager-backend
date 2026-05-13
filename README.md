# 🚀 Astra – Team Task Manager · Backend

> A production-ready **Spring Boot REST API** for team task management with JWT authentication, OTP email verification, role-based access control, and soft-delete support.

---

## 📋 Table of Contents

- [Tech Stack](#-tech-stack)
- [System Design](#-system-design)
- [Project Structure](#-project-structure)
- [Entity Relationship Diagram](#-entity-relationship-diagram)
- [API Endpoints](#-api-endpoints)
- [Setup & Installation](#-setup--installation)
- [Environment Configuration](#-environment-configuration)
- [Running the Application](#-running-the-application)
- [Testing Each API](#-testing-each-api)
- [Security Architecture](#-security-architecture)
- [Error Handling](#-error-handling)

---

## 🛠 Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 3.5.x |
| Security | Spring Security + JWT (jjwt 0.12.6) |
| Database | MySQL 8.x |
| ORM | Spring Data JPA / Hibernate |
| Email | Spring Mail (Gmail SMTP) |
| Validation | Jakarta Bean Validation |
| Build Tool | Maven |
| Dev Tools | Lombok, Spring DevTools |

---

## 🏗 System Design

```
┌─────────────────────────────────────────────────────────────────┐
│                        CLIENT (Browser)                         │
│                    HTML + CSS + Vanilla JS                      │
└───────────────────────────┬─────────────────────────────────────┘
                            │  HTTP/REST (JSON)
                            │  CORS: localhost:5500
                            ▼
┌─────────────────────────────────────────────────────────────────---┐
│                   SPRING BOOT APPLICATION                          │
│                   localhost:8080                                   │
│                                                                    │
│  ┌─────────────┐      ┌────────────── ┐    ┌────────────────────┐  │
│  │  JWT Filter │───▶  | Controllers  │───▶│     Services       │  │
│  │  (Auth Gate)│      │  /api/auth    │    │  Business Logic    │  │
│  └─────────────┘      │  /api/users   │    └────────┬───────────┘  │
│                       │  /api/projects│             │              │
│  ┌───────────── ┐     │  /api/tasks   │    ┌────────▼───────────┐  │
│  │  CORS Config │     │  /api/members │    │    Repositories    │  │
│  │  (Security)  │     │  /api/dash    │    │   (Spring Data)    │  │
│  └───────────── ┘     └────────────── ┘    └────────┬───────────┘  │
│                                                    │               │
└────────────────────────────────────────────────────┼─────────────--┘
                                                     │
                            ┌────────────────────────▼─────────--┐
                            │           MySQL Database           │
                            │                                    │
                            │  ┌──────┐  ┌────────┐ ┌───────┐    │
                            │  │users │  │projects│ │ tasks │    │
                            │  └──────┘  └────────┘ └───────┘    │
                            │       ┌────────────────┐           │
                            │       │ project_members│           │
                            │       └────────────────┘           │
                            └────────────────────────────────────┘
                                                     │
                            ┌────────────────────────▼─────────--┐
                            │         Gmail SMTP Server          │
                            │    (OTP & Password Reset Emails)   │
                            └────────────────────────────────────┘
```

### Authentication Flow

```
  SIGNUP                    VERIFY OTP                  LOGIN
─────────                 ─────────────              ──────────
User fills form    →    Email arrives with    →    Enter email +
name/email/pass         6-digit OTP code           password
      │                        │                        │
      ▼                        ▼                        ▼
POST /auth/signup       POST /auth/verify        POST /auth/login
      │                        │                        │
      ▼                        ▼                        ▼
Save user (unverified)   Mark user verified      Validate password
Generate 6-digit OTP     Clear OTP from DB       Generate JWT token
Send OTP via Gmail              │                        │
                                ▼                        ▼
                          Redirect to login       Return JWT token
                                               (stored in localStorage)

Every subsequent request:
  Request → JWT Filter → Validate Token → Set Authentication → Controller
```

### Role-Based Access Control

```
┌─────────────────────────────────────────────────────┐
│                   ENDPOINTS                         │
├─────────────────────────┬───────────────────────────┤
│  PUBLIC (no token)      │  /api/auth/**             │
├─────────────────────────┼───────────────────────────┤
│  MEMBER + ADMIN         │  /api/projects/**         │
│                         │  /api/tasks/**            │
│                         │  /api/dashboard/**        │
├─────────────────────────┼───────────────────────────┤
│  ADMIN ONLY             │  /api/users/**            │
└─────────────────────────┴───────────────────────────┘
```

---

## 📁 Project Structure

```
team-task-manager/
│
├── src/main/java/com/taskmanager/teamtaskmanager/
│   │
│   ├── config/                          # Security & JWT Config
│   │   ├── SecurityConfig.java          # Spring Security rules + CORS
│   │   ├── JwtFilter.java               # JWT request filter (per request)
│   │   ├── JwtUtil.java                 # Token generate / validate / extract
│   │   └── CustomUserDetailsService.java# Load user by email for Spring Security
│   │
│   ├── controller/                      # REST API Controllers
│   │   ├── AuthController.java          # POST /api/auth/**
│   │   ├── UserController.java          # GET/DELETE /api/users/**
│   │   ├── ProjectController.java       # CRUD /api/projects/**
│   │   ├── TaskController.java          # CRUD /api/tasks/**
│   │   ├── ProjectMemberController.java # /api/projects/{id}/members
│   │   └── DashboardController.java     # GET /api/dashboard
│   │
│   ├── service/                         # Business Logic Interfaces
│   │   ├── AuthService.java
│   │   ├── UserService.java
│   │   ├── ProjectService.java
│   │   ├── TaskService.java
│   │   ├── ProjectMemberService.java
│   │   ├── DashboardService.java
│   │   └── EmailService.java
│   │
│   ├── service/impl/                    # Business Logic Implementations
│   │   ├── AuthServiceImpl.java
│   │   ├── UserServiceImpl.java
│   │   ├── ProjectServiceImpl.java
│   │   ├── TaskServiceImpl.java
│   │   ├── ProjectMemberServiceImpl.java
│   │   ├── DashboardServiceImpl.java
│   │   └── EmailServiceImpl.java
│   │
│   ├── entity/                          # JPA Entities (Database Tables)
│   │   ├── User.java                    # users table
│   │   ├── Project.java                 # projects table
│   │   ├── Task.java                    # tasks table
│   │   └── ProjectMember.java           # project_members table
│   │
│   ├── repository/                      # Spring Data JPA Repositories
│   │   ├── UserRepository.java
│   │   ├── ProjectRepository.java
│   │   ├── TaskRepository.java
│   │   └── ProjectMemberRepository.java
│   │
│   ├── dto/
│   │   ├── request/                     # Incoming request bodies
│   │   │   ├── SignupRequest.java
│   │   │   ├── LoginRequest.java
│   │   │   ├── VerifyOtpRequest.java
│   │   │   ├── ForgotPasswordRequest.java
│   │   │   ├── ResetPasswordRequest.java
│   │   │   ├── ProjectRequest.java
│   │   │   ├── TaskRequest.java
│   │   │   ├── TaskStatusUpdateRequest.java
│   │   │   └── ProjectMemberRequest.java
│   │   │
│   │   └── response/                    # Outgoing response bodies
│   │       ├── AuthResponse.java
│   │       ├── UserResponse.java
│   │       ├── ProjectResponse.java
│   │       ├── TaskResponse.java
│   │       ├── ProjectMemberResponse.java
│   │       └── DashboardResponse.java
│   │
│   ├── mapper/                          # Entity ↔ DTO Converters
│   │   ├── UserMapper.java
│   │   ├── ProjectMapper.java
│   │   ├── TaskMapper.java
│   │   └── ProjectMemberMapper.java
│   │
│   ├── enums/                           # Enumerations
│   │   ├── Role.java                    # ADMIN, MEMBER
│   │   ├── TaskStatus.java              # TODO, IN_PROGRESS, REVIEW, COMPLETED
│   │   ├── Priority.java                # LOW, MEDIUM, HIGH
│   │   └── ProjectRole.java             # PROJECT_ADMIN, MEMBER
│   │
│   ├── exception/                       # Custom Exceptions
│   │   ├── GlobalExceptionHandler.java  # @RestControllerAdvice
│   │   ├── ErrorResponse.java
│   │   ├── ResourceNotFoundException.java
│   │   ├── DuplicateResourceException.java
│   │   ├── BadRequestException.java
│   │   ├── AccessDeniedException.java
│   │   └── UnauthorizedException.java
│   │
│   └── TeamTaskManagerApplication.java  # Main entry point
│
├── src/main/resources/
│   └── application.properties           # DB, JWT, Mail config
│
├── src/test/java/
│   └── (test classes here)
│
└── pom.xml                              # Maven dependencies
```

---

## 🗃 Entity Relationship Diagram

```
┌──────────────────────────────┐
│            USERS             │
├──────────────────────────────┤
│ id (PK)                      │
│ name          VARCHAR        │
│ email         VARCHAR UNIQUE │
│ password      VARCHAR        │
│ role          ENUM           │◄── ADMIN | MEMBER
│ is_verified   BOOLEAN        │
│ otp           VARCHAR        │
│ otp_expiry    DATETIME       │
│ is_deleted    BOOLEAN        │
│ created_at    DATETIME       │
└──────────┬───────────────────┘
           │ 1
           │ creates
           │ N
┌──────────▼───────────────────┐         ┌─────────────────────────-┐
│          PROJECTS            │    N    │     PROJECT_MEMBERS      │
├──────────────────────────────┤◄────────├─────────────────────────-┤
│ id (PK)                      │         │ id (PK)                  │
│ name          VARCHAR        │         │ project_id  FK → projects│
│ description   TEXT           │         │ user_id     FK → users   │
│ created_by_id FK → users     │         │ role        ENUM         │◄── PROJECT_ADMIN | MEMBER
│ is_deleted    BOOLEAN        │         │ joined_at   DATETIME     │
│ created_at    DATETIME       │         │ is_deleted  BOOLEAN      │
└──────────┬───────────────────┘         └─────────────────────────-┘
           │ 1
           │ contains
           │ N
┌──────────▼───────────────────┐
│            TASKS             │
├──────────────────────────────┤
│ id (PK)                      │
│ title         VARCHAR        │
│ description   TEXT           │
│ status        ENUM           │◄── TODO | IN_PROGRESS | REVIEW | COMPLETED
│ priority      ENUM           │◄── LOW | MEDIUM | HIGH
│ due_date      DATE           │
│ project_id    FK → projects  │
│ assigned_to_id FK → users    │
│ created_by_id FK → users     │
│ is_deleted    BOOLEAN        │
│ created_at    DATETIME       │
└──────────────────────────────┘
```

---

## 🔌 API Endpoints

### Auth (`/api/auth`) — Public

| Method | Endpoint | Description | Body |
|--------|----------|-------------|------|
| POST | `/api/auth/signup` | Register new user | `{name, email, password, role}` |
| POST | `/api/auth/verify` | Verify email OTP | `{email, otp}` |
| POST | `/api/auth/login` | Login, get JWT | `{email, password}` |
| POST | `/api/auth/forgot` | Send reset OTP | `{email}` |
| POST | `/api/auth/reset` | Reset password | `{email, otp, newPassword}` |

### Users (`/api/users`) — Admin Only

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| DELETE | `/api/users/{id}` | Soft delete user |
| PUT | `/api/users/restore/{id}` | Restore deleted user |

### Projects (`/api/projects`) — Admin + Member

| Method | Endpoint | Description | Body |
|--------|----------|-------------|------|
| POST | `/api/projects/{userId}` | Create project | `{name, description}` |
| GET | `/api/projects` | Get all projects | — |
| GET | `/api/projects/{id}` | Get project by ID | — |
| PUT | `/api/projects/{id}` | Update project | `{name, description}` |
| DELETE | `/api/projects/{id}` | Soft delete project | — |
| PUT | `/api/projects/restore/{id}` | Restore project | — |

### Project Members (`/api/projects/{projectId}/members`)

| Method | Endpoint | Description | Body |
|--------|----------|-------------|------|
| POST | `/api/projects/{projectId}/members` | Add member | `{userId, role}` |
| GET | `/api/projects/{projectId}/members` | Get members | — |
| DELETE | `/api/projects/{projectId}/members/{userId}` | Remove member | — |

### Tasks (`/api/tasks`) — Admin + Member

| Method | Endpoint | Description | Body |
|--------|----------|-------------|------|
| POST | `/api/tasks/{userId}` | Create task | `{title, description, projectId, assignedToUserId, priority, dueDate}` |
| GET | `/api/tasks` | Get all tasks | — |
| GET | `/api/tasks/{id}` | Get task by ID | — |
| GET | `/api/tasks/project/{projectId}` | Get tasks by project | — |
| GET | `/api/tasks/user/{userId}` | Get tasks by user | — |
| PUT | `/api/tasks/{id}` | Update task | `{title, description, projectId, assignedToUserId, priority, status, dueDate}` |
| PATCH | `/api/tasks/{id}/status` | Update task status only | `{status}` |
| DELETE | `/api/tasks/{id}` | Soft delete task | — |
| PUT | `/api/tasks/restore/{id}` | Restore task | — |

### Dashboard (`/api/dashboard`) — Admin + Member

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/dashboard` | Overall stats |
| GET | `/api/dashboard/project/{projectId}` | Per-project stats |

---

## ⚙️ Setup & Installation

### Prerequisites

```
✅ Java 17+
✅ Maven 3.8+
✅ MySQL 8.0+
✅ Gmail account (for email OTP)
```

### Step 1: Clone the Repository

```bash
git clone https://github.com/your-username/team-task-manager-backend.git
cd team-task-manager-backend
```

### Step 2: Create MySQL Database

```sql
CREATE DATABASE team_task_manager;
```

### Step 3: Configure application.properties

```properties
# ── Database ──────────────────────────────────────────
spring.datasource.url=jdbc:mysql://localhost:3306/team_task_manager
spring.datasource.username=root
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.database-platform=org.hibernate.dialect.MySQLDialect

# ── JWT ───────────────────────────────────────────────
jwt.secret=teamtaskmanagersecretkeyteamtaskmanagersecretkey123456
jwt.expiration=86400000

# ── Gmail SMTP ────────────────────────────────────────
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-16-char-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

> **Gmail App Password**: Go to Google Account → Security → 2-Step Verification → App Passwords → Generate a 16-character password.

### Step 4: Build & Run

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

Server starts at: `http://localhost:8080`

---

## 🧪 Testing Each API

> Use **Postman**, **Thunder Client**, or **cURL** to test.
> Set `Content-Type: application/json` for all POST/PUT/PATCH requests.

---

### 1️⃣ SIGNUP

```http
POST http://localhost:8080/api/auth/signup
Content-Type: application/json

{
  "name": "Siva Kumar",
  "email": "siva@gmail.com",
  "password": "siva@123",
  "role": "MEMBER"
}
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "name": "Siva Kumar",
  "email": "siva@gmail.com",
  "role": "MEMBER",
  "createdAt": "2024-01-15T10:30:00"
}
```

**Test Cases:**

| Case | Input | Expected |
|------|-------|----------|
| ✅ Valid signup | All fields correct | 201 Created |
| ❌ Duplicate email | Same email twice | 409 Conflict |
| ❌ Short password | password: "abc" | 400 Bad Request |
| ❌ Invalid email | email: "notanemail" | 400 Bad Request |
| ❌ Missing name | name: "" | 400 Bad Request |

---

### 2️⃣ VERIFY OTP

```http
POST http://localhost:8080/api/auth/verify
Content-Type: application/json

{
  "email": "siva@gmail.com",
  "otp": "482931"
}
```

**Expected Response (200 OK):**
```
"Email verified successfully"
```

**Test Cases:**

| Case | Input | Expected |
|------|-------|----------|
| ✅ Valid OTP | Correct 6-digit OTP | 200 OK |
| ❌ Wrong OTP | Incorrect digits | 400 Bad Request |
| ❌ Expired OTP | After 5 minutes | 400 Bad Request |
| ❌ Already verified | Verify twice | 400 Bad Request |

---

### 3️⃣ LOGIN

```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "siva@gmail.com",
  "password": "John@123"
}
```

**Expected Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "userId": 1,
  "name": "Siva Kumar",
  "email": "siva@gmail.com",
  "role": "MEMBER"
}
```

> 💡 **Copy the `token` value** — use it in all subsequent requests as:
> `Authorization: Bearer <token>`

**Test Cases:**

| Case | Input | Expected |
|------|-------|----------|
| ✅ Valid credentials | Correct email + pass | 200 + JWT token |
| ❌ Wrong password | Incorrect password | 401 Unauthorized |
| ❌ Unverified user | Not OTP verified | 401 Unauthorized |
| ❌ Non-existent user | Unknown email | 401 Unauthorized |

---

### 4️⃣ FORGOT PASSWORD

```http
POST http://localhost:8080/api/auth/forgot
Content-Type: application/json

{
  "email": "madhan@gmail.com"
}
```

**Expected Response (200 OK):**
```
"Reset password OTP sent successfully"
```

---

### 5️⃣ RESET PASSWORD

```http
POST http://localhost:8080/api/auth/reset
Content-Type: application/json

{
  "email": "madhan@gmail.com",
  "otp": "123456",
  "newPassword": "NewPass@456"
}
```

**Expected Response (200 OK):**
```
"Password reset successfully"
```

---

### 6️⃣ GET ALL USERS (Admin Only)

```http
GET http://localhost:8080/api/users
Authorization: Bearer <admin_jwt_token>
```

**Expected Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Madhan",
    "email": "madhan@gmail.com",
    "role": "ADMIN",
    "createdAt": "2024-01-15T10:30:00"
  },
  {
    "id": 2,
    "name": "Siva Kumar",
    "email": "siva@gmail.com",
    "role": "MEMBER",
    "createdAt": "2024-01-15T11:00:00"
  }
]
```

**Test Cases:**

| Case | Token | Expected |
|------|-------|----------|
| ✅ Admin token | ADMIN role | 200 + user list |
| ❌ Member token | MEMBER role | 403 Forbidden |
| ❌ No token | Missing header | 403 Forbidden |

---

### 7️⃣ CREATE PROJECT

```http
POST http://localhost:8080/api/projects/1
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "name": "E-Commerce Platform",
  "description": "Build a full e-commerce website"
}
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "name": "E-Commerce Platform",
  "description": "Build a full e-commerce website",
  "createdAt": "2024-01-15T12:00:00",
  "createdById": 1,
  "createdByName": "Siva Kumar",
  "members": [],
  "tasks": []
}
```

**Test Cases:**

| Case | Input | Expected |
|------|-------|----------|
| ✅ Valid project | Unique name | 201 Created |
| ❌ Duplicate name | Same project name | 409 Conflict |
| ❌ Wrong userId | userId ≠ logged-in user | 403 Forbidden |
| ❌ Missing name | name: "" | 400 Bad Request |

---

### 8️⃣ ADD PROJECT MEMBER

```http
POST http://localhost:8080/api/projects/1/members
Authorization: Bearer <project_owner_token>
Content-Type: application/json

{
  "userId": 2,
  "role": "MEMBER"
}
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "userId": 2,
  "name": "Jane Smith",
  "email": "jane@example.com",
  "role": "MEMBER",
  "joinedAt": "2024-01-15T13:00:00"
}
```

**Test Cases:**

| Case | Input | Expected |
|------|-------|----------|
| ✅ Owner adds member | Project owner token | 201 Created |
| ❌ Non-owner adds | Other user's token | 403 Forbidden |
| ❌ Already a member | Add same user twice | 409 Conflict |
| ❌ User not found | Invalid userId | 404 Not Found |

---

### 9️⃣ CREATE TASK

```http
POST http://localhost:8080/api/tasks/1
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "title": "Design Homepage UI",
  "description": "Create Figma mockups for homepage",
  "projectId": 1,
  "assignedToUserId": 2,
  "priority": "HIGH",
  "dueDate": "2024-02-01"
}
```

**Expected Response (201 Created):**
```json
{
  "id": 1,
  "title": "Design Homepage UI",
  "description": "Create Figma mockups for homepage",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2024-02-01",
  "projectId": 1,
  "projectName": "E-Commerce Platform",
  "assignedToId": 2,
  "assignedToName": "Jane Smith",
  "createdById": 1,
  "createdByName": "John Doe"
}
```

**Test Cases:**

| Case | Input | Expected |
|------|-------|----------|
| ✅ Valid task | All fields correct | 201 Created |
| ❌ Duplicate title | Same title in project | 409 Conflict |
| ❌ Invalid project | projectId not found | 404 Not Found |
| ❌ Invalid assignee | assignedToUserId not found | 404 Not Found |
| ❌ Wrong userId | userId ≠ logged-in user | 403 Forbidden |

---

### 🔟 UPDATE TASK STATUS

```http
PATCH http://localhost:8080/api/tasks/1/status
Authorization: Bearer <jwt_token>
Content-Type: application/json

{
  "status": "IN_PROGRESS"
}
```

**Expected Response (200 OK):**
```json
{
  "id": 1,
  "title": "Design Homepage UI",
  "status": "IN_PROGRESS",
  ...
}
```

**Valid Status Values:** `TODO` → `IN_PROGRESS` → `REVIEW` → `COMPLETED`

---

### 1️⃣1️⃣ GET DASHBOARD

```http
GET http://localhost:8080/api/dashboard
Authorization: Bearer <jwt_token>
```

**Expected Response (200 OK):**
```json
{
  "totalTasks": 15,
  "todoTasks": 5,
  "inProgressTasks": 4,
  "reviewTasks": 2,
  "completedTasks": 4,
  "overdueTasks": 1
}
```

---

### 1️⃣2️⃣ DELETE & RESTORE PROJECT

```http
# Delete (soft delete — sets is_deleted = true)
DELETE http://localhost:8080/api/projects/1
Authorization: Bearer <jwt_token>

# Restore
PUT http://localhost:8080/api/projects/restore/1
Authorization: Bearer <jwt_token>
```

---

## 🔐 Security Architecture

```
Every request to protected endpoints:

1. Request arrives → JwtFilter.doFilterInternal()
2. Extract "Authorization: Bearer <token>" header
3. Parse token → extract email
4. Load user from DB by email (CustomUserDetailsService)
5. Validate token: not expired, signature valid
6. Set Authentication in SecurityContextHolder
7. Request proceeds to Controller

Authorization:
├── /api/auth/**        → No token required
├── /api/users/**       → ROLE_ADMIN only
├── /api/projects/**    → ROLE_ADMIN or ROLE_MEMBER
├── /api/tasks/**       → ROLE_ADMIN or ROLE_MEMBER
└── /api/dashboard/**   → ROLE_ADMIN or ROLE_MEMBER
```

---

## ❌ Error Handling

All errors follow this standard format:

```json
{
  "timestamp": "2024-01-15T12:00:00",
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Project not found with id: 5",
  "path": "/api/projects/5"
}
```

| Exception | HTTP Status | Error Code |
|-----------|-------------|------------|
| ResourceNotFoundException | 404 | NOT_FOUND |
| DuplicateResourceException | 409 | DUPLICATE_RESOURCE |
| BadRequestException | 400 | BAD_REQUEST |
| AccessDeniedException | 403 | FORBIDDEN |
| UnauthorizedException | 401 | UNAUTHORIZED |
| MethodArgumentNotValidException | 400 | VALIDATION_ERROR |
| Exception (generic) | 500 | INTERNAL_SERVER_ERROR |

---

## 🌐 CORS Configuration

The backend allows requests from:
- `http://127.0.0.1:5500` (VS Code Live Server)
- `http://localhost:5500`

To add more origins, update `SecurityConfig.java`:
```java
config.setAllowedOrigins(List.of(
    "http://127.0.0.1:5500",
    "http://localhost:5500"
));
```

---

## 📌 Postman Collection Setup

1. Create a new Collection: `Astra API`
2. Set Collection Variable: `baseUrl = http://localhost:8080/api`
3. After login, set Collection Variable: `token = <jwt_token_from_login>`
4. For all protected requests, add Header:
   ```
   Authorization: Bearer {{token}}
   ```

---

*Built with ❤️ using Spring Boot*
