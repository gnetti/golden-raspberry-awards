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
 * <p>This adapter follows hexagonal architecture principles:
 * - Implements Port defined by Application layer
 * - Handles file system operations (Infrastructure)
 * - Formats logs with timestamp and sessionId
 *
 * <p>Log file format: `{timestamp}-{sessionId}.log`
 * Example: `2025-01-21T10-30-45-abc123.log`
 *
 * <p>Uses Java 21 features: Records, var, String Templates, Text Blocks.
 *
 * @author Golden Raspberry Awards Team
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
    public void logCreate(String sessionId, String entityType, String entityId, Object dataAfter) {
        var logMessage = buildLogMessage("CREATE", sessionId, entityType, entityId, null, dataAfter);
        writeToLogFile(sessionId, logMessage);
    }

    @Override
    public void logUpdate(String sessionId, String entityType, String entityId, Object dataBefore, Object dataAfter) {
        var logMessage = buildLogMessage("UPDATE", sessionId, entityType, entityId, dataBefore, dataAfter);
        writeToLogFile(sessionId, logMessage);
    }

    @Override
    public void logDelete(String sessionId, String entityType, String entityId, Object dataBefore) {
        var logMessage = buildLogMessage("DELETE", sessionId, entityType, entityId, dataBefore, null);
        writeToLogFile(sessionId, logMessage);
    }

    /**
     * Builds log message with formatted content.
     *
     * @param action     Action type (CREATE, UPDATE, DELETE)
     * @param sessionId  Session identifier
     * @param entityType Entity type
     * @param entityId   Entity identifier
     * @param dataBefore Data before (for UPDATE/DELETE)
     * @param dataAfter  Data after (for CREATE/UPDATE)
     * @return Formatted log message
     */
    private String buildLogMessage(String action, String sessionId, String entityType, String entityId,
                                   Object dataBefore, Object dataAfter) {
        var timestamp = LocalDateTime.now().format(LOG_FORMATTER);
        var beforeJson = toJson(dataBefore);
        var afterJson = toJson(dataAfter);

        return """
                [%s] %s | Session: %s | Entity: %s | ID: %s
                %s
                """.formatted(
                timestamp,
                action,
                sessionId,
                entityType,
                entityId,
                formatData(action, beforeJson, afterJson)
        );
    }

    /**
     * Formats data based on action type.
     *
     * @param action     Action type
     * @param beforeJson Data before (JSON)
     * @param afterJson  Data after (JSON)
     * @return Formatted data string
     */
    private String formatData(String action, String beforeJson, String afterJson) {
        return switch (action) {
            case "CREATE" -> "Created: %s".formatted(afterJson);
            case "UPDATE" -> """
                    Before: %s
                    After:  %s
                    """.formatted(beforeJson, afterJson);
            case "DELETE" -> "Deleted: %s".formatted(beforeJson);
            default -> "";
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

