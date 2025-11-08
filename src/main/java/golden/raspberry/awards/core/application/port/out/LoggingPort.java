package golden.raspberry.awards.core.application.port.out;

/**
 * Output Port for logging operations.
 * Defined by Application layer, implemented by Output Adapter.
 *
 * <p>This port defines the contract for logging CREATE, UPDATE, DELETE operations
 * to files in the resources/log directory.
 *
 * <p>Uses Java 21 features: clean interfaces, Records for data transfer.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface LoggingPort {
    
    /**
     * Logs a CREATE operation.
     *
     * @param sessionId Session identifier
     * @param entityType Type of entity (e.g., "Movie")
     * @param entityId Entity identifier
     * @param dataAfter Data after creation (serialized to JSON)
     */
    void logCreate(String sessionId, String entityType, String entityId, Object dataAfter);
    
    /**
     * Logs an UPDATE operation.
     *
     * @param sessionId Session identifier
     * @param entityType Type of entity (e.g., "Movie")
     * @param entityId Entity identifier
     * @param dataBefore Data before update (serialized to JSON)
     * @param dataAfter Data after update (serialized to JSON)
     */
    void logUpdate(String sessionId, String entityType, String entityId, Object dataBefore, Object dataAfter);
    
    /**
     * Logs a DELETE operation.
     *
     * @param sessionId Session identifier
     * @param entityType Type of entity (e.g., "Movie")
     * @param entityId Entity identifier
     * @param dataBefore Data before deletion (serialized to JSON)
     */
    void logDelete(String sessionId, String entityType, String entityId, Object dataBefore);
}

