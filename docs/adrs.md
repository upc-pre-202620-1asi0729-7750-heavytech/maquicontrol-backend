# Architecture Decision Records

This document records significant architecture decisions for the MaquiControl Platform.
Each record captures the context, decision, and consequences so that future
changes can revisit decisions with their original trade-offs in view.

## Process

- Records are numbered sequentially and use the `ADR-NNNN` identifier.
- New records are appended; accepted records are not silently rewritten.
- When a decision changes, add a new ADR that supersedes the old one and mark the old one `Superseded`.
- Use `Proposed`, `Accepted`, `Rejected`, `Deprecated`, or `Superseded` as status.

## Index

| ADR | Title | Status |
|---|---|---|
| [ADR-0001](#adr-0001-resultt-e-for-railway-oriented-outcomes-instead-of-exceptions) | Result<T, E> for Railway-Oriented Outcomes Instead of Exceptions | Accepted |
| [ADR-0002](#adr-0002-hexagonal-persistence-the-domain-model-never-carries-a-jpa-annotation) | Hexagonal Persistence: the Domain Model Never Carries a JPA Annotation | Accepted |
| [ADR-0003](#adr-0003-value-objects-for-every-domain-primitive-with-a-validation-rule) | Value Objects for Every Domain Primitive With a Validation Rule | Accepted |
| [ADR-0004](#adr-0004-cross-context-integration-through-acl-facades-and-integration-events) | Cross-Context Integration Through ACL Facades and Integration Events | Accepted |
| [ADR-0005](#adr-0005-a-repository-adapters-save-hands-domain-events-to-the-domaineventpublisher) | A Repository Adapter's `save()` Hands Domain Events to the `DomainEventPublisher` | Accepted |
| [ADR-0006](#adr-0006-stateless-jwt-authentication-instead-of-server-side-sessions) | Stateless JWT Authentication Instead of Server-Side Sessions | Accepted |
| [ADR-0007](#adr-0007-cqrs-command-and-query-services-are-separate-contracts) | CQRS: Command and Query Services Are Separate Contracts | Accepted |
| [ADR-0008](#adr-0008-transactional-outbox-for-domain-events) | Transactional Outbox for Domain Events | Accepted |

## ADR-0001: Result<T, E> for Railway-Oriented Outcomes Instead of Exceptions

- **Status:** Accepted
- **Date:** 2026-11-06

### Context

Command handlers need to report an expected failure (a duplicate serial number, a reservation for machinery that is not available, an invalid status transition) distinctly from an unexpected one (a database outage, a programming error).

### Decision

Every command handler returns a sealed `Result<T, E>` (`Success`, `Failure`). `ApplicationError` is the shared error shape (`code`, `message`, `details`), with one factory per error family (`validationError`, `notFound`, `conflict`, `businessRuleViolation`, `unexpected`) whose `code` suffix drives the HTTP status in `ProblemDetailFromApplicationErrorAssembler`.

### Consequences

- A `handle()` signature states every outcome in its return type.
- One error shape and one REST error mapping for every bounded context.
- More ceremony than throwing an exception at a single call site.

## ADR-0002: Hexagonal Persistence: the Domain Model Never Carries a JPA Annotation

- **Status:** Accepted
- **Date:** 2026-11-06

### Context

`Machinery`, `Rental`, `Maintenance`, `ServiceOperation`, `Subscription`, and `Invoice` are the concepts the platform is about. Annotating them with `@Entity` would couple the domain model to Hibernate's mapping rules.

### Decision

Each aggregate has a parallel `*PersistenceEntity`, a `*PersistenceAssembler`, and a `*RepositoryImpl` adapter that implements the domain's own `*Repository` port through a Spring Data `*PersistenceRepository`. Every persistence entity extends `AuditableAbstractPersistenceEntity` for `id`, `createdAt`, and `updatedAt`.

### Consequences

- The domain model reads as business rules only.
- Four classes per aggregate instead of one.

## ADR-0003: Value Objects for Every Domain Primitive With a Validation Rule

- **Status:** Accepted
- **Date:** 2026-11-06

### Context

A RUC, an email address, a serial number, a money amount, or a date range is just a string or a number in the database, and each needs validation wherever it is built.

### Decision

These primitives are immutable records that validate in their compact constructor, persisted through a JPA converter or an embeddable.

### Consequences

- An aggregate cannot hold an invalid value, whatever code path built it.
- One converter or embeddable per persisted value object.

## ADR-0004: Cross-Context Integration Through ACL Facades and Integration Events

- **Status:** Accepted
- **Date:** 2026-11-06

### Context

`rental` needs to know whether a machinery is available (`fleet`), `billing` needs the amount of a completed rental, and `subscription` needs to talk to Mercado Pago. A direct dependency on another context's `domain` package would break as soon as that context changes.

### Decision

A context exposes an ACL facade in `interfaces.acl` (taking and returning primitives) and integration events in `interfaces.events`. Consumers wrap the facade in an `External*Service` in `application.internal.outboundservices.acl`. External systems (Mercado Pago, SUNAT) sit behind their own ACL service.

### Consequences

- No context imports another context's `domain` package.
- Facades and integration events must be kept backwards compatible.

## ADR-0005: A Repository Adapter's `save()` Hands Domain Events to the `DomainEventPublisher`

- **Status:** Accepted
- **Date:** 2026-11-06

### Context

Spring Data publishes `@DomainEvents` only for the object passed to `save()`. Our adapters save the `*PersistenceEntity`, not the aggregate, so the automatic mechanism never sees the events.

### Decision

Every `*RepositoryImpl.save()` calls `domainEventPublisher.publishAndClearEvents(aggregate)` once, right after the JPA save, inside the command's transaction.

### Consequences

- One line, the same in every adapter; a harmless no-op when there are no events.
- Forgetting the call still compiles, so it is part of code review.

## ADR-0006: Stateless JWT Authentication Instead of Server-Side Sessions

- **Status:** Accepted
- **Date:** 2026-11-06

### Context

The clients are the Angular web application and, later, mobile apps. There is no server-side session state worth scaling.

### Decision

`POST /api/v1/authentication/sign-in` issues an HMAC-signed JWT that expires after `authorization.jwt.expiration.days`. Every other endpoint, except sign-up, sign-in, `/health`, and the Swagger paths, requires `Authorization: Bearer <token>`.

### Consequences

- Any instance validates any token without a session store.
- A token cannot be revoked before it expires without adding a denylist.

## ADR-0007: CQRS: Command and Query Services Are Separate Contracts

- **Status:** Accepted
- **Date:** 2026-11-06

### Context

The report's Event Storming defines one command per business action. A single service per aggregate would mix writes and reads behind one interface.

### Decision

Every bounded context has a `*CommandService` (one `handle(XxxCommand)` per Event Storming command, returning `Result<T, ApplicationError>`) and a `*QueryService` (one `handle(XxxQuery)` per read model). Command names are the same in the report, the backend, and the web application.

### Consequences

- The command catalog in the report traces one to one to the code.
- Two services per aggregate instead of one.

## ADR-0008: Transactional Outbox for Domain Events

- **Status:** Accepted
- **Date:** 2026-11-06

### Context

Several business policies run on domain events: confirming a rental starts it, a payment activates a subscription, a completed rental is billed. Publishing an event directly from the command has two failure modes: the event is published but the transaction rolls back (a handler acts on data that does not exist), or the transaction commits but the process dies before the handlers run (the policy never happens).

### Decision

`DomainEventPublisher` does not dispatch events. It serializes each event as JSON into the `outbox_events` table through `OutboxEventRecorder`, in the same transaction that saves the aggregate. `OutboxEventRelay` runs on a fixed delay (`outbox.relay.fixed-delay-ms`), reads `PENDING` rows oldest first, and publishes each one to the `@EventListener` handlers in its own transaction, marking it `PUBLISHED`. A failure increments `attempts`, keeps the error, and retries on the next run until `outbox.relay.max-attempts`, after which the row is marked `FAILED`.

### Consequences

- An event is published if and only if its aggregate was saved (at-least-once delivery).
- Handlers can see the same event twice after a failure, so they must be idempotent.
- Policies run a couple of seconds after the command instead of immediately.
- One extra table, `outbox_events`, in the database model.
