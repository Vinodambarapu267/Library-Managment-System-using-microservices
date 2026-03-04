# 📚 Library Management System — Microservices Architecture

> A scalable, cloud-native Library Management System built with **Spring Boot 3+** and **Spring Cloud**.  
> Manages books, users, loans, and fines across independent services with full observability.

---

## 🗂️ Table of Contents

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Services](#-services)
- [Communication Patterns](#-communication-patterns)
- [Technology Stack](#-technology-stack)
- [Setup & Running](#-setup--running)
- [Testing the System](#-testing-the-system)
- [References](#-references)

---

## 🌐 Overview

This system is composed of **four independent microservices**, each with its own database, business logic, and API surface. They are wired together via **Eureka service discovery**, **Feign HTTP clients**, and exposed to the outside world through an **API Gateway**.

```
             Client
               │
               ▼
   ┌───────────────────────┐
   │     API Gateway       │  :8080
   └─────────────┬─────────┘
                 │
  ┌──────────────┼──────────────────┐
  ▼              ▼                  ▼
Book-Catalog  Loan-Service     User-Service
(MySQL)       (MySQL)          (MySQL)
                │
                ▼
             Fine-Service
              (MySQL)
```

All services register with **Eureka** at `localhost:8761`.  
Distributed traces are captured by **Zipkin** at `localhost:9411`.

---

## 🏛️ Architecture

| Component         | Role                                                        | Port  |
|-------------------|-------------------------------------------------------------|-------|
| **Eureka Server** | Service registry & discovery                                | 8761  |
| **API Gateway**   | Single entry point, routes all external requests            | 8090  |
| **Book-Catalog**  | Manages book metadata & inventory (MongoDB)                 | 9090  |
| **User-Service**  | User registration, roles, JWT authentication (MySQL)        | 9091  |
| **Loan-Service**  | Borrow / return orchestration (MySQL)                       | 9092  |
| **Fine-Service**  | Overdue fine calculation via scheduled job (MySQL)          | 9093  |
| **Zipkin**        | End-to-end distributed tracing                              | 9411  |

---

## 🔧 Services

### 📖 1. Book-Catalog Service
> **Database**: MySQL — flexible schema for book metadata

| Field             | Type     | Description                        |
|-------------------|----------|------------------------------------|
| `isbn`            | String   | Primary key                        |
| `title`           | String   | Book title                         |
| `author`          | String   | Author name                        |
| `categories`      | List     | Genre / topic tags                 |
| `copiesAvailable` | Integer  | Real-time availability count       |

**Endpoints:**

| Method | Path                        | Description                      |
|--------|-----------------------------|----------------------------------|
| `POST` | `/books`                    | Add a new book                   |
| `GET`  | `/books/{isbn}`             | Retrieve book details            |
| `GET`  | `/books/search?title=xyz`   | Search books by title            |
| `GET`  | `/books/{isbn}/available`   | *(Internal)* Check availability  |

---

### 👤 2. User-Service
> **Database**: MySQL — ACID-compliant user accounts

| Field   | Type   | Description                    |
|---------|--------|--------------------------------|
| `id`    | Long   | Auto-generated primary key     |
| `email` | String | Unique user email              |
| `role`  | Enum   | `MEMBER` or `LIBRARIAN`        |

**Endpoints:**

| Method | Path                  | Description                          |
|--------|-----------------------|--------------------------------------|
| `POST` | `/users/register`     | Register a new user                  |
| `GET`  | `/users/{id}/isMember`| *(Internal)* Check active membership |

> 🔐 JWT authentication is integrated — tokens are issued and validated per request.

---

### 🔄 3. Loan-Service
> **Database**: MySQL — transactional integrity for borrow/return operations

| Field        | Type      | Description                        |
|--------------|-----------|------------------------------------|
| `id`         | Long      | Primary key                        |
| `isbn`       | String    | Book being borrowed                |
| `userId`     | Long      | Borrowing user                     |
| `borrowedAt` | LocalDate | Loan start date                    |
| `dueDate`    | LocalDate | Expected return date               |
| `returnedAt` | LocalDate | Actual return date (nullable)      |
| `status`     | Enum      | `ACTIVE`, `RETURNED`, `OVERDUE`    |

**Endpoints:**

| Method  | Path               | Description                         |
|---------|--------------------|-------------------------------------|
| `POST`  | `/loans/borrow`    | Borrow a book                       |
| `PUT`   | `/loans/{id}/return` | Return a book                     |

**Before creating a loan, the service:**
1. ✅ Validates book availability via `book-catalog` Feign client
2. ✅ Verifies active membership via `user-service` Feign client
3. ✅ Atomically decrements `copiesAvailable` in Book-Catalog

---

### 💰 4. Fine-Service
> **Database**: MySQL — tracks overdue fines

| Field         | Type      | Description                     |
|---------------|-----------|---------------------------------|
| `loanId`      | Long      | Reference to the overdue loan   |
| `amount`      | BigDecimal| Calculated fine amount          |
| `overdueSince`| LocalDate | When the loan became overdue    |
| `status`      | Enum      | `PENDING` or `PAID`             |

**Background Processing:**
- A `@Scheduled` cron job runs **daily**, scanning all active overdue loans
- Fine formula: `daysOverdue × dailyFineRate`
- Creates or updates `Fine` records automatically

---

## 🔗 Communication Patterns

### Service Discovery
All services (including the gateway) self-register with **Eureka Server** on startup.

### API Gateway Routing

| Incoming Path | Routed To          |
|---------------|--------------------|
| `/books/**`   | `book-catalog`     |
| `/loans/**`   | `loan-service`     |
| `/users/**`   | `user-service`     |
| `/fines/**`   | `fine-service`     |

### Distributed Tracing
**Zipkin** + **Spring Cloud Sleuth** automatically propagate trace/span IDs across all service boundaries.  
View traces at: `http://localhost:9411`

---

## 🛠️ Technology Stack

| Category          | Technology                                       |
|-------------------|--------------------------------------------------|
| Language          | Java 17+                                         |
| Framework         | Spring Boot 3+                                   |
| Cloud             | Spring Cloud (Eureka, Gateway, OpenFeign, Sleuth)|
| Databases         | MySQL (Book-Catalog), MySQL (User, Loan, Fine)   |
| Build Tool        | Maven (multi-module project)                     |
| Security          | JWT Authentication                               |
| Testing           | JUnit 5, Mockito                                 |
| Tracing           | Zipkin                                           |
| Containerization  | Docker (optional)                                |

---

## 🚀 Setup & Running

### Prerequisites

- ☑️ Java 17 or later
- ☑️ Maven
- ☑️ Docker *(optional, recommended for infrastructure)*

---

### Step 1 — Start Infrastructure Services

**Recommended: use Docker**

```bash
# Zipkin — distributed tracing UI
docker run -d -p 9411:9411 openzipkin/zipkin

# MySQL — for User, Loan, Fine services
docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root mysql:8


> **Alternative:** Run Zipkin directly from its JAR:
> ```bash
> java -jar zipkin.jar
> ```

---

### Step 2 — Start Eureka Server

```bash
cd eureka-server
mvn spring-boot:run
```

> Eureka dashboard: `http://localhost:8761`

---

### Step 3 — Start Microservices

Open **separate terminals** for each service:

```bash
# Terminal 1
cd book-service  && mvn spring-boot:run

# Terminal 2
cd user-service && mvn spring-boot:run

# Terminal 3
cd loan-service && mvn spring-boot:run

# Terminal 4
cd fine-service && mvn spring-boot:run
```

---

### Step 4 — Start API Gateway

```bash
cd api-gateway
mvn spring-boot:run
```

> Gateway listens on: `http://localhost:8080`

---

### ✅ Startup Checklist

| Service         | Status Check URL                          |
|-----------------|-------------------------------------------|
| Eureka          | `http://localhost:8761`                   |
| API Gateway     | `http://localhost:8090/actuator/health`   |
| Zipkin UI       | `http://localhost:9411`                   |

---

## 🧪 Testing the System

### End-to-End Borrow Flow

```bash
curl -X POST http://localhost:8090/loans/borrow \
  -H "Content-Type: application/json" \
  -d '{"isbn": "1234567890", "userId": "1"}'
```

**What happens behind the scenes:**

```
1. Client ──────────────────────► API Gateway (:8080)
2. Gateway ─────────────────────► Loan-Service
3. Loan-Service ────────────────► Book-service  (is book available?)
4. Loan-Service ────────────────► User-Service  (is user an active member?)
5. Loan-Service ────────────────► Book-service  (decrement copiesAvailable)
6. Loan-Service ─── creates Loan record in MySQL
7. Zipkin ─────── captures full trace across all hops
```

> 📊 **View the full trace at:** `http://localhost:9411`

---

## 📚 References

- [Spring Boot Microservices — GeeksforGeeks](https://www.geeksforgeeks.org/springboot/java-spring-boot-microservices-example-step-by-step-guide/)
- [Spring.io Microservices Overview](https://spring.io/microservices)
- [Auth0 Blog — Java Spring Boot Microservices](https://auth0.com/blog/java-spring-boot-microservices/)
- [Building Microservices with Spring Boot — YouTube](https://www.youtube.com/watch?v=Us5acS_3pik)
- [Inspiration Repository](https://github.com/ioeltadeu/library-management)

---

<div align="center">

Built with ☕ Java · 🍃 Spring Boot · ☁️ Spring Cloud

</div>