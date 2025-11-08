package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.out.LoggingPort;

import java.util.Objects;
import java.util.UUID;

/**
 * Use Case for logging REST API operations (GET, PUT, DELETE, POST).
 * Orchestrates logging through the LoggingPort.
 *
 * <p>This use case is part of the Application layer and orchestrates
 * the logging of REST API operations following hexagonal architecture principles.
 *
 * <p>Logs include:
 * - HTTP method and endpoint
 * - Status codes (200 OK, 400 Bad Request, 500 Internal Server Error, etc.)
 * - Request and response data
 * - Errors (if any)
 * - Before/after data for PUT and DELETE operations
 *
 * <p>Uses Java 21 features: var, String Templates, Objects.requireNonNull.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public class LogOperationUseCase {

    private final LoggingPort loggingPort;
    private final String sessionId;

    /**
     * Constructor for dependency injection.
     * Generates a unique sessionId for this application instance.
     *
     * @param loggingPort Logging port (output adapter)
     */
    public LogOperationUseCase(LoggingPort loggingPort) {
        this.loggingPort = Objects.requireNonNull(loggingPort, "LoggingPort cannot be null");
        this.sessionId = UUID.randomUUID().toString();
    }

    /**
     * Gets the current session ID.
     *
     * @return Session identifier
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Logs a GET operation (read/retrieve).
     *
     * @param httpMethod  HTTP method (GET)
     * @param endpoint    Endpoint path
     * @param statusCode  HTTP status code (e.g., 200, 404, 500)
     * @param entityType  Type of entity (e.g., "Movie", "ProducerInterval")
     * @param entityId    Entity identifier (if applicable)
     * @param responseData Response data (what was retrieved)
     * @param error       Error message (if status code indicates error, null otherwise)
     */
    public void logGet(String httpMethod, String endpoint, Integer statusCode,
                       String entityType, String entityId, Object responseData, String error) {
        loggingPort.logGet(sessionId, httpMethod, endpoint, statusCode, entityType, entityId, responseData, error);
    }

    /**
     * Logs a PUT operation (update).
     *
     * @param httpMethod  HTTP method (PUT)
     * @param endpoint     Endpoint path
     * @param statusCode   HTTP status code (e.g., 200, 400, 500)
     * @param entityType   Type of entity (e.g., "Movie")
     * @param entityId     Entity identifier
     * @param dataBefore   Data before update
     * @param dataAfter    Data after update
     * @param error        Error message (if status code indicates error, null otherwise)
     */
    public void logPut(String httpMethod, String endpoint, Integer statusCode,
                       String entityType, String entityId, Object dataBefore, Object dataAfter, String error) {
        loggingPort.logPut(sessionId, httpMethod, endpoint, statusCode, entityType, entityId, dataBefore, dataAfter, error);
    }

    /**
     * Logs a DELETE operation.
     *
     * @param httpMethod  HTTP method (DELETE)
     * @param endpoint     Endpoint path
     * @param statusCode   HTTP status code (e.g., 200, 404, 500)
     * @param entityType   Type of entity (e.g., "Movie")
     * @param entityId     Entity identifier
     * @param dataBefore   Data before deletion
     * @param error        Error message (if status code indicates error, null otherwise)
     */
    public void logDelete(String httpMethod, String endpoint, Integer statusCode,
                          String entityType, String entityId, Object dataBefore, String error) {
        loggingPort.logDelete(sessionId, httpMethod, endpoint, statusCode, entityType, entityId, dataBefore, error);
    }

    /**
     * Logs a POST operation (create).
     *
     * @param httpMethod   HTTP method (POST)
     * @param endpoint     Endpoint path
     * @param statusCode   HTTP status code (e.g., 201, 400, 500)
     * @param entityType   Type of entity (e.g., "Movie")
     * @param entityId     Entity identifier
     * @param requestData  Request data (what was sent)
     * @param responseData Response data (what was created)
     * @param error        Error message (if status code indicates error, null otherwise)
     */
    public void logPost(String httpMethod, String endpoint, Integer statusCode,
                        String entityType, String entityId, Object requestData, Object responseData, String error) {
        loggingPort.logPost(sessionId, httpMethod, endpoint, statusCode, entityType, entityId, requestData, responseData, error);
    }
}
