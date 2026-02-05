# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew build          # Build the project
./gradlew test           # Run all tests
./gradlew bootRun        # Run the application (port 8080)
./gradlew asciidoctor    # Generate API documentation from REST Docs snippets
```

Run a single test:
```bash
./gradlew test --tests "TestClassName"
./gradlew test --tests "TestClassName.methodName"
```

## Technology Stack

- Java 21, Spring Boot 4.0.2, Gradle 9.3.0
- PostgreSQL with JPA/Hibernate
- Lombok for boilerplate reduction
- Spring REST Docs with MockMVC for API documentation

## Architecture

**Domain-Driven Layered Architecture** organized by business domain:

```
pro.grape_server/
├── domain/          # Business domains (grape, oauth)
│   └── <domain>/
│       ├── controller/     # REST endpoints + request/response DTOs
│       ├── service/        # Business logic + service DTOs + assemblers
│       └── repository/     # JPA repositories
├── model/entity/    # JPA entities with enums in model/entity/enums/
└── global/          # Cross-cutting concerns
```

**Key Patterns:**
- Entities use factory methods (`Grape.create()`, `Record.create()`) with validation logic
- Assemblers transform entities to service DTOs (e.g., `GrapeOverviewAssembler`)
- Response DTOs use `from()` factory methods to convert from service DTOs
- Request/Response DTOs are Java records

**Entity Relationships:**
- User → Grape (1:N) - users own grapes
- Grape → Record (1:N) - grapes have daily records
- Record has unique constraint on (grape_id, record_date) - one record per grape per day

## Conventions

- Controllers: `<Resource>Controller` mapped to `/api/<resources>`
- Services: `@Transactional` at class level, constructor injection via `@RequiredArgsConstructor`
- DTOs: `<Action><Entity>Request/Response` (e.g., `CreateGrapeRequest`, `GetGrapeResponse`)
- Entities: `@Builder` with private access, protected no-arg constructor for JPA
- Base entity `BaseEntity` provides `createdAt`/`updatedAt` audit fields
