# GitHub Repository Searcher

Spring Boot REST API that searches GitHub repositories, stores the results in PostgreSQL, and provides filtered/paginated retrieval.

## Tech stack
- Java 21
- Spring Boot 3.5.7
- Spring MVC `RestClient`
- Spring Data JPA / Hibernate
- PostgreSQL
- JUnit 5 / Mockito
- Springdoc OpenAPI

## Prerequisites
- Java 21
- PostgreSQL
- Maven (or use the Maven wrapper)

## Database setup

Create a PostgreSQL database:

```sql
CREATE DATABASE github_searcher;
```

Configure credentials with environment variables, or update `application.yml` for local development:

- `DB_URL` (default `jdbc:postgresql://localhost:5432/github_searcher`)
- `DB_USERNAME` (default `postgres`)
- `DB_PASSWORD` (default `postgres`)
- `GITHUB_TOKEN` (optional; recommended to increase GitHub API rate limits)

Hibernate creates/updates tables using `ddl-auto: update`. Flyway is not used.

## Run

```bash
./mvnw clean test
./mvnw spring-boot:run
```

Windows:

```powershell
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

## API

### Search GitHub

`POST /api/github/search`

```json
{
  "query": "spring boot",
  "language": "Java",
  "sort": "stars",
  "page": 1,
  "size": 20
}
```

Supported `sort`: `stars`, `forks`, `updated`.

### Retrieve stored repositories

`GET /api/github/repositories?language=Java&minStars=100&sort=stars&page=0&size=20`

- `language`: optional exact language match (case-insensitive)
- `minStars`: optional minimum star count
- `sort`: `stars` (default), `forks`, `updated`
- `page`: zero-based page number (default `0`)
- `size`: page size from 1 to 100 (default `20`)

### Retrieve one repository

`GET /api/github/repositories/{githubId}`

### Health

`GET /actuator/health`

### Swagger UI

`/swagger-ui.html`
