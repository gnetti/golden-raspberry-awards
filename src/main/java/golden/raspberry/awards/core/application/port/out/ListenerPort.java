package golden.raspberry.awards.core.application.port.out;

/**
 * Output Port for listener operations.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface ListenerPort {
    
    /**
     * Listens to a GET operation.
     *
     * @param sessionId Session identifier
     * @param httpMethod HTTP method
     * @param endpoint Endpoint path
     * @param statusCode HTTP status code
     * @param entityType Type of entity
     * @param entityId Entity identifier
     * @param responseData Response data
     * @param error Error message
     */
    void listenGet(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                String entityType, String entityId, Object responseData, String error);
    
    /**
     * Listens to a PUT operation.
     *
     * @param sessionId Session identifier
     * @param httpMethod HTTP method
     * @param endpoint Endpoint path
     * @param statusCode HTTP status code
     * @param entityType Type of entity
     * @param entityId Entity identifier
     * @param dataBefore Data before update
     * @param dataAfter Data after update
     * @param error Error message
     */
    void listenPut(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                String entityType, String entityId, Object dataBefore, Object dataAfter, String error);
    
    /**
     * Listens to a DELETE operation.
     *
     * @param sessionId Session identifier
     * @param httpMethod HTTP method
     * @param endpoint Endpoint path
     * @param statusCode HTTP status code
     * @param entityType Type of entity
     * @param entityId Entity identifier
     * @param dataBefore Data before deletion
     * @param error Error message
     */
    void listenDelete(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                   String entityType, String entityId, Object dataBefore, String error);
    
    /**
     * Listens to a POST operation.
     *
     * @param sessionId Session identifier
     * @param httpMethod HTTP method
     * @param endpoint Endpoint path
     * @param statusCode HTTP status code
     * @param entityType Type of entity
     * @param entityId Entity identifier
     * @param requestData Request data
     * @param responseData Response data
     * @param error Error message
     */
    void listenPost(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                 String entityType, String entityId, Object requestData, Object responseData, String error);
}

