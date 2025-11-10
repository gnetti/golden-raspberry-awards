package golden.raspberry.awards.infrastructure.adapter.driven.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import golden.raspberry.awards.core.application.port.out.ListenerPort;
import golden.raspberry.awards.core.application.service.ListenerAdapter;
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
import java.util.Optional;
import java.util.stream.Stream;

/**
 * Output Adapter for listener operations.
 * Implements ListenerPort and writes listener files to daily folders.
 *
 * <p>Folder structure: {base-path}/{yyyy_MM_dd}/
 * File format: {prefix}_{sessionId}_{yyyy_MM_dd_HH_mm_ss}.log
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class FileListenerAdapter implements ListenerPort {

    private final ObjectMapper objectMapper;
    private final ListenerAdapter listenerAdapter;
    private final boolean enabled;
    private final String basePath;
    private final String prefix;
    private final DateTimeFormatter folderDateFormatter;
    private final DateTimeFormatter recordDateFormatter;
    private final int retentionDays;

    /**
     * Constructor for dependency injection.
     *
     * @param objectMapper ObjectMapper for JSON serialization
     * @param listenerAdapter Listener adapter for orchestration patterns
     * @param enabled Whether listener is enabled
     * @param basePath Base path for listener files
     * @param prefix Prefix for file names
     * @param folderDateFormat Format for folder date (e.g., "yyyy_MM_dd")
     * @param recordDateFormat Format for record date (e.g., "yyyy-MM-dd HH:mm:ss")
     * @param retentionDays Number of days to retain listener files
     */
    public FileListenerAdapter(
            ObjectMapper objectMapper,
            ListenerAdapter listenerAdapter,
            @Value("${listener.enabled:true}") boolean enabled,
            @Value("${listener.base-path:src/main/resources/listener}") String basePath,
            @Value("${listener.prefix:listener}") String prefix,
            @Value("${listener.folder-date-format:yyyy_MM_dd}") String folderDateFormat,
            @Value("${listener.log-date-format:yyyy_MM_dd_HH_mm_ss}") String recordDateFormat,
            @Value("${listener.retention-days:7}") int retentionDays) {

        this.objectMapper = Objects.requireNonNull(objectMapper, "ObjectMapper cannot be null");
        this.listenerAdapter = Objects.requireNonNull(listenerAdapter, "ListenerAdapter cannot be null");
        this.enabled = enabled;
        this.basePath = Objects.requireNonNull(basePath, "Base path cannot be null");
        this.prefix = Objects.requireNonNull(prefix, "Prefix cannot be null");
        this.folderDateFormatter = DateTimeFormatter.ofPattern(normalizeDateFormat(folderDateFormat));
        this.recordDateFormatter = DateTimeFormatter.ofPattern(recordDateFormat);
        this.retentionDays = retentionDays;

        createBaseDirectoryIfNeeded();
        cleanupOldFolders();
    }

    /**
     * Listens to GET operations and records them to file.
     *
     * @param sessionId Session identifier
     * @param httpMethod HTTP method (e.g., "GET")
     * @param endpoint Endpoint path
     * @param statusCode HTTP status code
     * @param entityType Entity type
     * @param entityId Entity identifier
     * @param responseData Response data
     * @param error Error message (if any)
     */
    @Override
    public void listenGet(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                       String entityType, String entityId, Object responseData, String error) {
        if (!enabled) return;

        listenerAdapter.observeProcess("GET operation: %s %s".formatted(httpMethod, endpoint));
        listenerAdapter.emitWithSession(responseData, sessionId);
        listenerAdapter.recordResult(responseData);

        var listenerMessage = buildListenerMessage(
                "GET", sessionId, httpMethod, endpoint, statusCode,
                entityType, entityId, null, responseData, error
        );
        writeToListenerFile(sessionId, listenerMessage);
    }

    /**
     * Listens to PUT operations and records them to file.
     *
     * @param sessionId Session identifier
     * @param httpMethod HTTP method (e.g., "PUT")
     * @param endpoint Endpoint path
     * @param statusCode HTTP status code
     * @param entityType Entity type
     * @param entityId Entity identifier
     * @param dataBefore Data before update
     * @param dataAfter Data after update
     * @param error Error message (if any)
     */
    @Override
    public void listenPut(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                       String entityType, String entityId, Object dataBefore, Object dataAfter, String error) {
        if (!enabled) return;

        listenerAdapter.observeProcess("PUT operation: %s %s".formatted(httpMethod, endpoint));
        listenerAdapter.archiveData(dataBefore);
        listenerAdapter.archiveData(dataAfter);
        var changes = listenerAdapter.detectChanges(dataBefore, dataAfter);
        listenerAdapter.emitWithSession(changes, sessionId);
        listenerAdapter.recordResult(dataAfter);

        var listenerMessage = buildListenerMessage(
                "PUT", sessionId, httpMethod, endpoint, statusCode,
                entityType, entityId, dataBefore, dataAfter, error
        );
        writeToListenerFile(sessionId, listenerMessage);
    }

    /**
     * Listens to DELETE operations and records them to file.
     *
     * @param sessionId Session identifier
     * @param httpMethod HTTP method (e.g., "DELETE")
     * @param endpoint Endpoint path
     * @param statusCode HTTP status code
     * @param entityType Entity type
     * @param entityId Entity identifier
     * @param dataBefore Data before deletion
     * @param error Error message (if any)
     */
    @Override
    public void listenDelete(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                          String entityType, String entityId, Object dataBefore, String error) {
        if (!enabled) return;

        listenerAdapter.observeProcess("DELETE operation: %s %s".formatted(httpMethod, endpoint));
        listenerAdapter.archiveData(dataBefore);
        listenerAdapter.preserveData(dataBefore);
        listenerAdapter.emitWithSession(dataBefore, sessionId);
        listenerAdapter.recordResult(dataBefore);

        var listenerMessage = buildListenerMessage(
                "DELETE", sessionId, httpMethod, endpoint, statusCode,
                entityType, entityId, dataBefore, null, error
        );
        writeToListenerFile(sessionId, listenerMessage);
    }

    /**
     * Listens to POST operations and records them to file.
     *
     * @param sessionId Session identifier
     * @param httpMethod HTTP method (e.g., "POST")
     * @param endpoint Endpoint path
     * @param statusCode HTTP status code
     * @param entityType Entity type
     * @param entityId Entity identifier
     * @param requestData Request data
     * @param responseData Response data
     * @param error Error message (if any)
     */
    @Override
    public void listenPost(String sessionId, String httpMethod, String endpoint, Integer statusCode,
                        String entityType, String entityId, Object requestData, Object responseData, String error) {
        if (!enabled) return;

        listenerAdapter.observeProcess("POST operation: %s %s".formatted(httpMethod, endpoint));
        listenerAdapter.emitWithSession(requestData, sessionId);
        listenerAdapter.emitWithSession(responseData, sessionId);
        listenerAdapter.storeData(responseData);
        listenerAdapter.recordResult(responseData);

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
        var timestamp = LocalDateTime.now().format(recordDateFormatter);
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
     * If a file with the same sessionId already exists, it will be reused.
     *
     * @param sessionId      Session identifier
     * @param listenerMessage Listener message to write
     */
    private void writeToListenerFile(String sessionId, String listenerMessage) {
        try {
            var now = LocalDateTime.now();
            var folderName = now.format(folderDateFormatter);
            var folderPath = Paths.get(basePath, folderName);
            Files.createDirectories(folderPath);

            var existingFile = findExistingLogFile(folderPath, sessionId);
            var filePath = existingFile.orElseGet(() -> {
                var timestamp = now.format(recordDateFormatter);
                var fileName = "%s_%s_%s.log".formatted(prefix, sessionId, timestamp);
                return folderPath.resolve(fileName);
            });

            Files.writeString(
                    filePath,
                    listenerMessage,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );
        } catch (IOException e) {
            handleIoError(e);
        }
    }

    /**
     * Finds existing log file with the same sessionId.
     *
     * @param folderPath Folder path to search in
     * @param sessionId Session identifier to match
     * @return Optional containing existing file path if found, empty otherwise
     */
    private Optional<Path> findExistingLogFile(Path folderPath, String sessionId) {
        try {
            if (!Files.exists(folderPath)) {
                return Optional.empty();
            }

            var filePrefix = "%s_%s_".formatted(prefix, sessionId);
            try (Stream<Path> paths = Files.list(folderPath)) {
                return paths
                        .filter(Files::isRegularFile)
                        .filter(path -> {
                            var fileName = path.getFileName().toString();
                            return fileName.startsWith(filePrefix) && fileName.endsWith(".log");
                        })
                        .findFirst();
            }
        } catch (IOException e) {
            handleIoError(e);
            return Optional.empty();
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
            handleIoError(e);
        }
    }

    /**
     * Handles IO errors silently.
     *
     * @param exception IOException that occurred
     */
    private void handleIoError(IOException exception) {
        switch (exception) {
            case null -> {}
            case IOException ignored -> Objects.requireNonNull(ignored);
        }
    }

    /**
     * Normalizes date format by converting hyphens to underscores.
     *
     * @param dateFormat Original date format
     * @return Normalized date format with underscores
     */
    private String normalizeDateFormat(String dateFormat) {
        return dateFormat.replace('-', '_');
    }

    /**
     * Cleans up old folders based on retention days.
     * Removes folders that are older than or equal to the cutoff date.
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
                        .filter(folderPath -> {
                            var folderName = folderPath.getFileName().toString();
                            var folderDate = parseFolderDate(folderName);
                            return folderDate != null && !folderDate.isAfter(cutoffDate);
                        })
                        .forEach(folderPath -> {
                            try {
                                deleteDirectoryRecursively(folderPath);
                            } catch (IOException e) {
                                handleIoError(e);
                            }
                        });
            }
        } catch (IOException e) {
            handleIoError(e);
        }
    }

    /**
     * Parses folder date from folder name.
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
                                       handleIoError(e);
                                   }
                        });
            }
        }
    }
}

