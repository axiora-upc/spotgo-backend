# SpotGo Backend

REST API for managing parking facilities, real-time spot detection, reservations, and billing in the SpotGo smart parking platform. Built with Spring Boot 4.0 and Java 26.

## Tech Stack

- **Java 26** — Latest LTS
- **Spring Boot 4.0.7** — Web, Data JPA, Validation, DevTools
- **PostgreSQL** — Primary database
- **Springdoc OpenAPI 3.0.3** — API documentation (Swagger UI)
- **Lombok** — Boilerplate reduction
- **DDD** — Domain-driven design with clear bounded contexts

## Architecture

The project follows **Domain-Driven Design** with a **hexagonal (ports & adapters)** structure, organized into four bounded contexts:

```
src/main/java/com/axiora/spotgo/
├── parking/        # Core domain: parkings, blueprints, spots, reservations
├── monitoring/     # Employees, occupancy by hour, weekly trends
├── billing/        # Billing domain: receipts, subscriptions, client plans
└── shared/         # Shared kernel: base entities, error handling, config
```

Each context follows the same layered pattern:

| Layer | Purpose |
|-------|---------|
| `domain/model/aggregates/` | Domain entities / aggregates |
| `domain/model/valueobjects/` | Value objects |
| `domain/model/commands/` | CQRS commands |
| `domain/model/queries/` | CQRS queries |
| `application/internal/commandservices/` | Command handlers |
| `application/internal/queryservices/` | Query handlers |
| `interfaces/rest/` | REST controllers |
| `interfaces/rest/resources/` | Request/response DTOs |
| `infrastructure/persistence/jpa/` | JPA repositories & entities |

## API Endpoints

### Parking

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/parkings` | Create a parking facility |
| `GET` | `/api/v1/parkings` | Get all parkings |
| `PATCH` | `/api/v1/parkings/{parkingId}` | Update parking rating |
| `POST` | `/api/v1/blueprints` | Create a blueprint (map) |
| `GET` | `/api/v1/blueprints` | Get all blueprints (optional `?parkingId=`) |
| `GET` | `/api/v1/blueprints/parking/{parkingId}` | Get blueprints by parking |
| `DELETE` | `/api/v1/blueprints/{blueprintId}` | Delete a blueprint |
| `POST` | `/api/v1/detectedSpots` | Create a detected spot |
| `GET` | `/api/v1/detectedSpots` | Get all spots (optional `?parkingId=` / `?blueprintId=`) |
| `GET` | `/api/v1/detectedSpots/blueprint/{blueprintId}` | Get spots by blueprint |
| `PATCH` | `/api/v1/detectedSpots/{spotId}/status?status=` | Update spot status |
| `POST` | `/api/v1/reservations` | Reserve a spot |
| `GET` | `/api/v1/reservations` | Get all reservations (optional `?parkingId=`) |

### Billing

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/v1/clientPlans` | Get all subscription plans |
| `GET` | `/api/v1/clientPlans/{clientPlanId}` | Get plan by ID |
| `POST` | `/api/v1/receipts` | Create a receipt |
| `GET` | `/api/v1/receipts` | Get all receipts (optional `?bookingCode=`) |
| `GET` | `/api/v1/receipts/{receiptId}` | Get receipt by ID |
| `DELETE` | `/api/v1/receipts/{receiptId}` | Delete a receipt |
| `POST` | `/api/v1/subscriptions` | Create a subscription |
| `GET` | `/api/v1/subscriptions` | Get all subscriptions |
| `GET` | `/api/v1/subscriptions/{subscriptionId}` | Get subscription by ID |
| `PUT` | `/api/v1/subscriptions/{subscriptionId}` | Update a subscription |
| `PATCH` | `/api/v1/subscriptions/{subscriptionId}` | Patch subscription savings |

### Monitoring

| Method | Path | Description |
|--------|------|-------------|
| `POST` | `/api/v1/employees` | Create an employee |
| `GET` | `/api/v1/employees` | Get all employees |
| `PUT` | `/api/v1/employees/{employeeId}` | Update an employee |
| `DELETE` | `/api/v1/employees/{employeeId}` | Delete an employee |
| `GET` | `/api/v1/occupancyByHour` | Get occupancy by hour (optional `?parkingId=`) |
| `GET` | `/api/v1/weeklyTrends` | Get weekly occupancy trends (optional `?parkingId=`) |

## Prerequisites

- **Java 26** (or compatible JDK)
- **PostgreSQL** running on `localhost:5432`
- **Maven** (or use the bundled `mvnw` wrapper)


### Seed data

On startup, `DbSeeder` reads `src/main/resources/db.json` and loads it into PostgreSQL — but only if the `parkings` table is empty. With `app.seeder.reset-before-seed=true`, it wipes all tables first and reseeds every time the app starts. Turn that off (`false`) once you have real data you don't want to lose.

Note that `db.json`'s own ids (text-based) are not preserved — every row gets a new auto-generated numeric id once it's in PostgreSQL.

## Running Locally

```bash
# Start PostgreSQL (adjust to your setup)

# Run the app
./mvnw spring-boot:run
```