package golden.raspberry.awards.core.application.port.out;

/**
 * Output Port for listener operations.
 * Defined by Application layer, implemented by Output Adapter.
 *
 * <p>This port defines the contract for listening to REST API operations (GET, PUT, DELETE, POST)
 * to files in the listener directory with detailed information including:
 * - HTTP method and endpoint
 * - Status codes (200 OK, 400 Bad Request, 500 Internal Server Error, etc.)
 * - Request and response data
 * - Errors (if any)
 * - Before/after data for PUT and DELETE operations
 *
 * <p>Uses Java 21 features: clean interfaces, Records for data transfer.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface ListenerPort {
    
    /**
     * Listens to a GET operation (read/retrieve).
     *
     * @param sessionId Session identifier
     * @param httpMethod HTTP method (GET)
     * @param endpoint Endpoint path
     * @param statusCode HTTP status code (e.g., 200, 404, 500)
     * @param entityType Type of entity (e.g., "Movie", "ProducerInterval")
     * @param entityId Entity identifier (if applicable)
     * @param responseData Response data (what was retrieved)
     * @param error Error message (if status code indicates error, null otherwise)
     */
    void listenGet(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                String entityType, String entityId, Object responseData, String error);
    
    /**
     * Listens to a PUT operation (update).
     *
     * @param sessionId Session identifier
     * @param httpMethod HTTP method (PUT)
     * @param endpoint Endpoint path
     * @param statusCode HTTP status code (e.g., 200, 400, 500)
     * @param entityType Type of entity (e.g., "Movie")
     * @param entityId Entity identifier
     * @param dataBefore Data before update (serialized to JSON)
     * @param dataAfter Data after update (serialized to JSON)
     * @param error Error message (if status code indicates error, null otherwise)
     */
    void listenPut(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                String entityType, String entityId, Object dataBefore, Object dataAfter, String error);
    
    /**
     * Listens to a DELETE operation.
     *
     * @param sessionId Session identifier
     * @param httpMethod HTTP method (DELETE)
     * @param endpoint Endpoint path
     * @param statusCode HTTP status code (e.g., 200, 404, 500)
     * @param entityType Type of entity (e.g., "Movie")
     * @param entityId Entity identifier
     * @param dataBefore Data before deletion (serialized to JSON)
     * @param error Error message (if status code indicates error, null otherwise)
     */
    void listenDelete(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                   String entityType, String entityId, Object dataBefore, String error);
    
    /**
     * Listens to a POST operation (create).
     *
     * @param sessionId Session identifier
     * @param httpMethod HTTP method (POST)
     * @param endpoint Endpoint path
     * @param statusCode HTTP status code (e.g., 201, 400, 500)
     * @param entityType Type of entity (e.g., "Movie")
     * @param entityId Entity identifier
     * @param requestData Request data (what was sent)
     * @param responseData Response data (what was created)
     * @param error Error message (if status code indicates error, null otherwise)
     */
    void listenPost(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                 String entityType, String entityId, Object requestData, Object responseData, String error);
}

