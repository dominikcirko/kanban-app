# 🌟 Kanban Board

> Full-stack Kanban app in **Java/Spring Boot** and **Typescript/React**. Implemented a **HATEOAS REST API** and exposed a parallel **GraphQL API**. Enabled real-time updates via **WebSocket** and **STOMP** with **SockJS fallback**. Used **Spring Data JPA/Hibernate** with **PostgreSQL** and managed schema migrations with **Liquibase**. Secured endpoints with JWT authentication using custom **Spring Security Filters**. Applied rate limiting with **Bucket4j** (100 requests/min per IP). Implemented caching on a read-heavy endpoint to improve performance. Documented the API with **OpenAPI 3** and **Swagger UI**. Achieved over 80% test coverage using both Unit testing (**JUnit, Mockito**) and Integration testing (**Testcontainers**). Enabled basic observability using **Spring Boot Actuator** and **Prometheus**. Packaged the application and database with **Docker**. Set up **Continuous Integration (CI)** workflows using **GitHub Actions** for automated builds and tests.


---

## 🚀 Setup Guide

### 🔧 IntelliJ Configuration

1. Open **Edit Configurations** in IntelliJ
2. Click **Modify options** → **Add VM options**
3. In the **VM options** field, add the following (all on one line or separated appropriately):


```text
--kanban.dbUser=yourDbUsername --kanban.dbPassword=yourDbPassword --kanban.jwtSecret=yourJwtSecretKey
```


## 🐳 Docker Support

### Windows/Mac OS
Install Docker Desktop: https://docs.docker.com/desktop/setup/install/windows-install/

### Linux
Install Docker Engine: https://docs.docker.com/engine/install/

## ⚙️ Environment Setup

1. Copy template variables from `.env.example`:

```env
KANBAN_DB_USER=USER //Place your database username here
KANBAN_DB_PASSWORD=PASSWORD //Place your database password here
KANBAN_JWT_SECRET=JWTSECRET
```

2. Create a `.env` file in the same directory and populate it with your actual data:

```env
KANBAN_DB_USER=yourDbUsername //Place your database username here
KANBAN_DB_PASSWORD=yourDbPassword //Place your database username here
KANBAN_JWT_SECRET=yourJwtSecretKey
