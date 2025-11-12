/**
 * Golden Raspberry Awards API - Main Package
 * 
 * <p>This package contains the main Spring Boot application class and serves as the root
 * package for the Golden Raspberry Awards API project.</p>
 * 
 * <h2>Overview</h2>
 * <p>The Golden Raspberry Awards API is a RESTful web application that processes movie data
 * from CSV files and provides endpoints for querying information about producers and their
 * intervals between consecutive awards. The application follows Hexagonal Architecture
 * (Ports and Adapters) pattern and implements Richardson Maturity Level 2.</p>
 * 
 * <h2>Architecture</h2>
 * <p>The application is organized into the following layers:</p>
 * <ul>
 *   <li><b>Domain Layer</b> - Pure business logic and domain models</li>
 *   <li><b>Application Layer</b> - Use cases and application services</li>
 *   <li><b>Infrastructure Layer</b> - Adapters for external systems (database, CSV, XML)</li>
 *   <li><b>Adapter Layer</b> - REST controllers and DTOs</li>
 * </ul>
 * 
 * <h2>Key Features</h2>
 * <ul>
 *   <li>Automatic CSV data loading on application startup</li>
 *   <li>RESTful API with CRUD operations for movies</li>
 *   <li>Producer interval calculation (min/max intervals between awards)</li>
 *   <li>H2 embedded database for data persistence</li>
 *   <li>XML-based ID management for movie creation</li>
 *   <li>Comprehensive integration test coverage</li>
 * </ul>
 * 
 * <h2>Technologies</h2>
 * <ul>
 *   <li>Java 21</li>
 *   <li>Spring Boot 3.4.11</li>
 *   <li>Spring Data JPA</li>
 *   <li>H2 Database</li>
 *   <li>Maven</li>
 *   <li>JUnit 5</li>
 * </ul>
 * 
 * <h2>Getting Started</h2>
 * <p>To run the application:</p>
 * <pre>{@code
 * mvn clean spring-boot:run
 * }</pre>
 * <p>The application will start on port 8080. Access the API at:</p>
 * <ul>
 *   <li>API Base URL: http://localhost:8080/api/movies</li>
 *   <li>Swagger UI: http://localhost:8080/swagger-ui.html</li>
 *   <li>JavaDoc: http://localhost:8080/docs</li>
 *   <li>H2 Console: http://localhost:8080/h2-console</li>
 * </ul>
 * 
 * <h2>Package Structure</h2>
 * <ul>
 *   <li>adapter.driving.rest - REST Controllers and DTOs</li>
 *   <li>core.application - Use Cases and Ports</li>
 *   <li>core.domain - Domain Models</li>
 *   <li>infrastructure - Infrastructure Adapters</li>
 * </ul>
 * 
 * @author Luiz Generoso
 * @version 1.0.0
 * @since 1.0.0
 */
package golden.raspberry.awards;

