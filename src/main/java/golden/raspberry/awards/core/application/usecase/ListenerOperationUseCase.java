package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.out.ListenerPort;

import java.util.Objects;

/**
 * Use Case for listening to REST API operations (GET, PUT, DELETE, POST).
 * Orchestrates listening through the ListenerPort.
 *
 * <p>This use case is part of the Application layer and orchestrates
 * the listening to REST API operations following hexagonal architecture principles.
 *
 * <p>Listens to:
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
public class ListenerOperationUseCase {

    private final ListenerPort listenerPort;

    /**
     * Constructor for dependency injection.
     *
     * @param listenerPort Listener port (output adapter)
     */
    public ListenerOperationUseCase(ListenerPort listenerPort) {
        this.listenerPort = Objects.requireNonNull(listenerPort, "ListenerPort cannot be null");
    }

    /**
     * Listens to a GET operation (read/retrieve).
     *
     * @param sessionId   Session identifier from HTTP request
     * @param httpMethod  HTTP method (GET)
     * @param endpoint    Endpoint path
     * @param statusCode  HTTP status code (e.g., 200, 404, 500)
     * @param entityType  Type of entity (e.g., "Movie", "ProducerInterval")
     * @param entityId    Entity identifier (if applicable)
     * @param responseData Response data (what was retrieved)
     * @param error       Error message (if status code indicates error, null otherwise)
     */
    public void listenGet(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                          String entityType, String entityId, Object responseData, String error) {
        listenerPort.listenGet(sessionId, httpMethod, endpoint, statusCode, entityType, entityId, responseData, error);
    }

    /**
     * Listens to a PUT operation (update).
     *
     * @param sessionId  Session identifier from HTTP request
     * @param httpMethod HTTP method (PUT)
     * @param endpoint   Endpoint path
     * @param statusCode HTTP status code (e.g., 200, 400, 500)
     * @param entityType Type of entity (e.g., "Movie")
     * @param entityId   Entity identifier
     * @param dataBefore Data before update
     * @param dataAfter  Data after update
     * @param error      Error message (if status code indicates error, null otherwise)
     */
    public void listenPut(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                          String entityType, String entityId, Object dataBefore, Object dataAfter, String error) {
        listenerPort.listenPut(sessionId, httpMethod, endpoint, statusCode, entityType, entityId, dataBefore, dataAfter, error);
    }

    /**
     * Listens to a DELETE operation.
     *
     * @param sessionId  Session identifier from HTTP request
     * @param httpMethod HTTP method (DELETE)
     * @param endpoint   Endpoint path
     * @param statusCode HTTP status code (e.g., 200, 404, 500)
     * @param entityType Type of entity (e.g., "Movie")
     * @param entityId   Entity identifier
     * @param dataBefore Data before deletion
     * @param error      Error message (if status code indicates error, null otherwise)
     */
    public void listenDelete(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                             String entityType, String entityId, Object dataBefore, String error) {
        listenerPort.listenDelete(sessionId, httpMethod, endpoint, statusCode, entityType, entityId, dataBefore, error);
    }

    /**
     * Listens to a POST operation (create).
     *
     * @param sessionId    Session identifier from HTTP request
     * @param httpMethod   HTTP method (POST)
     * @param endpoint     Endpoint path
     * @param statusCode   HTTP status code (e.g., 201, 400, 500)
     * @param entityType   Type of entity (e.g., "Movie")
     * @param entityId     Entity identifier
     * @param requestData  Request data (what was sent)
     * @param responseData Response data (what was created)
     * @param error        Error message (if status code indicates error, null otherwise)
     */
    public void listenPost(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                           String entityType, String entityId, Object requestData, Object responseData, String error) {
        listenerPort.listenPost(sessionId, httpMethod, endpoint, statusCode, entityType, entityId, requestData, responseData, error);
    }
}

