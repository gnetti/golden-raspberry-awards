package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.out.LoggingPort;

import java.util.Objects;
import java.util.UUID;

/**
 * Use Case for logging operations (CREATE, UPDATE, DELETE).
 * Orchestrates logging through the LoggingPort.
 *
 * <p>This use case is part of the Application layer and orchestrates
 * the logging of business operations following hexagonal architecture principles.
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
     * Logs a CREATE operation.
     *
     * @param entityType Type of entity (e.g., "Movie")
     * @param entityId Entity identifier
     * @param dataAfter Data after creation
     */
    public void logCreate(String entityType, String entityId, Object dataAfter) {
        loggingPort.logCreate(sessionId, entityType, entityId, dataAfter);
    }
    
    /**
     * Logs an UPDATE operation.
     *
     * @param entityType Type of entity (e.g., "Movie")
     * @param entityId Entity identifier
     * @param dataBefore Data before update
     * @param dataAfter Data after update
     */
    public void logUpdate(String entityType, String entityId, Object dataBefore, Object dataAfter) {
        loggingPort.logUpdate(sessionId, entityType, entityId, dataBefore, dataAfter);
    }
    
    /**
     * Logs a DELETE operation.
     *
     * @param entityType Type of entity (e.g., "Movie")
     * @param entityId Entity identifier
     * @param dataBefore Data before deletion
     */
    public void logDelete(String entityType, String entityId, Object dataBefore) {
        loggingPort.logDelete(sessionId, entityType, entityId, dataBefore);
    }
}

