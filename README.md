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
2. Inserts data into H2 database
3. Preserves original IDs from CSV

## API Endpoints

### Get Producer Intervals

**GET** `/api/movies/producers/intervals`

Returns producers with the greatest and smallest interval between two consecutive awards.

**Response:**

```json
{
  "min": [
    {
      "producer": "Producer 1",
      "interval": 1,
      "previousWin": 2008,
      "followingWin": 2009
    }
  ],
  "max": [
    {
      "producer": "Producer 2",
      "interval": 99,
      "previousWin": 1900,
      "followingWin": 1999
    }
  ]
}
```

**Status:** 200 OK

### Additional Endpoints (Movie CRUD)

- **POST** `/api/movies` - Create movie (201 Created)
- **GET** `/api/movies/{id}` - Get movie by ID (200 OK / 404 Not Found)
- **PUT** `/api/movies/{id}` - Update movie (200 OK / 404 Not Found)
- **DELETE** `/api/movies/{id}` - Delete movie (204 No Content / 404 Not Found)

## Richardson Maturity Level 2

The API implements Richardson Maturity Level 2, using:

- **HTTP Verbs:** GET, POST, PUT, DELETE for CRUD operations
- **HTTP Status Codes:** 200, 201, 204, 400, 404, 500
- **Resources identified by URIs:** `/api/movies`, `/api/movies/{id}`, `/api/movies/producers/intervals`
- **Structured error messages:** JSON with timestamp, status, error, message and path

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

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── golden/raspberry/awards/
│   │       ├── adapter/driving/rest/    # REST Controllers
│   │       ├── core/application/       # Use Cases and Ports
│   │       ├── core/domain/            # Domain Models
│   │       └── infrastructure/          # Adapters (JPA, CSV)
│   └── resources/
│       ├── data/movieList.csv          # CSV Data
│       └── application.properties       # Configuration
└── test/
    └── java/
        └── golden/raspberry/awards/
            └── adapter/driving/          # Integration Tests
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

## Notes

- CSV file is processed automatically on startup
- Data is persisted in H2 database
- Application follows Hexagonal Architecture (Ports & Adapters)
- Complete implementation of Richardson Maturity Level 2

## Author

Luiz Generoso
