# Library-Managment-System-using-microservices
A scalable Library Management System built with Spring Boot 3+ and Spring Cloud microservices architecture. Manages books (MongoDB), users/loans/fines (MySQL) without notification service. Features Eureka discovery, API Gateway routing, Feign inter-service communication, and Zipkin tracing.

The system consists of four independent services:

- **Book-Catalog Service** (MongoDB) – manages book metadata and inventory.
- **User-Service** (MySQL) – handles user registration, roles, and JWT authentication.
- **Loan-Service** (MySQL) – orchestrates borrowing and returning of books.
- **Fine-Service** (MySQL) – calculates overdue fines periodically.

All services register with **Eureka** for discovery, communicate via **Feign clients**, and are exposed through an **API Gateway**. **Zipkin** provides end‑to‑end request tracing.

---

## Services

### 1. Book-Catalog Service
- **Database**: MongoDB (non‑relational, flexible schema for book metadata).
- **Domain**: `Book` entity with fields: `isbn` (primary key), `title`, `author`, `categories`, `copiesAvailable`.
- **Endpoints**:
  - `POST /books` – add a new book.
  - `GET /books/{isbn}` – retrieve book details.
  - `GET /books/search?title=xyz` – search books by title.
- **Function**: Provides availability checks for the Loan-Service via Feign.

### 2. User-Service
- **Database**: MySQL (relational, ACID compliant for user accounts).
- **Domain**: `LibraryUser` with fields: `id`, `email`, `role` (MEMBER/LIBRARIAN).
- **Endpoints**:
  - `POST /users/register` – create a new user.
  - `GET /users/{id}/isMember` – check if a user is an active member.
- **Security**: JWT authentication integrated (tokens are issued/validated, but details are out of scope for this README).

### 3. Loan-Service
- **Database**: MySQL (ensures transactional integrity for loan operations).
- **Domain**: `Loan` entity with fields: `id`, `isbn`, `userId`, `borrowedAt`, `dueDate`, `returnedAt`, `status`.
- **Business logic**:
  - Before creating a loan, validate book availability (`Book-Catalog`) and user membership (`User-Service`) via Feign clients.
  - Atomically decrement/increment `copiesAvailable` in the Book-Catalog service.
- **Endpoints**:
  - `POST /loans/borrow` – borrow a book.
  - `PUT /loans/{id}/return` – return a book (updates loan status and book inventory).

### 4. Fine-Service
- **Database**: MySQL.
- **Domain**: `Fine` entity with fields: `loanId`, `amount`, `overdueSince`, `status` (PENDING/PAID).
- **Background processing**:
  - A `@Scheduled` cron job runs daily, scanning all active loans that are overdue.
  - For each overdue loan, it calculates the fine as `daysOverdue * dailyFineRate` and creates/updates a `Fine` record.
- **Endpoints**: (Optional endpoints for querying/paying fines could be added; not specified in the original design.)

---

## Communication Patterns

- **Service Discovery**: All services (including the gateway) register with a **Eureka server**.
- **Synchronous HTTP calls**: Services use **Feign clients** with Spring Cloud LoadBalancer to call each other.
  - Loan-Service declares Feign clients to Book-Catalog and User-Service:
    ```java
    @FeignClient(name = "book-catalog")
    public interface BookClient {
        @GetMapping("/books/{isbn}/available")
        boolean isAvailable(@PathVariable String isbn);
    }

    @FeignClient(name = "user-service")
    public interface UserClient {
        @GetMapping("/users/{id}/isMember")
        boolean isActiveMember(@PathVariable String id);
    }
    ```
- **API Gateway**: All external requests go through the **Spring Cloud Gateway**, which routes:
  - `/books/**` → book-catalog service
  - `/loans/**` → loan-service
  - (Other services can be added similarly)
- **Distributed Tracing**: **Zipkin** is used to trace requests across service boundaries. Trace and span IDs are automatically propagated via Sleuth.

---

## Technology Stack

- **Java 17+**
- **Spring Boot 3+**
- **Spring Cloud** (Eureka, Gateway, OpenFeign, Sleuth)
- **Databases**:
  - MongoDB (Book-Catalog)
  - MySQL (User, Loan, Fine)
- **Build tool**: Maven (multi‑module project)
- **Testing**: JUnit 5, Mockito
- **Tracing**: Zipkin

---

## Setup and Running

### Prerequisites
- Java 17 or later
- Maven
- Docker (optional, for running MySQL, MongoDB, and Zipkin easily)

### Step 1: Start Infrastructure Services
It is recommended to run Zipkin, MySQL, and MongoDB using Docker:
```bash
# Zipkin
docker run -d -p 9411:9411 openzipkin/zipkin

# MySQL (for User, Loan, Fine)
docker run -d -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root mysql:8

# MongoDB (for Book-Catalog)
docker run -d -p 27017:27017 mongo:6
```
Alternatively, you can run Zipkin directly from its JAR:
```bash
java -jar zipkin.jar
```

### Step 2: Start Eureka Server
```bash
cd eureka-server
mvn spring-boot:run
```

### Step 3: Start the Microservices
In separate terminals, run each service:
```bash
cd book-catalog
mvn spring-boot:run

cd user-service
mvn spring-boot:run

cd loan-service
mvn spring-boot:run

cd fine-service
mvn spring-boot:run
```

### Step 4: Start API Gateway
```bash
cd api-gateway
mvn spring-boot:run
```

All services will register with Eureka (typically on `localhost:8761`). The gateway listens on port `8080`.

---

## Testing

Once all services are up, you can test a borrow flow via the gateway:

```bash
curl -X POST http://localhost:8080/loans/borrow \
  -H "Content-Type: application/json" \
  -d '{"isbn":"1234567890", "userId":"1"}'
```

This request triggers:
1. Gateway → Loan-Service
2. Loan-Service calls `book-catalog` to check availability.
3. Loan-Service calls `user-service` to verify membership.
4. Loan-Service creates the loan and updates the book’s `copiesAvailable`.
5. Zipkin captures the entire trace – view it at `http://localhost:9411`.

---

## References

- [Spring Boot Microservices Example – GeeksforGeeks](https://www.geeksforgeeks.org/springboot/java-spring-boot-microservices-example-step-by-step-guide/)
- [Spring.io Microservices](https://spring.io/microservices)
- [Auth0 Blog – Java Spring Boot Microservices](https://auth0.com/blog/java-spring-boot-microservices/)
- [Building Microservices with Spring Boot – YouTube](https://www.youtube.com/watch?v=Us5acS_3pik)
- [Example Repository (Inspiration)](https://github.com/ioeltadeu/library-management)