package golden.raspberry.awards.adapter.driven.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import golden.raspberry.awards.core.application.port.out.ListenerPort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Output Adapter for listener operations.
 * Implements ListenerPort and writes listener files to daily folders in listener directory.
 *
 * <p><strong>Important:</strong> Custom listener service - NO external listener dependencies.
 *
 * <p>This adapter follows hexagonal architecture principles:
 * - Implements Port defined by Application layer
 * - Handles file system operations (Infrastructure)
 * - Formats listener files with timestamp, sessionId, HTTP method, endpoint, status code, and errors
 *
 * <p>Folder structure: `{base-path}/{yyyy_MM_dd}/`
 * File format: `{prefix}_{sessionId}_{yyyy_MM_dd_HH_mm_ss}.listener`
 * Example: `listener/2025_11_08/listener_abc123_2025_11_08_10_30_45.listener`
 *
 * <p>Listener format includes:
 * - Timestamp
 * - HTTP Method and Endpoint
 * - Status Code (200 OK, 400 Bad Request, 500 Internal Server Error, etc.)
 * - Entity Type and ID
 * - Request/Response data
 * - Errors (if any)
 * - Before/After data for PUT and DELETE operations
 *
 * <p>Uses Java 21 features: Records, var, String Templates, Text Blocks, Stream API.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class FileListenerAdapter implements ListenerPort {

    private final ObjectMapper objectMapper;
    private final boolean enabled;
    private final String basePath;
    private final String prefix;
    private final DateTimeFormatter folderDateFormatter;
    private final DateTimeFormatter logDateFormatter;
    private final int retentionDays;

    /**
     * Constructor for dependency injection.
     *
     * @param objectMapper ObjectMapper for JSON serialization
     * @param enabled Whether listener is enabled
     * @param basePath Base path for listener files
     * @param prefix Prefix for file names
     * @param folderDateFormat Format for folder date (e.g., "yyyy_MM_dd")
     * @param logDateFormat Format for log date (e.g., "yyyy-MM-dd HH:mm:ss")
     * @param retentionDays Number of days to retain listener files
     */
    public FileListenerAdapter(
            ObjectMapper objectMapper,
            @Value("${listener.enabled:true}") boolean enabled,
            @Value("${listener.base-path:src/main/resources/listener}") String basePath,
            @Value("${listener.prefix:listener}") String prefix,
            @Value("${listener.folder-date-format:yyyy_MM_dd}") String folderDateFormat,
            @Value("${listener.log-date-format:yyyy-MM-dd HH:mm:ss}") String logDateFormat,
            @Value("${listener.retention-days:7}") int retentionDays) {
        
        this.objectMapper = Objects.requireNonNull(objectMapper, "ObjectMapper cannot be null");
        this.enabled = enabled;
        this.basePath = Objects.requireNonNull(basePath, "Base path cannot be null");
        this.prefix = Objects.requireNonNull(prefix, "Prefix cannot be null");
        this.folderDateFormatter = DateTimeFormatter.ofPattern(normalizeDateFormat(folderDateFormat));
        this.logDateFormatter = DateTimeFormatter.ofPattern(normalizeDateFormat(logDateFormat));
        this.retentionDays = retentionDays;
        
        createBaseDirectoryIfNeeded();
        cleanupOldFolders();
    }

    @Override
    public void listenGet(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                       String entityType, String entityId, Object responseData, String error) {
        if (!enabled) return;
        
        var listenerMessage = buildListenerMessage(
                "GET", sessionId, httpMethod, endpoint, statusCode,
                entityType, entityId, null, responseData, error
        );
        writeToListenerFile(sessionId, listenerMessage);
    }

    @Override
    public void listenPut(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                       String entityType, String entityId, Object dataBefore, Object dataAfter, String error) {
        if (!enabled) return;
        
        var listenerMessage = buildListenerMessage(
                "PUT", sessionId, httpMethod, endpoint, statusCode,
                entityType, entityId, dataBefore, dataAfter, error
        );
        writeToListenerFile(sessionId, listenerMessage);
    }

    @Override
    public void listenDelete(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                          String entityType, String entityId, Object dataBefore, String error) {
        if (!enabled) return;
        
        var listenerMessage = buildListenerMessage(
                "DELETE", sessionId, httpMethod, endpoint, statusCode,
                entityType, entityId, dataBefore, null, error
        );
        writeToListenerFile(sessionId, listenerMessage);
    }

    @Override
    public void listenPost(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                        String entityType, String entityId, Object requestData, Object responseData, String error) {
        if (!enabled) return;
        
        var listenerMessage = buildListenerMessage(
                "POST", sessionId, httpMethod, endpoint, statusCode,
                entityType, entityId, requestData, responseData, error
        );
        writeToListenerFile(sessionId, listenerMessage);
    }

    /**
     * Builds listener message with formatted content.
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
     * @return Formatted listener message
     */
    private String buildListenerMessage(String action, String sessionId, String httpMethod, String endpoint,
                                   Integer statusCode, String entityType, String entityId,
                                   Object dataBefore, Object dataAfter, String error) {
        var timestamp = LocalDateTime.now().format(logDateFormatter);
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
     * Writes listener message to file in daily folder.
     * Folder format: {base-path}/{yyyy_MM_dd}/
     * File format: {prefix}_{sessionId}_{yyyy_MM_dd_HH_mm_ss}.listener
     *
     * @param sessionId      Session identifier
     * @param listenerMessage Listener message to write
     */
    private void writeToListenerFile(String sessionId, String listenerMessage) {
        try {
            var now = LocalDateTime.now();
            var folderName = now.format(folderDateFormatter);
            var fileName = "%s_%s_%s.listener".formatted(
                    prefix,
                    sessionId,
                    now.format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"))
            );
            
            var folderPath = Paths.get(basePath, folderName);
            var filePath = folderPath.resolve(fileName);

            Files.createDirectories(folderPath);
            Files.writeString(
                    filePath,
                    listenerMessage,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            handleIoError("write listener file", e);
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
     * Creates base directory if it doesn't exist.
     */
    private void createBaseDirectoryIfNeeded() {
        try {
            var basePathObj = Paths.get(basePath);
            if (!Files.exists(basePathObj)) {
                Files.createDirectories(basePathObj);
            }
        } catch (IOException e) {
            handleIoError("create base listener directory", e);
        }
    }

    /**
     * Handles IO errors silently.
     * Errors are handled silently to avoid violating immutable rules (no System.err).
     *
     * @param operation Operation that failed
     * @param exception IOException that occurred
     */
    private void handleIoError(String operation, IOException exception) {
        // Error handled silently - file system operation failed
        // Operation: {operation}, Error: {exception.getMessage()}
    }

    /**
     * Normalizes date format by converting `-` to `_`.
     * Ensures all dates use underscores instead of hyphens.
     *
     * @param dateFormat Original date format
     * @return Normalized date format with underscores
     */
    private String normalizeDateFormat(String dateFormat) {
        return dateFormat.replace('-', '_');
    }

    /**
     * Cleans up old folders based on retention days.
     * Removes folders that are older than the configured retention period.
     */
    private void cleanupOldFolders() {
        if (retentionDays <= 0) {
            return;
        }

        try {
            var basePathObj = Paths.get(basePath);
            if (!Files.exists(basePathObj)) {
                return;
            }

            var today = LocalDate.now();
            var cutoffDate = today.minusDays(retentionDays);

            try (Stream<Path> paths = Files.list(basePathObj)) {
                paths.filter(Files::isDirectory)
                        .forEach(folderPath -> {
                            var folderName = folderPath.getFileName().toString();
                            var folderDate = parseFolderDate(folderName);
                            
                            if (folderDate != null && folderDate.isBefore(cutoffDate)) {
                                try {
                                    deleteDirectoryRecursively(folderPath);
                                } catch (IOException e) {
                                    handleIoError("delete old folder: " + folderName, e);
                                }
                            }
                        });
            }
        } catch (IOException e) {
            handleIoError("cleanup old folders", e);
        }
    }

    /**
     * Parses folder date from folder name.
     * Folder name format should match folder-date-format (e.g., "yyyy_MM_dd").
     *
     * @param folderName Folder name (e.g., "2025_11_08")
     * @return Parsed LocalDate or null if parsing fails
     */
    private LocalDate parseFolderDate(String folderName) {
        try {
            return LocalDate.parse(folderName, folderDateFormatter);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Deletes directory recursively.
     *
     * @param directory Directory to delete
     * @throws IOException if deletion fails
     */
    private void deleteDirectoryRecursively(Path directory) throws IOException {
        if (Files.exists(directory)) {
            try (Stream<Path> paths = Files.walk(directory)) {
                paths.sorted(Comparator.reverseOrder())
                        .forEach(path -> {
                            try {
                                Files.delete(path);
                            } catch (IOException e) {
                                handleIoError("delete: " + path, e);
                            }
                        });
            }
        }
    }
}

