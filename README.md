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
   │      API Gateway      │  :9095
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
| **API Gateway**   | Single entry point, routes all external requests            | 9095  |
| **Book-Service**  | Manages book metadata & inventory (MySQL)                   | 9090  |
| **User-Service**  | User registration, roles, JWT authentication (MySQL)        | 9091  |
| **Loan-Service**  | Borrow / return orchestration (MySQL)                       | 9092  |
| **Fine-Service**  | Overdue fine calculation via scheduled job (MySQL)          | 9093  |
| **Zipkin**        | End-to-end distributed tracing                              | 9411  |

---

## 🔧 Services

### 📖 1. Book-Catalog Service
> **Base URL**: `http://localhost:9090/api/books` · **Database**: MySQL

| Field             | Type     | Description                        |
|-------------------|----------|------------------------------------|
| `id`              | Long     | Primary key                        |
| `isbn`            | String   | Unique book identifier             |
| `title`           | String   | Book title                         |
| `author`          | String   | Author name                        |
| `category`        | String   | Genre / topic tag                  |
| `copiesAvailable` | Integer  | Real-time availability count       |
| `totalCopies`     | Integer  | Total copies in library            |
| `publishedYear`   | Integer  | Year of publication                |
| `bookStatus`      | Enum     | `AVAILABLE` or `UNAVAILABLE`       |

**Endpoints:**

| Method   | Path                              | Description                        |
|----------|-----------------------------------|------------------------------------|
| `POST`   | `/api/books/addbook`              | Add a new book                     |
| `PUT`    | `/api/books/updatebook/{id}`      | Update book details                |
| `GET`    | `/api/books/getbyisbn?isbn=`      | Retrieve book by ISBN              |
| `GET`    | `/api/books`                      | List all books                     |
| `GET`    | `/api/books/findbyauthor/{author}`| Find books by author               |
| `GET`    | `/api/books/totalbooks`           | Get total book count               |
| `DELETE` | `/api/books/deletebook/{isbn}`    | Delete a book by ISBN              |
| `PATCH`  | `/api/books/{title}/copies/{n}`   | Update total copies for a book     |
| `PATCH`  | `/api/books/{title}/issue`        | Issue a book (decrement available) |
| `PATCH`  | `/api/books/{title}/return`       | Return a book (increment available)|
| `GET`    | `/api/books/{title}/availability` | Check availability by title        |

---

### 👤 2. User-Service
> **Base URL**: `http://localhost:9091/api/users` · **Database**: MySQL

| Field      | Type   | Description                    |
|------------|--------|--------------------------------|
| `id`       | Long   | Auto-generated primary key     |
| `userName` | String | Unique username                |
| `email`    | String | Unique user email              |
| `password` | String | Encoded password               |
| `role`     | Enum   | `STUDENT` or `LIBRARIAN`       |

**Endpoints:**

| Method   | Path                               | Description                   |
|----------|------------------------------------|-------------------------------|
| `POST`   | `/api/users/register`              | Register a new user           |
| `PUT`    | `/api/users/updateUser/{id}`       | Update user details           |
| `GET`    | `/api/users/getbyid/{id}`          | Find user by ID               |
| `GET`    | `/api/users`                       | List all users                |
| `GET`    | `/api/users/{id}/checkstatus`      | Check user role/status        |
| `DELETE` | `/api/users/deletebyid/{id}`       | Delete user by ID             |

---

### 🔄 3. Loan-Service
> **Base URL**: `http://localhost:9092/api/loan` · **Database**: MySQL

| Field        | Type      | Description                        |
|--------------|-----------|------------------------------------|
| `id`         | Long      | Primary key                        |
| `title`      | String    | Title of book being borrowed       |
| `userId`     | Long      | Borrowing user                     |
| `borrowedAt` | LocalDate | Loan start date                    |
| `dueDate`    | LocalDate | Expected return date               |
| `returnedAt` | LocalDate | Actual return date (nullable)      |
| `status`     | Enum      | `ACTIVE`, `RETURNED`, `OVERDUE`    |

**Endpoints:**

| Method | Path                          | Description                     |
|--------|-------------------------------|---------------------------------|
| `POST` | `/api/loan/borrowbook`        | Borrow a book                   |
| `PUT`  | `/api/loan/returnbook/{loanId}` | Return a book by loan ID      |
| `GET`  | `/api/loan/{userId}/Loans`    | Get all loans for a user        |
| `GET`  | `/api/loan`                   | Get total loans count           |

**Before creating a loan, the service:**
1. ✅ Validates book availability via `book-catalog` Feign client
2. ✅ Verifies active membership via `user-service` Feign client
3. ✅ Atomically decrements `copiesAvailable` in Book-Catalog

---

### 💰 4. Fine-Service
> **Base URL**: `http://localhost:9093/api/fines` · **Database**: MySQL (Port: 9093)

| Field         | Type       | Description                     |
|---------------|------------|---------------------------------|
| `loanId`      | Long       | Reference to the overdue loan   |
| `amount`      | BigDecimal | Calculated fine amount          |
| `overdueSince`| LocalDate  | When the loan became overdue    |
| `status`      | Enum       | `PENDING` or `PAID`             |

**Endpoints:**

| Method | Path                               | Description                  |
|--------|------------------------------------|------------------------------|
| `POST` | `/api/fines/createfine?loanId=`    | Create a fine for a loan     |
| `GET`  | `/api/fines`                       | Get all fines                |
| `GET`  | `/api/fines/getallpendingfines`    | Get all pending fines        |

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
| `/books/**`   | `book-service`     |
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
| Databases         | MySQL (Book-Catalog, User, Loan, Fine)           |
| Build Tool        | Maven (multi-module project)                     |
| Tracing           | Zipkin                                           |
| Containerization  | Docker (optional)                                |

---

## 🚀 Setup & Running

### Prerequisites

- ☑️ Java 17 or later
- ☑️ Maven

---

## Step 1 — Start Zipkin (Distributed Tracing)

### Option A: Docker (Recommended)

```bash
docker run -d -p 9411:9411 openzipkin/zipkin
```

### Option B: Run from JAR

```bash
java -jar zipkin.jar
```

> Zipkin UI will be available at: `http://localhost:9411`

---

## Step 2 — Start Eureka Server

```bash
cd eureka-server
mvn spring-boot:run
```

> Eureka Dashboard will be available at: `http://localhost:8761`

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

> Gateway listens on: `http://localhost:9095`

---

### ✅ Startup Checklist

| Service         | Status Check URL                          |
|-----------------|-------------------------------------------|
| Eureka          | `http://localhost:8761`                   |
| API Gateway     | `http://localhost:9095/actuator/health`   |
| Zipkin UI       | `http://localhost:9411`                   |
| Admin - server  | `http://localhost:8900`                   |

---

## 🧪 Testing the System

### End-to-End Borrow Flow

```bash
curl -X POST http://localhost:9095/api/loan/borrowbook \
  -H "Content-Type: application/json" \
  -d '{"title": "Spring Microservices in Action", "userId": 1}'
```

**What happens behind the scenes:**

```
1. Client ──────────────────────► API Gateway (:9095)
2. Gateway ─────────────────────► Loan-Service (:9092)
3. Loan-Service ────────────────► Book-Catalog  (is book available?)
4. Loan-Service ────────────────► User-Service  (is user an active member?)
5. Loan-Service ────────────────► Book-Catalog  (issue book / decrement copiesAvailable)
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
