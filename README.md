# Golden Raspberry Awards API

RESTful API developed for reading the list of nominees and winners of the Worst Movie category of the Golden Raspberry Awards.

## About the Project

This Spring Boot application processes movie data from a CSV file and provides REST endpoints for querying information about producers and their intervals between consecutive awards.

## Technologies

- Java 21
- Spring Boot 3.4.11
- Spring Data JPA
- H2 Database (embedded)
- Maven
- JUnit 5

## Prerequisites

- JDK 21 or higher
- Maven 3.6+ (or use the project wrapper)

## How to Run

### Run the Application

```bash
mvn clean spring-boot:run
```

The application will start on port 8080. Access: `http://localhost:8080`

### Run Tests

```bash
mvn test
```

To run only integration tests:

```bash
mvn test -Dtest=*IntegrationTest
```

## Data Loading

On startup, the application automatically:
1. Reads the CSV file located at `src/main/resources/data/movieList.csv`
2. Inserts data into the H2 database
3. Preserves original IDs from CSV

## API Endpoints

### Movie CRUD Operations

#### Create Movie

**POST** `/api/movies`

Creates a new movie.

**Status Codes:**
- `201 Created` - Movie created successfully
- `400 Bad Request` - Validation error

#### Get All Movies

**GET** `/api/movies`

Returns a paginated list of movies.

**Query Parameters:**
- `page` (default: 0) - Page number (0-based)
- `size` (default: 10) - Page size
- `sort` (default: "id") - Sort field
- `direction` (default: "asc") - Sort direction (asc/desc)

**Status Codes:**
- `200 OK` - Movies retrieved successfully
- `400 Bad Request` - Invalid pagination parameters

#### Get Movie by ID

**GET** `/api/movies/{id}`

Returns a movie by its ID.

**Status Codes:**
- `200 OK` - Movie found
- `404 Not Found` - Movie not found

#### Update Movie

**PUT** `/api/movies/{id}`

Updates an existing movie.

**Status Codes:**
- `200 OK` - Movie updated successfully
- `400 Bad Request` - Validation error
- `404 Not Found` - Movie not found

#### Delete Movie

**DELETE** `/api/movies/{id}`

Deletes a movie by its ID.

**Status Codes:**
- `204 No Content` - Movie deleted successfully
- `404 Not Found` - Movie not found

### Producer Intervals

#### Get Producer Intervals

**GET** `/api/movies/producers/intervals`

Returns producers with the greatest and smallest interval between two consecutive awards.

**Response:**

```json
{
  "min": [
    {
      "producer": "Joel Silver",
      "interval": 1,
      "previousWin": 1990,
      "followingWin": 1991
    }
  ],
  "max": [
    {
      "producer": "Matthew Vaughn",
      "interval": 13,
      "previousWin": 2002,
      "followingWin": 2015
    }
  ]
}
```

**Status Codes:**
- `200 OK` - Intervals retrieved successfully
- `500 Internal Server Error` - Internal server error

## Richardson Maturity Level 2

The API implements Richardson Maturity Level 2, using:

- **HTTP Verbs:** GET, POST, PUT, DELETE for CRUD operations
- **HTTP Status Codes:** 200, 201, 204, 400, 404, 500
- **Resources identified by URIs:**
  - `/api/movies` - Collection resource (GET all, POST create)
  - `/api/movies/{id}` - Item resource (GET, PUT, DELETE)
  - `/api/movies/producers/intervals` - Sub-resource (GET intervals)
- **Structured error messages:** JSON with timestamp, status, error, message, and path

## Database

The application uses embedded H2 Database:

- **Development:** File-based database (`~/test`)
- **Tests:** In-memory database (`jdbc:h2:mem:testdb`)
- **H2 Console:** Available at `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:~/test`
  - Username: `sa`
  - Password: (empty)

## Integration Tests

Integration tests validate:

- Response JSON structure (presence of min/max)
- Interval object structure (producer, interval, previousWin, followingWin)
- Correct data types
- Valid values (non-negative intervals)
- Correct Content-Type (application/json)
- Appropriate HTTP status codes

Test files:
- `AwardsControllerIntegrationTest.java`
- `ProducerWebControllerIntegrationTest.java`

## Architecture

This project follows **Hexagonal Architecture** (also known as Ports & Adapters), which separates the business logic from external dependencies:

- **Domain Layer** (`core/domain`) - Pure business logic and domain models
- **Application Layer** (`core/application`) - Use cases and application services
- **Infrastructure Layer** (`infrastructure`) - Adapters for external systems (database, CSV, XML)
- **Adapter Layer** (`adapter`) - REST controllers and web interfaces

This architecture ensures:
- **Independence** - Business logic is independent of frameworks and external systems
- **Testability** - Easy to test business logic in isolation
- **Flexibility** - Easy to swap implementations (e.g., change database or add new adapters)
- **Maintainability** - Clear separation of concerns

## Project Structure

```
src/
├── 📁 main/
│   ├── 📁 java/
│   │   └── 📁 golden/raspberry/awards/
│   │       ├── 📁 adapter/                 # Driving Adapters
│   │       │   └── 📁 driving/rest/       # REST Controllers
│   │       ├── 📁 core/                   # Core Business Logic
│   │       │   ├── 📁 application/        # Use Cases and Ports
│   │       │   └── 📁 domain/             # Domain Models
│   │       │
│   │       ├── 📁 infrastructure/         # Driven Adapters (JPA, CSV, XML)
│   │       └── 📁 shared/                 # Shared Components
│   │           └── 📁 exception/          # Exception Classes
│   └── 📁 resources/
│       ├── 📁 data/movieList.csv          # CSV Data
│       ├── 📁 templates/                  # Thymeleaf Templates
│       │   ├── 📁 fragments/              # Reusable fragments
│       │   ├── 📁 layout/                 # Base layouts
│       │   └── 📁 pages/                 # Page templates
│       ├── 📁 static/                     # Static assets (CSS, JS, images)
│       └── ⚙️ application.properties       # Configuration
└── 📁 test/
    └── 📁 java/
        └── 📁 golden/raspberry/awards/
            └── 📁 adapter/driving/          # Integration Tests
```

## Configuration

Main configurations are in `src/main/resources/application.properties`:

- Server port: 8080
- H2 database
- JPA/Hibernate settings
- CSV file path

## Error Handling

The API returns structured errors in the format:

```json
{
  "timestamp": "2025-01-10T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error message",
  "path": "/api/movies"
}
```

## 📮 API Testing with Postman

The project includes Postman collection and environment files for easy API testing.

### Setup

1. Start the application: `mvn spring-boot:run`
2. Open Postman
3. Import Collection: File → Import → [Download Collection](https://raw.githubusercontent.com/gnetti/golden-raspberry-awards/master/data/golden-raspberry-awards.postman_collection.json) - Complete API request collection
4. Import Environment: File → Import → [Download Environment](https://raw.githubusercontent.com/gnetti/golden-raspberry-awards/master/data/golden-raspberry-awards.postman_environment.json) - Environment variables configuration
5. Select the imported environment from the environment dropdown
6. Run requests from the collection

### Available Requests

The collection includes all REST endpoints:
- Movie CRUD operations (Create, Read, Update, Delete)
- Producer intervals calculation
- Pagination and filtering examples

## Documentation

The project includes comprehensive JavaDoc documentation. To generate and view:

```bash
# Generate JavaDoc
mvn javadoc:javadoc

# Copy to docs directory (for web access)
# Windows: xcopy /E /I target\site\apidocs docs
# Linux/Mac: cp -r target/site/apidocs/* docs/
```

Access JavaDoc:
- **GitHub**: [View Documentation](https://github.com/gnetti/golden-raspberry-awards/tree/master/docs)
- **Local**: `http://localhost:8080/docs`

## Web Interface

The application includes a web interface accessible at `http://localhost:8080` after starting the application.

![Dashboard](https://raw.githubusercontent.com/gnetti/golden-raspberry-awards/master/data/dashboard.png)

**Dashboard Components:**
- **About This API** - API information (version, base URL, architecture, maturity level)
- **Producer Intervals** - View producer award intervals
- **Movies** - Manage movie collection (CRUD operations)
- **Swagger UI** - Interactive API documentation
- **JavaDoc** - API reference documentation
- **OpenAPI Spec** - OpenAPI 3.0 JSON specification
- **H2 Console** - Database management console
- **Movies API** - Raw JSON API response viewer
- **System Manual** - Complete system guide

## Notes

- CSV file is processed automatically on startup
- Data is persisted in H2 database
- Application follows Hexagonal Architecture (Ports & Adapters)
- Complete implementation of Richardson Maturity Level 2
- Comprehensive JavaDoc documentation available

## Author

Luiz Generoso
