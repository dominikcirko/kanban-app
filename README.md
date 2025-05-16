

# 🌟 Kanban Board

> Kanban board application combines a **React** front-end with a **Spring Boot** back-end that provides both REST and GraphQL APIs for managing tasks. The backend handles CRUD operations with pagination, filtering, and sorting, and sends real-time updates using WebSocket/STOMP. It uses **PostgreSQL** with **Spring Data JPA** and **Liquibase** for database migrations, and secures endpoints with JWT-based authentication implemented via Spring Security filters. To improve performance, caching is used on frequent read operations. The app also includes monitoring features through **Spring Boot Actuator** and **Prometheus**, allowing you to check health status and track metrics. Everything runs inside Docker containers, and API documentation is automatically generated with Swagger/OpenAPI.


---

## 🚀 Setup Guide

### ☕ Install Java Development Kit (JDK)
- Download and install **JDK 24** from the official Oracle site:  
  👉 [Download JDK 24](https://www.oracle.com/java/technologies/downloads/#jdk24-windows)

---

## 🛠 Local Development

### 🔧 IntelliJ Configuration

1. Open **Edit Configurations** in IntelliJ.
2. Click **Modify options** → **Add VM options**.
3. In the **VM options** field, add the following (all on one line or separated appropriately):

```text
--kanban.dbUser=yourDbUsername --kanban.dbPassword=yourDbPassword --kanban.jwtSecret=yourJwtSecretKey
```

> 💡 Make sure there are no line breaks if IntelliJ requires the input to be on a single line.

### 🗄 PostgreSQL Setup
- Create a **PostgreSQL** database:
  - Name: `kanban_db`
  - Port: `5432`

---

## 🐳 Enable Docker Support

1. Copy template variables from `.env.example`:

```env
KANBAN_DB_USER=USER
KANBAN_DB_PASSWORD=PASSWORD
KANBAN_JWT_SECRET=JWTSECRET
```

2. Create a `.env` file in the same directory and populate it with your actual data:

```env
KANBAN_DB_USER=yourDbUsername
KANBAN_DB_PASSWORD=yourDbPassword
KANBAN_JWT_SECRET=yourJwtSecretKey
```

---
