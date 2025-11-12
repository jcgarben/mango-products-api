# Mango Products API (Technical Test)

Spring Boot REST API to manage **products and their price history** with temporal validation.

Built following **Hexagonal Architecture (Ports & Adapters)**, **API-First (OpenAPI)**, with **PostgreSQL**, **OpenAPI/Swagger**, **unit + E2E tests**, **Docker**, **JaCoCo coverage**, and **k6 performance benchmark**.

---

## 📑 Table of Contents

- [Technology Stack](#️-technology-stack)
- [Architecture: Hexagonal (Ports & Adapters)](#️-architecture-hexagonal-ports--adapters)
- [Project Structure](#-project-structure)
- [Quickstart](#-quickstart)
- [Database (PostgreSQL)](#️-database-postgresql)
- [API (OpenAPI/Swagger)](#-api-openapiswagger)
- [Testing & Coverage](#-testing--coverage)
- [Performance Benchmark (Bonus)](#-performance-benchmark-bonus)
- [Design Notes](#-design-notes)
- [Future Improvements](#-future-improvements)

---

## 🛠️ Technology Stack

| Component         | Version         |
|-------------------|-----------------|
| Java              | 21              |
| Spring Boot       | 3.5.6           |
| Maven             | 3.9.9           |
| PostgreSQL        | 17 (Alpine)     |
| Flyway            | 11.10.0         |
| JUnit             | 5.x             |
| JaCoCo            | 0.8.12          |
| Testcontainers    | 1.21.3          |
| REST Assured      | Latest          |
| springdoc-openapi | 2.6.0           |
| Docker            | Latest          |
| k6                | 0.54.0          |

---

## 🏗️ Architecture: Hexagonal (Ports & Adapters)

```
+-------------------+
|  REST Controllers |  <-- Adapter Inbound (OpenAPI generated)
+-------------------+
         |
         v
+-------------------+
| Application Layer |  <-- Use Cases (CreateProduct, AddPrice, GetPrices)
+-------------------+
         |
         v
+-------------------+
|   Domain Model    |  <-- Pure Java: Product, Price, PriceOverlapValidator
+-------------------+
         ^
         |
+-------------------+
| Persistence Layer |  <-- Adapter Outbound (JPA/PostgreSQL)
+-------------------+
```

**Key principles:**
- Domain layer has **zero dependencies** on Spring or infrastructure
- Use cases orchestrate domain logic without knowing infrastructure details
- PostgreSQL GIST constraint enforces price overlap prevention at DB level

---

## 📦 Project Structure

```
src/
  main/
    java/com/mango/products/
      domain/
        model/            # Entities: Product, Price
        repository/       # Repository interfaces (ports)
        service/          # Domain services: PriceOverlapValidator
        exception/        # Business exceptions
      application/
        usecase/          # Use cases: CreateProduct, AddPrice, GetPrices
      infrastructure/
        persistence/      # JPA repositories & entities (adapters)
        rest/
          controller/     # REST controllers (adapters)
          mapper/         # DTO mappers
    resources/
      db/
        migration/        # Flyway migrations (V1__create_products.sql)
        seed/             # Seed data (R__seed_reference_data.sql)
      static/
        openapi.yaml      # OpenAPI 3.0.3 specification
      application.yml
  test/
    java/
      domain/             # Domain unit tests (30)
      application/        # Use case tests (15)
      e2e/                # E2E tests with Testcontainers (10)
```

---

## 🔥 Quickstart

### Option 1: Docker Compose (Recommended)

```bash
# Build & run everything (PostgreSQL + API)
docker-compose up --build

# API available at http://localhost:8080
# OpenAPI spec: http://localhost:8080/openapi.yaml
```

### Option 2: Local Development

```bash
# Start PostgreSQL only
docker-compose up postgres -d

# Run application with Maven
mvn spring-boot:run

# API available at http://localhost:8080
```

---

## 🗄️ Database (PostgreSQL)

- **PostgreSQL 17** with **btree_gist extension** for temporal validation
- **Flyway migrations** for schema versioning
- **GIST Constraint** to prevent price overlaps:

```sql
CREATE EXTENSION IF NOT EXISTS btree_gist;

ALTER TABLE product_prices
ADD CONSTRAINT ux_product_currency_period
EXCLUDE USING GIST (
    product_id WITH =,
    currency WITH =,
    daterange(init_date, end_date, '[]') WITH &&
);
```

**Why PostgreSQL instead of H2?**
- ✅ **Production-realistic** behavior
- ✅ **Advanced constraints** (GIST) not available in H2
- ✅ **Temporal queries** with native date range support
- ✅ **Testcontainers** for reliable E2E tests

**Seed Data:**
- Located in `db/seed/R__seed_reference_data.sql` (repeatable migration)
- Creates 2 example products with price history
- Sequence starts at ID 3 to avoid conflicts with k6 benchmark

---

## 📖 API (OpenAPI/Swagger)

**Access the API documentation:**

- **OpenAPI Spec**: [http://localhost:8080/openapi.yaml](http://localhost:8080/openapi.yaml)

**Main endpoints:**

### Create Product
```http
POST /products
Content-Type: application/json

{
  "name": "Zapatillas deportivas",
  "description": "Modelo 2025 edición limitada"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Zapatillas deportivas",
  "description": "Modelo 2025 edición limitada"
}
```

### Get Product by ID
```http
GET /products/{id}
```

**Response:**
```json
{
  "id": 1,
  "name": "Zapatillas deportivas",
  "description": "Modelo 2025 edición limitada"
}
```

### Add Price to Product
```http
POST /products/{id}/prices
Content-Type: application/json

{
  "value": 99.99,
  "currency": "EUR",
  "initDate": "2025-01-01",
  "endDate": "2025-06-30"
}
```

**Response:**
```json
{
  "id": 1,
  "value": 99.99,
  "currency": "EUR",
  "initDate": "2025-01-01",
  "endDate": "2025-06-30"
}
```

**💰 Multi-Currency Support:**

You can add prices in different currencies for the same period:

```http
# Add EUR price
POST /products/1/prices
{"value": 99.99, "currency": "EUR", "initDate": "2025-01-01", "endDate": null}

# Add USD price for same period (✅ ALLOWED)
POST /products/1/prices
{"value": 119.99, "currency": "USD", "initDate": "2025-01-01", "endDate": null}

# Add another EUR price for same period (❌ CONFLICT - 409)
POST /products/1/prices
{"value": 89.99, "currency": "EUR", "initDate": "2025-01-01", "endDate": null}
```

**Supported currencies:** Any valid ISO 4217 code (EUR, USD, GBP, JPY, CNY, etc.)

### Get Price History
```http
GET /products/{id}/prices
```

**Response (multiple currencies):**
```json
{
  "id": 1,
  "name": "Zapatillas deportivas",
  "description": "Modelo 2025 edición limitada",
  "prices": [
    {
      "id": 1,
      "value": 99.99,
      "currency": "EUR",
      "initDate": "2025-01-01",
      "endDate": "2025-06-30"
    },
    {
      "id": 2,
      "value": 119.99,
      "currency": "USD",
      "initDate": "2025-01-01",
      "endDate": "2025-06-30"
    },
    {
      "id": 3,
      "value": 149.99,
      "currency": "EUR",
      "initDate": "2025-07-01",
      "endDate": null
    }
  ]
}
```

**Filter by currency:**
```http
GET /products/{id}/prices?currency=EUR
```

### Get Current Price by Date

**Single currency available:**
```http
GET /products/{id}/prices?date=2025-03-15
```

**Response (when only one currency exists for that date):**
```json
{
  "value": 99.99,
  "currency": "EUR"
}
```

**Multiple currencies available:**
```http
GET /products/{id}/prices?date=2025-03-15
```

**Response (when multiple currencies exist for that date):**
```json
{
  "id": 1,
  "name": "Zapatillas deportivas",
  "description": "Modelo 2025 edición limitada",
  "prices": [
    {
      "id": 1,
      "value": 99.99,
      "currency": "EUR",
      "initDate": "2025-01-01",
      "endDate": "2025-06-30"
    },
    {
      "id": 2,
      "value": 119.99,
      "currency": "USD",
      "initDate": "2025-01-01",
      "endDate": "2025-06-30"
    }
  ]
}
```

**Get current price in specific currency:**
```http
GET /products/{id}/prices?date=2025-03-15&currency=USD
```

**Response:**
```json
{
  "value": 119.99,
  "currency": "USD"
}
```

**Error responses:**
- `404` → Product or price not found
- `409` → Conflict (duplicate name or price overlap)
- `400` → Invalid request data
- `500` → Internal server error

---

## 🧪 Testing & Coverage

**Run all tests:**
```bash
mvn test
```

**Generate coverage report:**
```bash
mvn test jacoco:report

# View report at: target/site/jacoco/index.html
```

**Test breakdown:**

| Type | Count | Coverage | Tools |
|------|-------|----------|-------|
| **Unit tests** | 58 | 90%+ | JUnit 5, Mockito |
| **E2E tests** | 10 | Integration | REST Assured, Testcontainers |
| **Total** | **68** | **>90% (Domain + Application)** | JaCoCo |

**Test structure:**
- **Domain tests**: Pure business logic (entities, services, validators)
- **Application tests**: Use case orchestration
- **E2E tests**: Full request/response cycle with real PostgreSQL

---

## ⚡ Performance Benchmark (Bonus)

Automated performance testing with **k6**.

**Run benchmark:**

```bash
docker-compose --profile benchmark up --build
```

**What it does:**
- Starts PostgreSQL + API
- Waits for application to be healthy
- Executes k6 benchmark for **1 minute**:
  - Up to **100 concurrent users** (peak)
  - **~10,000 HTTP requests**
  - **~5,000 iterations**
  - Mixed scenarios: 30% CREATE, 40% GET products, 30% GET prices


**Results saved to:** `./k6-results/k6-results.json`

**Resource limits:**

| Container | CPU | RAM | Type |
|-----------|-----|-----|------|
| PostgreSQL | 0.5 (500 Mi) | 1 GB | Auxiliary (limited per requirements) |
| API | 1.0 | 1 GB | Application (no restriction) |
| k6 | 1.0 | 1 GB | Benchmark script (no restriction) |

> 💡 **Note:** Auxiliary containers are limited to 1 GB RAM and 500 Mi CPU as per technical test requirements.

---

## 📝 Design Notes

### Price Overlap Prevention

**Challenge:** Ensure that for a given product, no two price periods overlap **for the same currency**.

**Solution:** PostgreSQL **GIST constraint** with `btree_gist` extension + currency dimension.

```sql
EXCLUDE USING GIST (
    product_id WITH =,
    currency WITH =,      -- ✅ Overlap validation PER currency
    daterange(init_date, end_date, '[]') WITH &&
);
```

**Advantages:**
- ✅ Validation at database level (100% reliable)
- ✅ Handles open-ended ranges (`endDate = null`)
- ✅ Prevents race conditions
- ✅ Allows EUR and USD prices for the same period
- ✅ Prevents EUR-EUR overlaps while allowing EUR-USD coexistence
- ✅ No complex application logic needed

### Multi-Currency Support

**Implementation:**
- Uses `java.util.Currency` (type-safe, validates ISO 4217 codes)
- Overlap validation considers currency dimension
- Composite index `(product_id, currency)` for optimal queries
- Currency filtering in GET endpoints (`?currency=EUR`)

**Key benefits:**
- 🌍 International pricing support
- 💱 Same period, different currencies allowed
- 🔒 Currency-aware overlap prevention
- 📊 Filter price history by currency

### Domain-Driven Design

**PriceOverlapValidator:**
- Domain service that validates price overlap rules
- Pure Java, no infrastructure dependencies
- Used in application layer before persistence

**Price Entity:**
- Validates that `initDate` is before `endDate`
- Immutable value objects for temporal data
- Business rules encapsulated in domain model

### API-First Approach

- **OpenAPI 3.0.3** specification defines the contract
- **OpenAPI Generator** creates DTOs and API interfaces
- Controllers implement generated interfaces
- Contract always synchronized with implementation

### Flyway Migrations

- **Versioned migrations**: `V1__create_products.sql`
- **Repeatable migrations**: `R__seed_reference_data.sql`
- **Idempotent seed data**: Can be run multiple times
- **Sequence configuration**: Starts at ID 3 for k6 compatibility

### Testcontainers Pattern

- **Singleton container** pattern for faster test execution
- Real PostgreSQL in Docker for E2E tests
- Clean database state between test suites
- Production-realistic testing environment

---

## 📊 Requirements Compliance

| Requirement | Status | Details |
|-------------|--------|---------|
| ✅ **Correct modeling** | Complete | Product 1:N Price with temporal validation |
| ✅ **Robust validation** | Complete | GIST constraint + domain validators |
| ✅ **RESTful design** | Complete | OpenAPI 3.0.3 specification |
| ✅ **Clean code** | Complete | SOLID, DDD, Hexagonal Architecture |
| ✅ **Stack justification** | Complete | See [Design Notes](#-design-notes) |
| ✅ **Performance** | Complete | p95: 299ms, 249 req/s, 0% errors |
| ✅ **Tests** | 68 tests | 90%+ coverage on Domain/Application |
| ✅ **Documentation** | Complete | README + OpenAPI + inline comments |
| ✅ **Bonus: Benchmark** | Implemented | k6 automated (1 min) |
| ✅ **Bonus: Seed data** | Implemented | Flyway repeatable migration |

---

## 👨‍💻 For Technical Reviewers


### Quick verification (3 commands):

```bash
# 1. Run application + benchmark 
docker-compose up --build
docker-compose up --profile benchmark --build

# 2. Run tests + coverage (~1 min)
mvn test jacoco:report

# 3. View documentation
# - OpenAPI: http://localhost:8080/openapi.yaml
# - Coverage: target/site/jacoco/index.html
```

---
