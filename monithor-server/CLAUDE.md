# Monithor

## Purpose

Monithor is a basic HTTP health checking tool built as a learning project. It periodically makes HTTP requests to configured URLs, performs pattern matching to validate responses, and extracts version information. The application provides a web-based dashboard for monitoring the health status of multiple services.

**Key Features:**
- Scheduled HTTP health checks with configurable intervals
- Pattern matching for response validation
- Version and build timestamp extraction
- Tag-based job organization
- Web-based management interface
- Deployment version checking for SNAPSHOT builds
- Concurrent job execution with configurable thread pools

## Tech Stack

### Backend (monithor-server)
- **Java**: 21 (upgraded from 1.8)
- **Spring Boot**: 3.3.3 (upgraded from 1.4.1.RELEASE)
- **Spring Framework Components**:
  - Spring Boot Starter Web
  - Spring Boot Starter Data JPA
  - Spring Scheduling (`@EnableScheduling`)
  - Spring Async (`@EnableAsync`)
- **Database**: SQLite Database (embedded)
- **ORM**: Hibernate with Java 8 support
- **Validation**: Spring Boot Starter Validation (Jakarta EE)
- **HTTP Client**: Apache HttpComponents Client 5 (upgraded from 4.5.2)
- **JSON Processing**: Jackson Core (jackson-annotations, jackson-databind)
- **Utilities**: Lombok 1.18.38 (with explicit annotation processing configuration)
- **Build Tool**: Maven 3.x
- **Testing**:
  - Spring Boot Starter Test (includes JUnit 5, AssertJ, Mockito)
  - Maven Failsafe Plugin (managed by Spring Boot parent)

### Frontend (monithor-client)
- **JavaScript Framework**: Vue.js 2.1.0
- **HTTP Client**: vue-resource 1.0.3
- **Routing**: vue-router 2.1.1
- **Form Validation**: vee-validate 2.0.0-beta.18
- **Data Tables**: vuetable-2 1.0.1
- **Build Tool**: Webpack 1.13.2
- **Development Server**: Express 4.13.3
- **Testing**:
  - Karma 1.3.0
  - Mocha 3.1.0
  - Chai 3.5.0
  - Sinon 1.17.3
  - PhantomJS 2.1.3
- **Code Quality**: ESLint 3.7.1

### Infrastructure
- **CI/CD**: CircleCI (circle.yml configuration)
- **Deployment**: Executable JAR with embedded Tomcat
- **Cross-Platform**: Maven profiles for Windows and Unix environments

## Architecture

The application follows a typical Spring Boot architecture with:
- **JobRunner**: Scheduled service that executes health checks
- **HttpChecker**: Component that performs HTTP requests and validates responses
- **Job Entity**: JPA entity representing monitoring jobs
- **REST Controllers**: API endpoints for job management
- **SQLite Database**: Stores job configurations and results
- **Vue.js SPA**: Frontend for job management and monitoring dashboard

## Development Commands

**Server:**
```bash
mvn spring-boot:run
# Runs on port 8090
```

**Client:**
```bash
cd monithor-client
npm install
npm run dev
# Runs on port 8080 with API proxy to 8090
```

**Build & Package:**
```bash
# Client build
cd monithor-client && npm run build

# Server build
mvn clean package

# Run packaged application
java -jar monithor-server/target/monithor-server-1.0.2-SNAPSHOT.jar
```

**Testing:**
```bash
# Client tests
cd monithor-client && npm test

# Server tests
mvn test
mvn failsafe:integration-test
```