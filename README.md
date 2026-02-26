# Layered Monolith — Sample / Boilerplate

A reference boilerplate for a classic layered monolith built with Spring Boot.

The goal is to demonstrate strict layer separation, effortless infrastructure replacement, and a deliberate testing pyramid.

The domain is intentionally minimal — a single `User` entity with full CRUD. 

So the architectural patterns remain in focus rather than getting buried in business details.

---

## Architecture

```
presentation  ──►  application.api  ◄──  infrastructure.api
                        │
                     domain
```

Layer | Package | Responsibility
------|---------|---------------
**Domain** | `domain` | Core entities and domain exceptions. No outward dependencies whatsoever.
**Application** | `application.api` / `application.impl` | Use-case interfaces and their implementations. Depends only on `domain` and the ports defined in `infrastructure.api`.
**Infrastructure API** | `infrastructure.api` | Ports: `ReadRepository`, `WriteRepository`, `IdGenerator`. Placed here deliberately — to illustrate the DAO pattern and to make the migration path toward Hexagonal / Onion architecture self-evident: moving these interfaces into `domain` or `application.api` and renaming them *ports* is all it takes.
**Infrastructure Impl** | `infrastructure.impl.*` | Adapters: JDBC, jOOQ, JPA — switched via Spring profile. UUID v7 ID generator.
**Presentation** | `presentation.rest` / `presentation.mvc` | JSON REST API and HTML MVC controllers (JTE templates), both built on top of the same `application.api`.

### Design Decisions

**Package-private implementations.** `UserCommandService`, `UserQueryService`, and all infrastructure classes are declared without `public`. Only interfaces are visible outside their package. Spring wires them as beans through `@Configuration` classes that live in the same package. Package visibility is used as an architectural boundary — not an accidental omission, but a deliberate encapsulation tool.

**CQRS hint.** `CommandUseCase` and `QueryUseCase` are separate interfaces. Controllers inject only what they actually need. `UserQueryService` is annotated with `@Transactional(readOnly = true)` — a deliberate optimization that most developers overlook: read-only transactions reduce overhead at both the JDBC driver and ORM levels.

**Swappable infrastructure.** Three complete repository implementations (JDBC via the modern `JdbcClient`, jOOQ, JPA) are activated by profile with zero changes to the application layer. This is a direct, working demonstration of the Liskov Substitution Principle and Dependency Inversion — in practice, not just in theory.

**`JdbcClient` over `JdbcTemplate`.** The project uses the API introduced in Spring 6.1 — concise, fluent, and free of boilerplate. A deliberate choice of the modern tool over the one kept out of habit.

**JPA entities never leak out.** `UserJpaEntity` is a package-private class. A dedicated `UserJpaMapper` converts between the entity and the domain object in both directions. The boundary between the persistence model and the domain is drawn explicitly — not blurred by placing `@Entity` on a domain class, which is a common mistake notably absent here.

**Two UIs, one core.** REST controllers and MVC controllers (with HTML forms) share the same `CommandUseCase` / `QueryUseCase`. A concrete proof that the delivery mechanism has no bearing on business logic.

**Separate error handling.** Two independent `@ControllerAdvice` classes — `GlobalRestExceptionHandler` and `GlobalMvcExceptionHandler` — each scoped to its own package. REST returns [RFC 9457 Problem Details](https://www.rfc-editor.org/rfc/rfc9457); MVC renders a template with an error message. Not a single catch-all solution, but context-appropriate handling for each protocol.

**Path to Hexagonal / Onion.** Migrating to hexagonal architecture requires nothing more than moving the interfaces from `infrastructure.api` into `domain` or `application.api`. The adapters stay exactly where they are. The distance between a layered monolith and an onion architecture is a single refactoring step.

---

## Project Structure

```
src/main/java/.../
├── domain/                    # User record, UserNotFoundException
├── application/
│   ├── api/                   # CommandUseCase, QueryUseCase, commands, views
│   └── impl/                  # UserCommandService, UserQueryService, ApplicationConfig
├── infrastructure/
│   ├── api/                   # ReadRepository, WriteRepository, IdGenerator
│   └── impl/
│       ├── jdbc/              # JdbcReadRepository, JdbcWriteRepository
│       ├── jooq/              # JooqReadRepository, JooqWriteRepository
│       ├── jpa/               # JpaReadRepository, JpaWriteRepository,
│       │                      # UserJpaEntity, UserJpaMapper
│       └── id/                # UuidV7IdGenerator
└── presentation/
    ├── common/dto/            # DTOs, Mapper
    ├── rest/                  # RestQueryController, RestCommandController,
    │                          # GlobalRestExceptionHandler
    └── mvc/                   # MvcQueryController, MvcFormController,
                               # MvcCommandController, GlobalMvcExceptionHandler
```

---

## Running the Application

```bash
# Default — jdbc profile, H2 in-memory, port 8083
./mvnw spring-boot:run

# Switch to jOOQ
./mvnw spring-boot:run -Dspring-boot.run.profiles=jooq

# Switch to JPA
./mvnw spring-boot:run -Dspring-boot.run.profiles=jpa
```

The H2 console is available at `http://localhost:8083/h2`.

---

## API

### REST

Method | URL | Description
-------|-----|------------
`GET` | `/api/users` | List all users
`GET` | `/api/users?namePrefix=Al` | Filter by name prefix (case-insensitive)
`GET` | `/api/users/{id}` | Get user by ID
`POST` | `/api/users` | Create a user (`Location` header in response)
`PUT` | `/api/users/{id}` | Update a user
`DELETE` | `/api/users/{id}` | Delete a user

Errors are returned as [RFC 9457 Problem Details](https://www.rfc-editor.org/rfc/rfc9457).

### MVC (HTML)

Entry point: `http://localhost:8083/mvc/users`

> **Note.** The MVC interface exists to demonstrate dual-presentation — two UIs
> on top of a single application layer. In a real application, name search would
> be the only entry point: a UUID is an internal system identifier
> and is never exposed to the user.
---

## Tests

The testing pyramid is structured deliberately — each level has a distinct purpose:

```
            [E2E]           RestTestClient, random real HTTP port
          [Integration]     @SpringBootTest + MockMvc + H2 + @Sql
        [Slice]             @WebMvcTest — controllers in full isolation
      [Unit]                Services via Fake repositories, no Mockito
    [Arch]                  ArchUnit — dependency rules enforced on bytecode
```

**Fake repositories over Mockito** in service unit tests — not laziness in writing a mock, but a deliberate choice: a Fake provides real behavior, so the test verifies logic rather than asserting that a particular method was called.

**`@Sql` with separate files** `test-schema.sql` / `test-data.sql` — schema and fixtures are decoupled. Command integration tests use only the schema (`@Sql("/test-schema.sql")`); query integration tests also load data (`@Sql({"/test-schema.sql", "/test-data.sql"})`). Each test gets exactly the state it needs — nothing more.

**`RestTestClient` in E2E** — a dedicated client over a real HTTP port, not MockMvc. The distinction matters: MockMvc exercises the Spring MVC dispatch pipeline; `RestTestClient` goes through the full network stack.

**ArchUnit rules** — dependency constraints are verified automatically on every build:

- Presentation does not depend on Infrastructure
- Application does not depend on Infrastructure Impl
- Application does not depend on Presentation
- Domain does not depend on anything
- Controllers do not access `application.impl` directly

```bash
./mvnw test
```

---

## Dependencies

- Java 21, Spring Boot 3.x
- H2 (in-memory, development and tests only)
- Spring JdbcClient (6.1+), jOOQ, Spring Data JPA — three interchangeable adapters
- JTE — template engine for MVC
- `com.fasterxml.uuid` — UUID v7 generation
- ArchUnit — architectural tests
