# 📚 LibraMS — Library Management System

> A scalable, cloud-native **Full-Stack Library Management System** built with **Spring Boot 3+** microservices and a **React 18** frontend.  
> Manages books, users, loans, and fines across independent services with full observability.

**Stack:** Java 21 · Spring Boot 3.5 · Spring Cloud · React 18 · MySQL 8 · Eureka · API Gateway · Zipkin

## 📋 Table of Contents

- [Overview](#-overview)
- [Architecture](#-architecture)
- [Services](#-services)
- [React Frontend](#%EF%B8%8F-react-frontend)
- [Communication Patterns](#-communication-patterns)
- [Technology Stack](#-technology-stack)
- [Setup & Running](#-setup--running)
- [Testing the System](#-testing-the-system)
- [References](#-references)

---

## 🌐 Overview

LibraMS is composed of **four independent microservices**, each with its own database, business logic, and API surface. They are wired together via **Eureka service discovery**, **Feign HTTP clients**, and exposed to the outside world through an **API Gateway**. A **React 18** frontend connects to all services through the gateway.

```
                    ┌─────────────────────────┐
                    │    React Frontend :3000  │
                    │  (Vite/CRA + Fetch API)  │
                    └────────────┬────────────┘
                                 │ /api/*
                                 ▼
                    ┌─────────────────────────┐
                    │    API Gateway   :9095   │
                    │  (Spring Cloud MVC GW)   │
                    └──────┬──────┬─────┬─────┘
                           │      │     │
           ┌───────────────┘      │     └──────────────────┐
           ▼                      ▼                        ▼
  ┌────────────────┐   ┌─────────────────┐      ┌──────────────────┐
  │  Book-Service  │   │  User-Service   │      │  Loan-Service    │
  │   :9090 MySQL  │   │  :9091 MySQL    │      │  :9092 MySQL     │
  └────────────────┘   └─────────────────┘      └────────┬─────────┘
                                                          │ (Feign)
                                                          ▼
                                               ┌──────────────────┐
                                               │  Fine-Service    │
                                               │  :9093 MySQL     │
                                               └──────────────────┘
```

All services register with **Eureka** at `localhost:8761`.  
Distributed traces are captured by **Zipkin** at `localhost:9411`.

---

## 🏛️ Architecture

| Component | Role | Port |
|---|---|---|
| **Eureka Server** | Service registry & discovery | `8761` |
| **API Gateway** | Single entry point — routes all external requests | `9095` |
| **Admin Server** | Spring Boot Admin — service health dashboard | `8900` |
| **Book-Service** | Manages book metadata & inventory (MySQL) | `9090` |
| **User-Service** | User registration, roles, membership validation (MySQL) | `9091` |
| **Loan-Service** | Borrow / return orchestration (MySQL) | `9092` |
| **Fine-Service** | Overdue fine calculation via scheduled job (MySQL) | `9093` |
| **Zipkin** | End-to-end distributed tracing | `9411` |
| **React Frontend** | Full-stack UI — communicates via API Gateway | `3000` |

---

## 🔧 Services

### 📖 1. Book-Catalog Service
> **Base URL**: `http://localhost:9095/api/books` · **Database**: `book_db`

<details>
<summary>Entity Fields</summary>

| Field | Type | Description |
|---|---|---|
| `id` | Long | Auto-generated primary key |
| `isbn` | String | Unique identifier (UUID-based, auto-generated) |
| `title` | String | Book title |
| `author` | String | Author name |
| `category` | String | Genre / topic tag |
| `copiesAvailable` | Integer | Real-time availability count |
| `totalCopies` | Integer | Total copies in library |
| `publishedYear` | Integer | Year of publication |
| `bookStatus` | Enum | `AVAILABLE` or `UNAVAILABLE` |

</details>

**Endpoints:**

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/books/addbook` | Add a new book |
| `PUT` | `/api/books/updatebook/{id}` | Update book details |
| `GET` | `/api/books/getbyisbn?isbn=` | Retrieve book by ISBN |
| `GET` | `/api/books` | List all books |
| `GET` | `/api/books/findbyauthor/{author}` | Find books by author |
| `GET` | `/api/books/totalbooks` | Get total book count |
| `DELETE` | `/api/books/deletebook/{isbn}` | Delete a book by ISBN |
| `PATCH` | `/api/books/{title}/copies/{n}` | Update total copies |
| `PATCH` | `/api/books/{title}/issue` | Issue book — decrement available |
| `PATCH` | `/api/books/{title}/return` | Return book — increment available |
| `GET` | `/api/books/{title}/availability` | Check availability count |

---

### 👤 2. User-Service
> **Base URL**: `http://localhost:9095/api/users` · **Database**: `user_db`

<details>
<summary>Entity Fields</summary>

| Field | Type | Description |
|---|---|---|
| `id` | Long | Auto-generated primary key |
| `userName` | String | Unique username |
| `email` | String | Unique user email |
| `password` | String | Base64-encoded password |
| `role` | Enum | `STUDENT`, `FACULTY`, `STAFF`, `LIBRARIAN`, `ADMIN`, `GUEST` |
| `isActive` | boolean | Whether user account is active |

</details>

**Endpoints:**

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/users/register` | Register a new user |
| `PUT` | `/api/users/updateUser/{id}` | Update user details |
| `GET` | `/api/users/getbyid/{id}` | Find user by ID |
| `GET` | `/api/users` | List all users |
| `GET` | `/api/users/{id}/checkstatus` | Check user role and borrow eligibility |
| `DELETE` | `/api/users/deletebyid/{id}` | Delete user by ID |

---

### 🔄 3. Loan-Service
> **Base URL**: `http://localhost:9095/api/loan` · **Database**: `loan_db`

<details>
<summary>Entity Fields</summary>

| Field | Type | Description |
|---|---|---|
| `loanId` | Long | Primary key |
| `title` | String | Title of book being borrowed |
| `userId` | Long | Borrowing user ID |
| `borrowAt` | LocalDate | Loan start date |
| `dueDate` | LocalDate | Expected return date |
| `returnedAt` | LocalDate | Actual return date (nullable) |
| `totalAmount` | Double | Fine amount if applicable |
| `status` | Enum | `BORROWED`, `RETURNED`, `OVERDUE` |

</details>

**Endpoints:**

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/loan/borrowbook` | Borrow a book |
| `PUT` | `/api/loan/returnbook/{loanId}` | Return a book by loan ID |
| `GET` | `/api/loan/{userId}/Loans` | Get all loans for a user |
| `GET` | `/api/loan` | Get all loans list |

**Before creating a loan, the service:**
1. ✅ Validates book availability via `book-catalog` Feign client
2. ✅ Verifies active membership via `user-service` Feign client
3. ✅ Atomically decrements `copiesAvailable` in Book-Catalog

---

### 💰 4. Fine-Service
> **Base URL**: `http://localhost:9095/api/fines` · **Database**: `fine_db`

<details>
<summary>Entity Fields</summary>

| Field | Type | Description |
|---|---|---|
| `loanId` | Long | Reference to the overdue loan |
| `amount` | BigDecimal | Calculated fine amount |
| `overdueSince` | LocalDate | When the loan became overdue |
| `status` | Enum | `PENDING` or `PAID` |

</details>

**Endpoints:**

| Method | Path | Description |
|---|---|---|
| `POST` | `/api/fines/createfine?loanId=` | Create a fine for a loan |
| `GET` | `/api/fines` | Get all fines |
| `GET` | `/api/fines/getallpendingfines` | Get all pending fines |

**Background Processing:**
- A `@Scheduled` cron job runs **daily**, scanning all active overdue loans
- Fine formula: `daysOverdue × dailyFineRate`
- Creates or updates `Fine` records automatically

---

## 🖥️ React Frontend

> **Location**: `librams-final/` · **Port**: `3000` · **Proxy**: `→ localhost:9095`

A React 18 single-page application that communicates with all four microservices through the API Gateway.

### Project Structure

```
librams-final/
├── public/
│   └── index.html
├── src/
│   ├── index.js               # App entry point
│   ├── index.css              # Global CSS variables & design tokens
│   ├── App.jsx                # Root — sidebar nav + toast system
│   ├── api.js                 # All API calls, unwrap(), toArray(), normalizeLoan()
│   ├── hooks/
│   │   └── useToast.js        # Toast notification hook
│   ├── components/
│   │   └── UI.jsx             # Full design system (Btn, Table, Modal, Badge, Form)
│   └── pages/
│       ├── Dashboard.jsx      # Stats overview + recent books + fines
│       ├── Books.jsx          # Book catalog — CRUD + author search
│       ├── Users.jsx          # User registry — register, edit, delete
│       ├── Loans.jsx          # Borrow / return + filter by user
│       └── Fines.jsx          # Fine tracker — all/pending + create
└── package.json               # proxy: "http://localhost:9095"
```

### Key Frontend Features

- 🎨 **Custom dark design system** — CSS variables, gold accent, editorial typography
- 🔄 **Cache-busting** — `?_t=Date.now()` on every GET to bypass Spring cache
- 🛡️ **Safe rendering** — `toArray()` prevents "Objects not valid as React child" error
- 📊 **Live stats** — Dashboard shows real-time counts from all 4 services
- 🔔 **Toast notifications** — success / error / info with auto-dismiss

### Running the Frontend

```bash
cd librams-final
npm install
npm start
# Opens at http://localhost:3000
# All /api/* calls proxy to http://localhost:9095
```

---

## 🔗 Communication Patterns

### Service Discovery
All services self-register with **Eureka Server** on startup via `@EnableEurekaClient`.

### API Gateway Routing

| Incoming Path | Routed To |
|---|---|
| `/api/books/**` | `book-service :9090` |
| `/api/users/**` | `user-service :9091` |
| `/api/loan/**` | `loan-service :9092` |
| `/api/fines/**` | `fine-service :9093` |

> **Note**: Gateway uses `spring-cloud-starter-gateway-server-webmvc` (MVC/Servlet stack — NOT WebFlux). CORS is configured via `WebMvcConfigurer.addCorsMappings()`.

### Feign Inter-Service Communication

```
Borrow Flow:
  React → Gateway → LoanService
                        ├── BookClient.checkAvailability(title)   → BookService
                        ├── UserClient.checkUserStatus(userId)    → UserService
                        └── BookClient.issueBook(title)           → BookService
```

### Distributed Tracing
**Zipkin** + **Spring Cloud Sleuth** propagate trace/span IDs across all hops.  
View traces at: `http://localhost:9411`

### Cache Strategy
All services use Spring `@Cacheable` / `@CacheEvict` to reduce DB load:

```java
// Read  — served from cache after first load
@Cacheable(value = "books", key = "'all'")
public List<Book> findAll() { ... }

// Write — evict all entries so next read fetches fresh data
@CacheEvict(value = "books", allEntries = true)
public Book createBook(Book book) { ... }
```

---

## 🛠️ Technology Stack

| Category | Technology |
|---|---|
| Language | Java 21 |
| Backend Framework | Spring Boot 3.5 |
| Cloud | Spring Cloud (Eureka, Gateway MVC, OpenFeign, Sleuth) |
| Databases | MySQL 8.0 — one DB per service |
| Build Tool | Maven |
| Frontend | React 18 + Hooks |
| Frontend Build | Vite / Create React App |
| HTTP Layer | Fetch API + custom `req()` utility |
| Tracing | Zipkin + Spring Cloud Sleuth |
| Containerization | Docker (optional) |

---

## 🚀 Setup & Running

### Prerequisites

- ☑️ Java 21
- ☑️ Maven 3.6+
- ☑️ MySQL 8 — create these databases:
  ```sql
  CREATE DATABASE book_db;
  CREATE DATABASE user_db;
  CREATE DATABASE loan_db;
  CREATE DATABASE fine_db;
  ```
- ☑️ Node.js 18+ and npm

---

### Step 1 — Start Zipkin

```bash
# Docker (recommended)
docker run -d -p 9411:9411 openzipkin/zipkin

# Or from JAR
java -jar zipkin.jar
```
> Zipkin UI: `http://localhost:9411`

---

### Step 2 — Start Eureka Server

```bash
cd eureka-server
mvn spring-boot:run
```
> Eureka Dashboard: `http://localhost:8761`

---

### Step 3 — Start Microservices

Open **separate terminals** for each service:

```bash
# Terminal 1 — Book Service
cd book-service && mvn spring-boot:run

# Terminal 2 — User Service
cd user-service && mvn spring-boot:run

# Terminal 3 — Loan Service
cd loan-service && mvn spring-boot:run

# Terminal 4 — Fine Service
cd fine-service && mvn spring-boot:run
```

---

### Step 4 — Start API Gateway

```bash
cd api-gateway-server
mvn spring-boot:run
```
> Gateway: `http://localhost:9095`

---

### Step 5 — Start React Frontend

```bash
cd librams-final
npm install
npm start
```
> Frontend: `http://localhost:3000`

---

### ✅ Startup Checklist

| Service | Health Check URL |
|---|---|
| Eureka Dashboard | `http://localhost:8761` |
| API Gateway | `http://localhost:9095/actuator/health` |
| Zipkin UI | `http://localhost:9411` |
| Admin Server | `http://localhost:8900` |
| React Frontend | `http://localhost:3000` |
| Books API | `http://localhost:9095/api/books` |
| Users API | `http://localhost:9095/api/users` |

---

## 🧪 Testing the System

### End-to-End Borrow Flow

```bash
# 1. Register a user
curl -X POST http://localhost:9095/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"userName":"john","email":"john@test.com","password":"pass","role":"STUDENT"}'

# 2. Add a book
curl -X POST http://localhost:9095/api/books/addbook \
  -H "Content-Type: application/json" \
  -d '{"title":"Clean Code","author":"Robert Martin","category":"Programming","totalCopies":3}'

# 3. Borrow the book
curl -X POST http://localhost:9095/api/loan/borrowbook \
  -H "Content-Type: application/json" \
  -d '{"title":"Clean Code","userId":1}'

# 4. Return the book
curl -X PUT http://localhost:9095/api/loan/returnbook/1
```

**What happens behind the scenes on borrow:**

```
1. React   ──────────────► API Gateway (:9095)
2. Gateway ──────────────► Loan-Service (:9092)
3. Loan-Service ─────────► Book-Service — is book available?
4. Loan-Service ─────────► User-Service — is user eligible to borrow?
5. Loan-Service ─────────► Book-Service — decrement copiesAvailable
6. Loan-Service ─────────► Creates Loan record in MySQL
7. Zipkin  ───────────────► Captures full trace across all hops
```

> 📊 View the full distributed trace at: `http://localhost:9411`

---

## 📂 Repository Structure

```
Library-Managment-System-using-microservices/
├── eureka-server/          # Service registry :8761
├── api-gateway-server/     # API Gateway :9095
├── admin-server/           # Spring Boot Admin :8900
├── book-service/           # Book-Catalog microservice :9090
├── user-service/           # User microservice :9091
├── loan-service/           # Loan microservice :9092
├── fine-service/           # Fine microservice :9093
├── librams-final/          # React 18 frontend :3000
├── .gitignore
└── README.md
```

---

## 📚 References

- [Spring Boot Microservices — GeeksforGeeks](https://www.geeksforgeeks.org/springboot/java-spring-boot-microservices-example-step-by-step-guide/)
- [Spring.io Microservices Overview](https://spring.io/microservices)
- [Spring Cloud Gateway MVC Docs](https://docs.spring.io/spring-cloud-gateway/docs/current/reference/html/)
- [Auth0 — Java Spring Boot Microservices](https://auth0.com/blog/java-spring-boot-microservices/)
- [Building Microservices with Spring Boot — YouTube](https://www.youtube.com/watch?v=Us5acS_3pik)

---

<div align="center">

Built with ☕ Java 21 · 🍃 Spring Boot 3 · ☁️ Spring Cloud · ⚛️ React 18

</div>
