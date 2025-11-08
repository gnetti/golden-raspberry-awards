package golden.raspberry.awards.adapter.driven.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import golden.raspberry.awards.core.application.port.out.LoggingPort;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Output Adapter for logging operations.
 * Implements LoggingPort and writes logs to files in resources/log directory.
 *
 * <p><strong>Important:</strong> Custom logging service - NO external logging dependencies.
 * Uses only System. Err for critical errors (file system failures).
 *
 * <p>This adapter follows hexagonal architecture principles:
 * - Implements Port defined by Application layer
 * - Handles file system operations (Infrastructure)
 * - Formats logs with timestamp, sessionId, HTTP method, endpoint, status code, and errors
 *
 * <p>Log file format: `{timestamp}-{sessionId}.log`
 * Example: `2025-01-21T10-30-45-abc123.log`
 *
 * <p>Log format includes:
 * - Timestamp
 * - HTTP Method and Endpoint
 * - Status Code (200 OK, 400 Bad Request, 500 Internal Server Error, etc.)
 * - Entity Type and ID
 * - Request/Response data
 * - Errors (if any)
 * - Before/After data for PUT and DELETE operations
 *
 * <p>Uses Java 21 features: Records, var, String Templates, Text Blocks.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class FileLoggingAdapter implements LoggingPort {

    private static final String LOG_DIRECTORY = "src/main/resources/log";
    private static final DateTimeFormatter FILE_NAME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH-mm-ss");
    private static final DateTimeFormatter LOG_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final ObjectMapper objectMapper;

    /**
     * Constructor for dependency injection.
     *
     * @param objectMapper ObjectMapper for JSON serialization
     */
    public FileLoggingAdapter(ObjectMapper objectMapper) {
        this.objectMapper = Objects.requireNonNull(objectMapper, "ObjectMapper cannot be null");
        createLogDirectoryIfNeeded();
    }

    @Override
    public void logGet(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                       String entityType, String entityId, Object responseData, String error) {
        var logMessage = buildLogMessage(
                "GET", sessionId, httpMethod, endpoint, statusCode,
                entityType, entityId, null, responseData, error
        );
        writeToLogFile(sessionId, logMessage);
    }

    @Override
    public void logPut(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                       String entityType, String entityId, Object dataBefore, Object dataAfter, String error) {
        var logMessage = buildLogMessage(
                "PUT", sessionId, httpMethod, endpoint, statusCode,
                entityType, entityId, dataBefore, dataAfter, error
        );
        writeToLogFile(sessionId, logMessage);
    }

    @Override
    public void logDelete(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                          String entityType, String entityId, Object dataBefore, String error) {
        var logMessage = buildLogMessage(
                "DELETE", sessionId, httpMethod, endpoint, statusCode,
                entityType, entityId, dataBefore, null, error
        );
        writeToLogFile(sessionId, logMessage);
    }

    @Override
    public void logPost(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                        String entityType, String entityId, Object requestData, Object responseData, String error) {
        var logMessage = buildLogMessage(
                "POST", sessionId, httpMethod, endpoint, statusCode,
                entityType, entityId, requestData, responseData, error
        );
        writeToLogFile(sessionId, logMessage);
    }

    /**
     * Builds log message with formatted content.
     *
     * @param action      Action type (GET, PUT, DELETE, POST)
     * @param sessionId   Session identifier
     * @param httpMethod  HTTP method
     * @param endpoint    Endpoint path
     * @param statusCode  HTTP status code
     * @param entityType  Entity type
     * @param entityId    Entity identifier
     * @param dataBefore  Data before (for PUT/DELETE) or request data (for POST)
     * @param dataAfter   Data after (for PUT) or response data (for GET/POST)
     * @param error       Error message (if any)
     * @return Formatted log message
     */
    private String buildLogMessage(String action, String sessionId, String httpMethod, String endpoint,
                                   Integer statusCode, String entityType, String entityId,
                                   Object dataBefore, Object dataAfter, String error) {
        var timestamp = LocalDateTime.now().format(LOG_FORMATTER);
        var statusText = getStatusText(statusCode);
        var beforeJson = toJson(dataBefore);
        var afterJson = toJson(dataAfter);

        return """
                ========================================
                [%s] %s %s | Status: %d %s | Session: %s
                Entity: %s | ID: %s
                %s
                %s
                ========================================
                """.formatted(
                timestamp,
                httpMethod,
                endpoint,
                statusCode,
                statusText,
                sessionId,
                entityType,
                entityId != null ? entityId : "N/A",
                formatData(action, beforeJson, afterJson),
                formatError(error)
        );
    }

    /**
     * Formats data based on action type.
     *
     * @param action     Action type
     * @param beforeJson Data before (JSON) or request data (JSON)
     * @param afterJson  Data after (JSON) or response data (JSON)
     * @return Formatted data string
     */
    private String formatData(String action, String beforeJson, String afterJson) {
        return switch (action) {
            case "GET" -> "Response: %s".formatted(afterJson);
            case "PUT" -> """
                    Before: %s
                    After:  %s
                    """.formatted(beforeJson, afterJson);
            case "DELETE" -> "Deleted: %s".formatted(beforeJson);
            case "POST" -> """
                    Request:  %s
                    Response: %s
                    """.formatted(beforeJson, afterJson);
            default -> "";
        };
    }

    /**
     * Formats error message if present.
     *
     * @param error Error message (can be null)
     * @return Formatted error string or empty string
     */
    private String formatError(String error) {
        if (error == null || error.isBlank()) {
            return "";
        }
        return "ERROR: %s".formatted(error);
    }

    /**
     * Gets status text for HTTP status code.
     *
     * @param statusCode HTTP status code
     * @return Status text (e.g., "OK", "Bad Request", "Internal Server Error")
     */
    private String getStatusText(Integer statusCode) {
        if (statusCode == null) {
            return "Unknown";
        }
        return switch (statusCode) {
            case 200 -> "OK";
            case 201 -> "Created";
            case 400 -> "Bad Request";
            case 404 -> "Not Found";
            case 500 -> "Internal Server Error";
            default -> "Status " + statusCode;
        };
    }

    /**
     * Writes log message to file.
     * File name format: {timestamp}-{sessionId}.log
     *
     * @param sessionId  Session identifier
     * @param logMessage Log message to write
     */
    private void writeToLogFile(String sessionId, String logMessage) {
        try {
            var timestamp = LocalDateTime.now().format(FILE_NAME_FORMATTER);
            var fileName = "%s-%s.log".formatted(timestamp, sessionId);
            var filePath = Paths.get(LOG_DIRECTORY, fileName);

            Files.createDirectories(filePath.getParent());
            Files.writeString(
                    filePath,
                    logMessage,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            System.err.printf("Failed to write log: %s%n", e.getMessage());
        }
    }

    /**
     * Converts object to JSON string.
     *
     * @param obj Object to serialize
     * @return JSON string or "null"
     */
    private String toJson(Object obj) {
        if (obj == null) {
            return "null";
        }
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            return "{\"error\": \"Failed to serialize: %s\"}".formatted(e.getMessage());
        }
    }

    /**
     * Creates log directory if it doesn't exist.
     */
    private void createLogDirectoryIfNeeded() {
        try {
            var logPath = Paths.get(LOG_DIRECTORY);
            if (!Files.exists(logPath)) {
                Files.createDirectories(logPath);
            }
        } catch (IOException e) {
            System.err.printf("Failed to create log directory: %s%n", e.getMessage());
        }
    }
}
