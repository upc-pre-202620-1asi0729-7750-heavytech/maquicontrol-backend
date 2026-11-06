# MaquiControl Platform

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE.md)

## Overview

MaquiControl Platform is the RESTful API behind MaquiControl, the AndesHeavyTech product for Peruvian heavy machinery companies. It is built with Domain-Driven Design and CQRS and organized into bounded contexts that match chapter 4 of the project report: `shared` (the kernel), `iam`, `profiles`, `fleet`, `rental`, `maintenance`, `operations`, `subscription`, and `billing`. Every command outcome is a `Result`, every aggregate stays free of JPA annotations, and every domain event goes through a transactional outbox.

## Features

- **`/api/v1/authentication`**: sign up and sign in, returning a JWT (`iam`).
- **`/api/v1/profiles`, `/api/v1/organizations`**: create and update a person's profile and their company (`profiles`).
- **`/api/v1/machinery`, `/api/v1/categories`**: register, update, change status of, and remove machinery and categories (`fleet`).
- **`/api/v1/rentals`**: request, confirm, reject, and cancel reservations (`rental`).
- **`/api/v1/maintenances`, `/api/v1/breakdown-reports`**: schedule maintenance and report breakdowns (`maintenance`).
- **`/api/v1/service-operations`**: start and complete operations, register and validate worked hours (`operations`).
- **`/api/v1/subscriptions`**: subscribe to a plan, pay with Mercado Pago, and cancel (`subscription`).
- **`/api/v1/invoices`**: issue invoices (`billing`).
- **`/health`**: liveness check.
- **Interactive API docs**: Swagger UI at `/swagger-ui/index.html`.

## Technologies

- Java 27 and Spring Boot 4
- Apache Maven (or the bundled `./mvnw` wrapper)
- Spring Web, Spring Data JPA (Hibernate) with MySQL
- Spring Security with JWT (`io.jsonwebtoken`)
- Spring Validation (Jakarta Bean Validation)
- SpringDoc OpenAPI 3 / Swagger UI
- Lombok
- Docker

## Prerequisites

- Java 27 or higher
- MySQL 8+ running locally
- Docker (optional, for the containerized build)

## Environment variables

| Variable | Description | Default (dev) |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `dev` |
| `DATABASE_USER` | MySQL username | `root` |
| `DATABASE_PASSWORD` | MySQL password | `password` |
| `DATABASE_URL` | MySQL host | `localhost` |
| `DATABASE_PORT` | MySQL port | `3306` |
| `DATABASE_NAME` | Database name | `maquicontrol` |
| `PORT` | Application port | `8080` |
| `JWT_SECRET` | Secret key for JWT signing | required in `prod` only |

## Spring profiles

| Profile | Usage |
|---|---|
| `dev` | Local defaults for every variable above; `ddl-auto=update`; SQL logging on |
| `prod` | Every variable required from the environment; `ddl-auto=validate`; SQL logging off |

## Architecture notes

- `shared.application.result.Result` and `ApplicationError` model every command outcome without HTTP or domain concepts ([ADR-0001](docs/adrs.md)).
- Each bounded context splits its application layer into a `*CommandService` and a `*QueryService`, with one Command record per Event Storming command ([ADR-0007](docs/adrs.md)).
- No aggregate carries a JPA annotation; persistence lives in a parallel `*PersistenceEntity`, `*PersistenceAssembler`, and `*RepositoryImpl` ([ADR-0002](docs/adrs.md)).
- Domain events are stored in the `outbox_events` table in the same transaction as the aggregate and relayed afterwards by `OutboxEventRelay` ([ADR-0008](docs/adrs.md)).

## Error responses

Every error is an RFC 9457 `ProblemDetail`:

- Validation errors return `400 Bad Request`.
- A missing resource returns `404 Not Found`.
- A duplicate or conflicting resource returns `409 Conflict`.
- A business rule violation (for example, an invalid status transition) returns `422 Unprocessable Entity`.

## Getting Started

1. Clone the repository:
   ```bash
   git clone https://github.com/upc-pre-202620-1asi0729-7750-heavytech/maquicontrol-backend.git
   cd maquicontrol-backend
   ```
2. Set the environment variables listed above.
3. Build and run:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Open `http://localhost:8080/health` and `http://localhost:8080/swagger-ui/index.html`.

## Deployment

Build the image with the [Dockerfile](Dockerfile):

```bash
docker build -t maquicontrol-backend .
docker run -p 8080:8080 -e DATABASE_URL=... -e DATABASE_NAME=... -e DATABASE_USER=... -e DATABASE_PASSWORD=... -e JWT_SECRET=... maquicontrol-backend
```

The image runs with `SPRING_PROFILES_ACTIVE=prod`.

## Contributing

See [`CONTRIBUTING.md`](CONTRIBUTING.md).

## License

MIT, see [`LICENSE.md`](LICENSE.md).
