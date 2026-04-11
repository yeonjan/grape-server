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
- Spring Security + JWT (jjwt 0.12.6) for authentication
- Spring REST Docs with MockMVC for API documentation

## Architecture

**Domain-Driven Layered Architecture** organized by business domain:

```
pro.grape_server/
├── domain/
│   ├── auth/
│   │   ├── controller/          # AuthController (/api/auth)
│   │   │   └── dto/request|response/
│   │   ├── repository/          # UserRepository, RefreshTokenRepository
│   │   └── service/
│   │       ├── AuthService.java
│   │       ├── JwtService.java
│   │       └── provider/        # OAuthProvider interface + implementations
│   └── grape/
│       ├── controller/          # GrapeController (/api/grapes), UserController (/api/users)
│       │   └── dto/request|response/
│       ├── repository/          # GrapeRepository, RecordRepository
│       └── service/
│           ├── GrapeService.java
│           ├── RecordService.java
│           └── dto/             # GrapeOverviewResult
├── model/entity/
│   ├── User.java, Grape.java, Record.java, RefreshToken.java
│   ├── common/BaseEntity.java   # createdAt/updatedAt audit fields
│   └── enums/                   # Provider, GrapeStatus
└── global/
    ├── config/SecurityConfig.java
    ├── exception/               # BusinessException, ErrorCode, GlobalExceptionHandler
    └── security/                # JwtAuthenticationFilter, CustomUserDetails
```

**Key Patterns:**
- Entities use factory methods (`Grape.create()`, `User.create()`, `User.createGuest()`) with validation logic
- Service DTOs (e.g., `GrapeOverviewResult`) use `from()` factory methods built directly in the DTO class
- Response DTOs use `from()` factory methods to convert from service DTOs or entities
- Request/Response DTOs are Java records

**Entity Relationships:**
- User → Grape (1:N) - users own grapes
- Grape → Record (1:N) - grapes have daily records
- User → RefreshToken (1:N) - tokens deleted and reissued on every login/refresh
- Record has unique constraint on (grape_id, record_date) - one record per grape per day
- GrapeStatus: `IN_PROGRESS` | `COMPLETED` — only one IN_PROGRESS grape per user allowed

## Auth Flow

- **Guest**: device ID-based signup/login (`POST /api/auth/register/guest`, `POST /api/auth/login/guest`)
- **Social**: provider token-based login (`POST /api/auth/login/{provider}`)
  - Supported providers: `kakao` (implemented), `apple` (implemented)
  - Stub providers: `google`, `naver` (return NOT_IMPLEMENTED error)
- **Token refresh**: `POST /api/auth/refresh`
- **Guest → Social migration**: if a guest user logs in with a social account, their grapes are migrated to the social account and the guest account is deleted
- Apple OAuth: validates identity token via Apple's JWKS endpoint (RS256), checks `iss` and `aud` (bundle ID)

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/api/auth/register/guest` | Guest signup |
| POST | `/api/auth/login/guest` | Guest login |
| POST | `/api/auth/login/{provider}` | Social login (kakao, apple) |
| POST | `/api/auth/refresh` | Token refresh |
| POST | `/api/grapes` | Create grape |
| GET | `/api/grapes/{grapeId}/overview` | Get grape overview |
| GET | `/api/grapes/overview` | Get in-progress grape overview |
| PATCH | `/api/grapes/{grapeId}` | Update grape |
| GET | `/api/grapes/exists` | Check if user has any grape |
| POST | `/api/grapes/record` | Create record |
| GET | `/api/grapes/record?recordId=` | Get record |
| PATCH | `/api/grapes/record/{recordId}` | Update record |
| DELETE | `/api/grapes/record/{recordId}` | Delete record |
| GET | `/api/users/me/nickname` | Get user nickname |

## Conventions

- Controllers: `<Resource>Controller` mapped to `/api/<resources>`
- Services: `@Transactional` at class level, constructor injection via `@RequiredArgsConstructor`
- DTOs: `<Action><Entity>Request/Response` (e.g., `CreateGrapeRequest`, `GetGrapeResponse`)
- Entities: `@Builder` with private access, protected no-arg constructor for JPA
- Base entity `BaseEntity` provides `createdAt`/`updatedAt` audit fields
- Exceptions: throw `BusinessException(ErrorCode.XXX)` for all domain errors; `GlobalExceptionHandler` converts to `ErrorResponse`