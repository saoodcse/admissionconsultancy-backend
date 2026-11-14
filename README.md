```markdown
# 🎓 Admission Consultancy Backend

A production-grade, highly scalable backend service built for managing admission processes, university courses, colleges, student leads, and administrative workflows. Designed with robust security, containerized metrics tracking, and database migrations.

---

## 🚀 Tech Stack & Architecture

* **Framework:** Spring Boot (v4.1.0)
* **Core Language:** Java 21 (leveraging modern virtual threads and performance optimizations)
* **Database & Persistence:** MySQL 8.0, Spring Data JPA / Hibernate
* **Database Migrations:** Flyway (fully automated and tracked schema evolution)
* **Connection Pooling:** HikariCP (`AdmissionHikariPool`)
* **Security & Auth:** Spring Security with JSON Web Tokens (JWT)
* **Documentation:** SpringDoc OpenAPI & Swagger UI
* **Monitoring & Metrics:** Spring Boot Actuator, Micrometer, Prometheus, Grafana, and ELK Stack (Elasticsearch & Filebeat) integration for structured logging.

---

## 📂 Project Structure

```tree
src/
├── main/
│   ├── java/
│   │   └── com/admissionconsultancy/
│   │       ├── config/        # Security, Database, and App Configurations
│   │       ├── controller/    # REST API Controllers
│   │       ├── model/         # JPA Entities & Enums
│   │       ├── repository/    # Spring Data JPA Repositories
│   │       ├── security/      # JWT Filters and Security Handlers
│   │       └── service/       # Business Logic Layer
            ..............
            ..............
│   └── resources/
│       ├── db/migration/      # Flyway SQL Migration Scripts (V1__init_schema.sql)
│       └── openapi/           # OpenAPI / Swagger YAML Specifications
└── test/
    └── java/com/admissionconsultancy/ # Unit and Integration Tests

```

---

## 🛠️ Prerequisites & Local Setup

Ensure you have the following installed on your local machine:

* **Java Development Kit (JDK) 21**
* **Maven 3.8+**
* **MySQL Server 8.0+**

### 1. Database Configuration

Create a local MySQL database named `admission_db`:

```sql
CREATE DATABASE admission_db;

```

### 2. Configure Environment Variables (Optional)

The application defaults to local development credentials, but you can override them using environment variables:

* `SERVER_PORT` (Default: `8080`)
* `DB_URL` (Default: `jdbc:mysql://localhost:3306/admission_db?...`)
* `DB_USERNAME` (Default: `root`)
* `DB_PASSWORD` (Default: `root#1234`)

### 3. Build and Run the Application

Open your terminal in the project root directory and run:

```bash
# Clean and compile the project
mvn clean compile

# Run the Spring Boot application
mvn spring-boot:run

```

---

## 🔄 Database Migrations (Flyway)

This project uses **Flyway** for version-controlled database schema management.

* Migration scripts are located under `src/main/resources/db/migration/`.
* On application startup, Flyway automatically validates and applies pending SQL scripts (`V1__init_schema.sql`) against your MySQL instance.
---

## 📚 API Documentation & Swagger UI

Once the application is successfully running, you can explore and test all available REST endpoints interactively via Swagger UI:

* **URL:** `http://localhost:8080/swagger-ui/index.html`

---

## 📊 Monitoring & Health Checks

Spring Boot Actuator is enabled for production telemetry:

* **Health Check:** `http://localhost:8080/actuator/health`
* **Prometheus Metrics:** `http://localhost:8080/actuator/prometheus`
