# MoniThor Component Diagram

```mermaid
graph TB
    subgraph "Client Layer"
        VueClient[Vue.js Client<br/>Frontend SPA]
    end

    subgraph "API Layer (Spring Boot)"
        JobCtrl[JobController<br/>Job CRUD & Query]
        CheckCtrl[CheckController<br/>Manual Check Trigger]
        TagCtrl[TagController<br/>Tag Management]
    end

    subgraph "Application Layer"
        JobRunner[JobRunner<br/>@Scheduled<br/>Fixed Delay: 30s]
    end

    subgraph "Business Logic Layer"
        Checker[Checker Interface]
        HttpChecker[HttpChecker<br/>HTTP Request Executor]
        DataExtractor[DataExtractor<br/>Regex Pattern Matcher]
    end

    subgraph "Data Access Layer"
        JobRepo[JobRepository<br/>JpaRepository]
        Job[Job Entity<br/>- url, interval<br/>- matchPattern<br/>- lastCheck, status]
    end

    subgraph "Infrastructure"
        H2[(H2 Database<br/>monithor.db)]
        HttpEndpoints[External HTTP<br/>Endpoints]
    end

    VueClient -->|REST API| JobCtrl
    VueClient -->|REST API| CheckCtrl
    VueClient -->|REST API| TagCtrl

    JobCtrl --> JobRepo
    CheckCtrl --> Checker
    TagCtrl --> JobRepo

    JobRunner -->|Every 30s| JobRepo
    JobRunner -->|Execute Due Jobs| Checker

    HttpChecker -.->|implements| Checker
    HttpChecker --> DataExtractor
    HttpChecker -->|HTTP GET| HttpEndpoints

    JobRepo --> Job
    Job -->|JPA| H2

    style VueClient fill:#42b983
    style JobRunner fill:#ff9800
    style H2 fill:#1976d2
    style HttpEndpoints fill:#607d8b
```

## Component Descriptions

### Client Layer
- **Vue.js Client**: Single-page application providing UI for job management and monitoring

### API Layer (Controllers)
- **JobController**: CRUD operations and queries for monitoring jobs
- **CheckController**: Triggers manual checks on demand
- **TagController**: Manages job tags and tag-based queries

### Application Layer
- **JobRunner**: Scheduled task that runs every 30 seconds (configurable), finds due jobs and executes checks in parallel using a thread pool

### Business Logic Layer
- **Checker Interface**: Contract for job checking implementations
- **HttpChecker**: Executes HTTP requests to monitored endpoints
- **DataExtractor**: Extracts version/build information from HTTP responses using regex patterns

### Data Access Layer
- **JobRepository**: Spring Data JPA repository with custom queries for jobs and tags
- **Job Entity**: Core domain model storing monitoring job configuration and last check results

### Infrastructure
- **H2 Database**: File-based database storing job configurations and check history
- **External HTTP Endpoints**: Third-party services being monitored

## Data Flow

1. **Scheduled Checks**: JobRunner → JobRepository → Checker → HttpChecker → External Endpoints
2. **Manual Checks**: CheckController → Checker → HttpChecker → External Endpoints
3. **Job Management**: Vue Client → JobController → JobRepository → H2 Database
4. **Result Updates**: HttpChecker → DataExtractor → Job Entity → H2 Database

## Key Patterns

- **Repository Pattern**: JobRepository abstracts data access
- **Strategy Pattern**: Checker interface allows multiple check implementations
- **Scheduled Tasks**: Spring @Scheduled annotation for periodic execution
- **REST API**: Standard HTTP endpoints for client-server communication
