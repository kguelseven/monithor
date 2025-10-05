# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

MoniThor is a Spring Boot monitoring application with a Vue.js client that periodically checks HTTP endpoints and tracks their status. The backend is built with Java 24 and Spring Boot 3.5.6, using JPA for persistence with H2 database.

### Key Dependencies
- Spring Boot: 3.5.6
- Apache HttpClient5: 5.5.1
- Lombok: 1.18.42
- H2 Database: 2.3.232
- JUnit Jupiter: 5.10.5
- AssertJ: 3.27.3

## Build Commands

**Build the full application (including frontend):**
```bash
mvn clean package
```

**Build Java backend only:**
```bash
mvn compile
```

**Run tests:**
```bash
mvn test
```

**Run integration tests:**
```bash
mvn verify
```

**Run the application:**
```bash
java -jar target/monithor-server-1.0.2-SNAPSHOT.jar
```

The application runs on port 8090 by default.

## Architecture

### Backend Structure

- **Main Application:** `org.korhan.monithor.Monithor` - Spring Boot entry point with async execution and CORS configuration
- **Scheduled Job Runner:** `org.korhan.monithor.runner.JobRunner` - Runs on fixed delay (configurable via `runner.delay` property, default 30s), executes all due jobs in parallel using a thread pool
- **Check System:**
  - `Checker` interface defines job checking contract
  - `HttpChecker` implementation performs HTTP requests and extracts data
  - `DataExtractor` extracts version/build information from responses using regex patterns
- **Controllers:** REST endpoints in `service` package (`JobController`, `CheckController`, `TagController`)
- **Data Model:**
  - `Job` entity stores monitoring jobs with URL, interval, match patterns, and last check results
  - Jobs are marked as "due" when current time exceeds `lastTimestamp + intervalSecs`
  - Disabled jobs are skipped by the runner
  - Uses Lombok for getters/setters/constructors

### Frontend Build Integration

The Maven build automatically builds the Vue.js client via `exec-maven-plugin`:
1. Runs `npm install` in `../monithor-client/`
2. Runs `npm run build`
3. Copies the built files to `target/classes/static` via `maven-resources-plugin`

Frontend-only development uses webpack dev server via `npm run dev` in the client directory.

## Database

- H2 file-based database at `./monithor.db`
- Schema auto-updated via `spring.jpa.hibernate.ddl-auto=update`
- Connection timeout: 30000ms (30 seconds)

## Key Configuration

Properties in `src/main/resources/application.properties`:
- `server.port=8090` - Server port
- `runner.delay=30000` - Job runner fixed delay in milliseconds
- `spring.datasource.url=jdbc:h2:./monithor;DB_CLOSE_ON_EXIT=FALSE` - H2 database location

## Testing

Tests use JUnit Jupiter 5, AssertJ, and Spring Boot Test. Run individual test classes:
```bash
mvn test -Dtest=HttpCheckerTest
```
