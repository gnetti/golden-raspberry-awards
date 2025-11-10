package golden.raspberry.awards.adapter.driving.rest.exception;

import golden.raspberry.awards.adapter.driving.rest.dto.ApiErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.stream.Collectors;

/**
 * Global Exception Handler for REST API.
 * <p>Implements Richardson Level 2: structured and informative error messages.
 * Maps exceptions to appropriate HTTP status codes.
 * <p><strong>Status Codes:</strong>
 * <ul>
 *   <li>400 Bad Request: IllegalArgumentException, IllegalStateException, NullPointerException, HttpMessageNotReadableException</li>
 *   <li>500 Internal Server Error: All other exceptions</li>
 * </ul>
 * *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    /**
     * Handles IllegalArgumentException (400 Bad Request).
     * Used for validation errors.
     * @param ex      Exception thrown
     * @param request Web request
     * @return ResponseEntity with ApiErrorDTO
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDTO> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        var error = ApiErrorDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                extractPath(request)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles IllegalStateException.
     * Returns 404 (Not Found) if message contains "not found", otherwise 400 (Bad Request).
     * @param ex      Exception thrown
     * @param request Web request
     * @return ResponseEntity with ApiErrorDTO
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorDTO> handleIllegalStateException(
            IllegalStateException ex, WebRequest request) {

        var message = ex.getMessage();
        var isNotFound = message != null && message.toLowerCase().contains("not found");
        var status = isNotFound ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;

        var error = ApiErrorDTO.of(
                status.value(),
                isNotFound ? "Not Found" : "Bad Request",
                message != null ? message : "Illegal state",
                extractPath(request)
        );

        return ResponseEntity.status(status).body(error);
    }

    /**
     * Handles NullPointerException (400 Bad Request).
     * Should not occur in production, but handled for safety.
     * @param ex      Exception thrown
     * @param request Web request
     * @return ResponseEntity with ApiErrorDTO
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiErrorDTO> handleNullPointerException(
            NullPointerException ex, WebRequest request) {

        var message = ex.getMessage() != null && !ex.getMessage().isBlank()
                ? ex.getMessage()
                : "Required field is missing or null";

        var error = ApiErrorDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                message,
                extractPath(request)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles MethodArgumentNotValidException (400 Bad Request).
     * Occurs when @Valid validation fails on request body.
     * @param ex      Exception thrown
     * @param request Web request
     * @return ResponseEntity with ApiErrorDTO
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            WebRequest request) {

        var fieldErrors = ex.getBindingResult().getFieldErrors();
        var errorCount = fieldErrors.size();

        var errorMessage = fieldErrors.stream()
                .map(error -> {
                    var fieldName = error.getField();
                    var message = error.getDefaultMessage();
                    
                    if (error.getRejectedValue() == null) {
                        return "Field '%s' is missing from request body".formatted(fieldName);
                    } else {
                        return "Field '%s' has invalid value: %s".formatted(fieldName, message != null ? message : "Invalid value");
                    }
                })
                .collect(Collectors.joining("; "));

        var finalMessage = errorCount > 1
                ? "Validation failed with %d error(s): %s".formatted(errorCount, errorMessage)
                : errorMessage.isEmpty() ? "Invalid request body" : errorMessage;

        var error = ApiErrorDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                finalMessage,
                extractPath(request)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles HttpMessageNotReadableException (400 Bad Request).
     * Occurs when JSON is malformed, missing, or contains type mismatches.
     * Provides clear and concise error messages.
     * @param ex      Exception thrown
     * @param request Web request
     * @return ResponseEntity with ApiErrorDTO
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDTO> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex,
            WebRequest request) {

        var rootCause = ex.getRootCause();
        var rootCauseMessage = rootCause != null ? rootCause.getMessage() : null;
        var exceptionMessage = ex.getMessage();
        
        // Extract specific error information
        String message;
        if (rootCauseMessage != null && !rootCauseMessage.isBlank()) {
            // Check for JsonParseException with "Unrecognized token" - usually means invalid value type
            var isUnrecognizedToken = rootCauseMessage.contains("Unrecognized token") 
                    && (rootCauseMessage.contains("was expecting") || rootCauseMessage.contains("true") || rootCauseMessage.contains("false"));
            
            // Check for missing or empty field values (e.g., "year": , "winner": ,)
            // This includes "expected a value" errors and "Unexpected character" with comma (code 44)
            var isExpectedValueError = rootCauseMessage.contains("expected a value");
            var isCommaError = rootCauseMessage.contains("Unexpected character") && rootCauseMessage.contains("code 44");
            
            if (isUnrecognizedToken || isExpectedValueError || isCommaError) {
                // Try to identify which field has the issue
                // First, check the full exception message which may contain more context
                var fullMessage = exceptionMessage != null ? exceptionMessage : rootCauseMessage;
                
                // Extract column position to help identify the field
                var columnMatch = java.util.regex.Pattern.compile("column: (\\d+)").matcher(rootCauseMessage);
                var lineMatch = java.util.regex.Pattern.compile("line: (\\d+)").matcher(rootCauseMessage);
                var columnFound = columnMatch.find();
                var lineFound = lineMatch.find();
                var columnNumber = columnFound ? Integer.parseInt(columnMatch.group(1)) : -1;
                var lineNumber = lineFound ? Integer.parseInt(lineMatch.group(1)) : -1;
                
                // Check if we can identify the field name from context
                // Search in both rootCauseMessage and exceptionMessage for better detection
                String fieldName = null;
                
                // Strategy 1: Direct field name search (most reliable)
                // Check for quoted field names first (exact match)
                var allMessages = fullMessage + " " + rootCauseMessage;
                var allMessagesLower = allMessages.toLowerCase();
                
                // Priority order: year and winner first (as they are most problematic)
                if (allMessages.contains("\"year\"") || allMessagesLower.contains("\"year\"")) {
                    fieldName = "year";
                } else if (allMessages.contains("\"winner\"") || allMessagesLower.contains("\"winner\"")) {
                    fieldName = "winner";
                } else if (allMessages.contains("\"title\"") || allMessagesLower.contains("\"title\"")) {
                    fieldName = "title";
                } else if (allMessages.contains("\"studios\"") || allMessagesLower.contains("\"studios\"")) {
                    fieldName = "studios";
                } else if (allMessages.contains("\"producers\"") || allMessagesLower.contains("\"producers\"")) {
                    fieldName = "producers";
                } else {
                    // Strategy 2: Word boundary search (case-insensitive)
                    var yearPattern = java.util.regex.Pattern.compile("\\byear\\b", java.util.regex.Pattern.CASE_INSENSITIVE);
                    var winnerPattern = java.util.regex.Pattern.compile("\\bwinner\\b", java.util.regex.Pattern.CASE_INSENSITIVE);
                    var titlePattern = java.util.regex.Pattern.compile("\\btitle\\b", java.util.regex.Pattern.CASE_INSENSITIVE);
                    var studiosPattern = java.util.regex.Pattern.compile("\\bstudios\\b", java.util.regex.Pattern.CASE_INSENSITIVE);
                    var producersPattern = java.util.regex.Pattern.compile("\\bproducers\\b", java.util.regex.Pattern.CASE_INSENSITIVE);
                    
                    // Check in order of priority
                    if (yearPattern.matcher(allMessages).find()) {
                        fieldName = "year";
                    } else if (winnerPattern.matcher(allMessages).find()) {
                        fieldName = "winner";
                    } else if (titlePattern.matcher(allMessages).find()) {
                        fieldName = "title";
                    } else if (studiosPattern.matcher(allMessages).find()) {
                        fieldName = "studios";
                    } else if (producersPattern.matcher(allMessages).find()) {
                        fieldName = "producers";
                    }
                }
                
                // Strategy 3: If still not found and we have column/line info, try to infer from position
                // This is a fallback - JSON structure: {"year":..., "title":..., "studios":..., "producers":..., "winner":...}
                // Note: This is approximate and may not always work
                if (fieldName == null) {
                    // Special case: If error mentions "true" or "false" in "was expecting", it's likely a boolean field (winner)
                    if (isUnrecognizedToken && (rootCauseMessage.contains("'true'") || rootCauseMessage.contains("'false'"))) {
                        fieldName = "winner";
                    } else if (lineNumber > 1 && columnNumber > 0 && columnNumber <= 5) {
                        // Last field (winner) typically appears at column 1-5 on a new line
                        fieldName = "winner";
                    } else if (lineNumber == 1 && columnNumber > 0 && columnNumber < 20) {
                        // First field (year) typically appears early on line 1
                        fieldName = "year";
                    } else if (columnNumber > 50) {
                        // If column is very far right, might be a later field
                        // Could be studios, producers, or winner depending on content
                        fieldName = "winner"; // Default to winner as it's most common issue
                    } else if (columnNumber > 0 && columnNumber < 20) {
                        // Early columns likely to be year
                        fieldName = "year";
                    }
                }
                
                if (fieldName != null) {
                    // Get field type and valid values description
                    var fieldInfo = getFieldTypeAndValidValues(fieldName);
                    message = "Field '%s' has invalid or missing value. Expected type: %s. Valid values: %s.".formatted(
                            fieldName, fieldInfo.type(), fieldInfo.validValues()
                    );
                } else {
                    var lineInfo = lineNumber > 0 ? " at line %s".formatted(lineNumber) : "";
                    var columnInfo = columnNumber > 0 ? ", column %s".formatted(columnNumber) : "";
                    message = "A field has invalid or missing value%s%s. Please ensure all fields have valid values (year: integer, title: string, studios: string, producers: string, winner: boolean).".formatted(lineInfo, columnInfo);
                }
            } else if (rootCauseMessage.contains("Unexpected character")) {
                // Always try to identify the field for "Unexpected character" errors
                // This catches cases where field is empty (e.g., "winner": ,)
                var fullMessage = exceptionMessage != null ? exceptionMessage : rootCauseMessage;
                var columnMatch = java.util.regex.Pattern.compile("column: (\\d+)").matcher(rootCauseMessage);
                var lineMatch = java.util.regex.Pattern.compile("line: (\\d+)").matcher(rootCauseMessage);
                var columnFound = columnMatch.find();
                var lineFound = lineMatch.find();
                var columnNumber = columnFound ? Integer.parseInt(columnMatch.group(1)) : -1;
                var lineNumber = lineFound ? Integer.parseInt(lineMatch.group(1)) : -1;
                
                String fieldName = null;
                var allMessages = fullMessage + " " + rootCauseMessage;
                
                // Strategy 1: Try to find field name in messages
                if (allMessages.contains("\"winner\"") || allMessages.toLowerCase().contains("\"winner\"")) {
                    fieldName = "winner";
                } else if (allMessages.contains("\"year\"") || allMessages.toLowerCase().contains("\"year\"")) {
                    fieldName = "year";
                } else if (allMessages.contains("\"title\"") || allMessages.toLowerCase().contains("\"title\"")) {
                    fieldName = "title";
                } else if (allMessages.contains("\"studios\"") || allMessages.toLowerCase().contains("\"studios\"")) {
                    fieldName = "studios";
                } else if (allMessages.contains("\"producers\"") || allMessages.toLowerCase().contains("\"producers\"")) {
                    fieldName = "producers";
                }
                
                // Strategy 2: Infer from position (especially for winner which is last field)
                if (fieldName == null && lineNumber > 0 && columnNumber > 0) {
                    // winner is typically the last field, appears on line > 1 with small column number
                    if (lineNumber > 1 && columnNumber <= 5) {
                        fieldName = "winner";
                    } else if (lineNumber == 1 && columnNumber < 20) {
                        fieldName = "year";
                    } else if (rootCauseMessage.contains("code 44")) {
                        // Comma error (code 44) usually means empty field value
                        // If we can't identify, default to winner if line > 1, year if line 1
                        fieldName = lineNumber > 1 ? "winner" : "year";
                    }
                }
                
                if (fieldName != null) {
                    // Get field type and valid values description
                    var fieldInfo = getFieldTypeAndValidValues(fieldName);
                    message = "Field '%s' has invalid or missing value. Expected type: %s. Valid values: %s.".formatted(
                            fieldName, fieldInfo.type(), fieldInfo.validValues()
                    );
                } else {
                    // Fallback to generic message only if we really can't identify
                    var lineInfo = lineNumber > 0 ? " at line %s".formatted(lineNumber) : "";
                    var columnInfo = columnNumber > 0 ? ", column %s".formatted(columnNumber) : "";
                    message = "A field has invalid or missing value%s%s. Please ensure all fields have valid values (year: integer, title: string, studios: string, producers: string, winner: boolean).".formatted(lineInfo, columnInfo);
                }
            } else if (rootCauseMessage.contains("Cannot deserialize value") 
                    || rootCauseMessage.contains("not a valid representation")
                    || rootCauseMessage.contains("not a valid")
                    || rootCauseMessage.contains("from String")
                    || rootCauseMessage.contains("from Number")
                    || (rootCause != null && rootCause.getClass().getSimpleName().contains("InvalidFormatException"))) {
                // Try to identify which field has type mismatch
                var fullMessage = exceptionMessage != null ? exceptionMessage : rootCauseMessage;
                var allMessages = fullMessage + " " + rootCauseMessage;
                var allMessagesLower = allMessages.toLowerCase();
                
                String fieldName = null;
                
                // Check for field names in error message
                if (allMessages.contains("\"winner\"") || allMessagesLower.contains("\"winner\"")) {
                    fieldName = "winner";
                } else if (allMessages.contains("\"year\"") || allMessagesLower.contains("\"year\"")) {
                    fieldName = "year";
                } else if (allMessages.contains("\"title\"") || allMessagesLower.contains("\"title\"")) {
                    fieldName = "title";
                } else if (allMessages.contains("\"studios\"") || allMessagesLower.contains("\"studios\"")) {
                    fieldName = "studios";
                } else if (allMessages.contains("\"producers\"") || allMessagesLower.contains("\"producers\"")) {
                    fieldName = "producers";
                } else {
                    // Try word boundary search
                    var yearPattern = java.util.regex.Pattern.compile("\\byear\\b", java.util.regex.Pattern.CASE_INSENSITIVE);
                    var winnerPattern = java.util.regex.Pattern.compile("\\bwinner\\b", java.util.regex.Pattern.CASE_INSENSITIVE);
                    var titlePattern = java.util.regex.Pattern.compile("\\btitle\\b", java.util.regex.Pattern.CASE_INSENSITIVE);
                    var studiosPattern = java.util.regex.Pattern.compile("\\bstudios\\b", java.util.regex.Pattern.CASE_INSENSITIVE);
                    var producersPattern = java.util.regex.Pattern.compile("\\bproducers\\b", java.util.regex.Pattern.CASE_INSENSITIVE);
                    
                    if (winnerPattern.matcher(allMessages).find()) {
                        fieldName = "winner";
                    } else if (yearPattern.matcher(allMessages).find()) {
                        fieldName = "year";
                    } else if (titlePattern.matcher(allMessages).find()) {
                        fieldName = "title";
                    } else if (studiosPattern.matcher(allMessages).find()) {
                        fieldName = "studios";
                    } else if (producersPattern.matcher(allMessages).find()) {
                        fieldName = "producers";
                    }
                }
                
                if (fieldName != null) {
                    var fieldInfo = getFieldTypeAndValidValues(fieldName);
                    message = "Field '%s' has invalid value type. Expected type: %s. Valid values: %s.".formatted(
                            fieldName, fieldInfo.type(), fieldInfo.validValues()
                    );
                } else {
                    message = "Invalid field type in JSON. Please ensure field types are correct: year (integer), title (string), studios (string), producers (string), winner (boolean).";
                }
            } else {
                // Generic but concise message
                message = "Invalid JSON format. Please ensure the request body is valid JSON.";
            }
        } else if (exceptionMessage != null && !exceptionMessage.isBlank()) {
            // Check if it's a missing body error
            if (exceptionMessage.contains("Required request body is missing") || exceptionMessage.contains("body is missing")) {
                message = "Request body is required. Please provide a JSON body with the following structure: {\"year\": 2024, \"title\": \"string\", \"studios\": \"string\", \"producers\": \"string\", \"winner\": true}.";
            } else {
                message = "Invalid JSON format. Please ensure the request body is valid JSON.";
            }
        } else {
            message = "Invalid JSON format. Please ensure the request body is valid JSON.";
        }

        var error = ApiErrorDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                message,
                extractPath(request)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Gets field type and valid values description for a given field name.
     * @param fieldName Name of the field
     * @return FieldInfo with type and valid values
     */
    private FieldInfo getFieldTypeAndValidValues(String fieldName) {
        return switch (fieldName.toLowerCase()) {
            case "year" -> new FieldInfo("integer", "a number between 1900 and 2100 (e.g., 2024)");
            case "title" -> new FieldInfo("string", "a non-empty text with 1 to 500 characters (e.g., \"The Matrix\")");
            case "studios" -> new FieldInfo("string", "a non-empty text with 1 to 500 characters (e.g., \"Warner Bros. Pictures\")");
            case "producers" -> new FieldInfo("string", "a non-empty text with 1 to 1000 characters (e.g., \"Lana Wachowski, Grant Hill\")");
            case "winner" -> new FieldInfo("boolean", "true or false (e.g., true)");
            default -> new FieldInfo("unknown", "valid value according to field specification");
        };
    }
    
    /**
     * Record to hold field type and valid values information.
     */
    private record FieldInfo(String type, String validValues) {}
    
    /**
     * Extracts path from WebRequest description.
     * @param request Web request
     * @return Request path
     */
    private String extractPath(WebRequest request) {
        var description = request.getDescription(false);
        return description.replace("uri=", "");
    }
}

