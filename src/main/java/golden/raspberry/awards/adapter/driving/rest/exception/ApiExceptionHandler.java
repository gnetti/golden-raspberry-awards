package golden.raspberry.awards.adapter.driving.rest.exception;

import golden.raspberry.awards.adapter.driving.rest.dto.ApiErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Arrays;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Pattern;
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
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@RestControllerAdvice
public class ApiExceptionHandler {

    private static final String FIELD_YEAR = "year";
    private static final String FIELD_WINNER = "winner";
    private static final String FIELD_TITLE = "title";
    private static final String FIELD_STUDIOS = "studios";
    private static final String FIELD_PRODUCERS = "producers";

    private static final Pattern COLUMN_PATTERN = Pattern.compile("column: (\\d+)");
    private static final Pattern LINE_PATTERN = Pattern.compile("line: (\\d+)");
    private static final Pattern FIELD_NAME_PATTERN = Pattern.compile("\"(%s|%s|%s|%s|%s)\"".formatted(
            FIELD_YEAR, FIELD_WINNER, FIELD_TITLE, FIELD_STUDIOS, FIELD_PRODUCERS));

    private static final String[] FIELD_PRIORITY = {FIELD_YEAR, FIELD_WINNER, FIELD_TITLE, FIELD_STUDIOS, FIELD_PRODUCERS};

    /**
     * Handles IllegalArgumentException (400 Bad Request).
     * Used for validation errors.
     *
     * @param ex      Exception thrown
     * @param request Web request
     * @return ResponseEntity with ApiErrorDTO
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDTO> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage(), request);
    }

    /**
     * Handles IllegalStateException.
     * Returns 404 (Not Found) if message contains "not found", otherwise 400 (Bad Request).
     *
     * @param ex      Exception thrown
     * @param request Web request
     * @return ResponseEntity with ApiErrorDTO
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorDTO> handleIllegalStateException(
            IllegalStateException ex, WebRequest request) {

        var message = Optional.ofNullable(ex.getMessage()).orElse("Illegal state");
        var isNotFound = message.toLowerCase().contains("not found");
        var status = isNotFound ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        var errorType = isNotFound ? "Not Found" : "Bad Request";

        return buildErrorResponse(status, errorType, message, request);
    }

    /**
     * Handles NullPointerException (400 Bad Request).
     * Should not occur in production, but handled for safety.
     *
     * @param ex      Exception thrown
     * @param request Web request
     * @return ResponseEntity with ApiErrorDTO
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ApiErrorDTO> handleNullPointerException(
            NullPointerException ex, WebRequest request) {

        var message = Optional.ofNullable(ex.getMessage())
                .filter(Predicate.not(String::isBlank))
                .orElse("Required field is missing or null");

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", message, request);
    }

    /**
     * Handles MethodArgumentNotValidException (400 Bad Request).
     * Occurs when @Valid validation fails on request body.
     *
     * @param ex      Exception thrown
     * @param request Web request
     * @return ResponseEntity with ApiErrorDTO
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorDTO> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, WebRequest request) {

        var fieldErrors = ex.getBindingResult().getFieldErrors();
        var errorCount = fieldErrors.size();

        var errorMessage = fieldErrors.stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));

        var finalMessage = errorCount > 1
                ? "Validation failed with %d error(s): %s".formatted(errorCount, errorMessage)
                : errorMessage.isEmpty() ? "Invalid request body" : errorMessage;

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation Error", finalMessage, request);
    }

    /**
     * Handles HttpMessageNotReadableException (400 Bad Request).
     * Occurs when JSON is malformed, missing, or contains type mismatches.
     * Provides clear and concise error messages.
     *
     * @param ex      Exception thrown
     * @param request Web request
     * @return ResponseEntity with ApiErrorDTO
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorDTO> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex, WebRequest request) {

        var errorContext = extractErrorContext(ex);
        var message = buildErrorMessage(errorContext);

        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Bad Request", message, request);
    }

    /**
     * Handles all other exceptions (500 Internal Server Error).
     *
     * @param ex      Exception thrown
     * @param request Web request
     * @return ResponseEntity with ApiErrorDTO
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGenericException(
            Exception ex, WebRequest request) {

        var message = Optional.ofNullable(ex.getMessage())
                .orElse("An unexpected error occurred");

        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", message, request);
    }

    private String formatFieldError(org.springframework.validation.FieldError error) {
        var fieldName = error.getField();
        var message = error.getDefaultMessage();

        return error.getRejectedValue() == null
                ? "Field '%s' is missing from request body".formatted(fieldName)
                : "Field '%s' has invalid value: %s".formatted(fieldName, message != null ? message : "Invalid value");
    }

    private ErrorContext extractErrorContext(HttpMessageNotReadableException ex) {
        var rootCause = ex.getCause();
        var rootCauseMessage = Optional.ofNullable(rootCause)
                .map(Throwable::getMessage)
                .orElse(null);
        var exceptionMessage = ex.getMessage();
        var allMessages = combineMessages(exceptionMessage, rootCauseMessage);

        var position = extractPosition(rootCauseMessage);
        var errorType = detectErrorType(rootCauseMessage, rootCause);
        var fieldName = detectFieldName(allMessages, position, errorType);

        return new ErrorContext(rootCauseMessage, exceptionMessage, allMessages, position, errorType, fieldName);
    }

    private String combineMessages(String exceptionMessage, String rootCauseMessage) {
        return String.join(" ", 
                Optional.ofNullable(exceptionMessage).orElse(""),
                Optional.ofNullable(rootCauseMessage).orElse(""));
    }

    private Position extractPosition(String rootCauseMessage) {
        if (rootCauseMessage == null) {
            return new Position(-1, -1);
        }

        var column = extractNumber(COLUMN_PATTERN, rootCauseMessage).orElse(-1);
        var line = extractNumber(LINE_PATTERN, rootCauseMessage).orElse(-1);

        return new Position(line, column);
    }

    private Optional<Integer> extractNumber(Pattern pattern, String text) {
        return pattern.matcher(text)
                .results()
                .findFirst()
                .map(match -> Integer.parseInt(match.group(1)));
    }

    private ErrorType detectErrorType(String rootCauseMessage, Throwable rootCause) {
        if (rootCauseMessage == null) {
            return ErrorType.GENERIC;
        }

        return switch (rootCauseMessage) {
            case String msg when msg.contains("Unrecognized token") && 
                    (msg.contains("was expecting") || msg.contains("true") || msg.contains("false")) -> ErrorType.UNRECOGNIZED_TOKEN;
            case String msg when msg.contains("expected a value") -> ErrorType.EXPECTED_VALUE;
            case String msg when msg.contains("Unexpected character") && msg.contains("code 44") -> ErrorType.COMMA_ERROR;
            case String msg when msg.contains("Unexpected character") -> ErrorType.UNEXPECTED_CHARACTER;
            case String msg when msg.contains("Cannot deserialize value") ||
                    msg.contains("not a valid representation") ||
                    msg.contains("not a valid") ||
                    msg.contains("from String") ||
                    msg.contains("from Number") -> ErrorType.TYPE_MISMATCH;
            default -> isInvalidFormatException(rootCause) ? ErrorType.TYPE_MISMATCH : ErrorType.GENERIC;
        };
    }

    private boolean isInvalidFormatException(Throwable rootCause) {
        return rootCause != null && 
                rootCause.getClass().getSimpleName().contains("InvalidFormatException");
    }

    private String detectFieldName(String allMessages, Position position, ErrorType errorType) {
        return detectFieldNameByQuotedMatch(allMessages)
                .or(() -> detectFieldNameByWordBoundary(allMessages))
                .or(() -> inferFieldNameFromPosition(position, errorType))
                .orElse(null);
    }

    private Optional<String> detectFieldNameByQuotedMatch(String allMessages) {
        var matcher = FIELD_NAME_PATTERN.matcher(allMessages);
        if (matcher.find()) {
            return Optional.of(matcher.group(1));
        }

        return Arrays.stream(FIELD_PRIORITY)
                .filter(field -> allMessages.contains("\"%s\"".formatted(field)) || 
                        allMessages.toLowerCase().contains("\"%s\"".formatted(field)))
                .findFirst();
    }

    private Optional<String> detectFieldNameByWordBoundary(String allMessages) {
        return Arrays.stream(FIELD_PRIORITY)
                .filter(field -> Pattern.compile("\\b%s\\b".formatted(field), Pattern.CASE_INSENSITIVE)
                        .matcher(allMessages)
                        .find())
                .findFirst();
    }

    private Optional<String> inferFieldNameFromPosition(Position position, ErrorType errorType) {
        if (position.line() <= 0 || position.column() <= 0) {
            return Optional.empty();
        }

        return switch (errorType) {
            case UNRECOGNIZED_TOKEN -> inferFromUnrecognizedToken(position);
            case COMMA_ERROR -> inferFromCommaError(position);
            default -> inferFromColumnPosition(position);
        };
    }

    private Optional<String> inferFromUnrecognizedToken(Position position) {
        if (position.line() > 1 && position.column() <= 5) {
            return Optional.of(FIELD_WINNER);
        }
        if (position.line() == 1 && position.column() < 20) {
            return Optional.of(FIELD_YEAR);
        }
        return Optional.empty();
    }

    private Optional<String> inferFromCommaError(Position position) {
        return Optional.of(position.line() > 1 ? FIELD_WINNER : FIELD_YEAR);
    }

    private Optional<String> inferFromColumnPosition(Position position) {
        if (position.column() > 50) {
            return Optional.of(FIELD_WINNER);
        }
        if (position.column() < 20) {
            return Optional.of(FIELD_YEAR);
        }
        return Optional.empty();
    }

    private String buildErrorMessage(ErrorContext context) {
        if (context.rootCauseMessage() != null && !context.rootCauseMessage().isBlank()) {
            return buildMessageFromRootCause(context);
        }

        if (context.exceptionMessage() != null && !context.exceptionMessage().isBlank()) {
            return buildMessageFromException(context);
        }

        return "Invalid JSON format. Please ensure the request body is valid JSON.";
    }

    private String buildMessageFromRootCause(ErrorContext context) {
        return switch (context.errorType()) {
            case UNRECOGNIZED_TOKEN, EXPECTED_VALUE, COMMA_ERROR, UNEXPECTED_CHARACTER ->
                    buildFieldSpecificMessage(context, "invalid or missing value");
            case TYPE_MISMATCH ->
                    buildFieldSpecificMessage(context, "invalid value type");
            default -> "Invalid JSON format. Please ensure the request body is valid JSON.";
        };
    }

    private String buildFieldSpecificMessage(ErrorContext context, String issueDescription) {
        return Optional.ofNullable(context.fieldName())
                .map(fieldName -> {
                    var fieldInfo = getFieldTypeAndValidValues(fieldName);
                    return "Field '%s' has %s. Expected type: %s. Valid values: %s.".formatted(
                            fieldName, issueDescription, fieldInfo.type(), fieldInfo.validValues());
                })
                .orElse(buildGenericFieldMessage(context));
    }

    private String buildGenericFieldMessage(ErrorContext context) {
        var position = context.position();
        var lineInfo = position.line() > 0 ? " at line %s".formatted(position.line()) : "";
        var columnInfo = position.column() > 0 ? ", column %s".formatted(position.column()) : "";

        return "A field has invalid or missing value%s%s. Please ensure all fields have valid values (year: integer, title: string, studios: string, producers: string, winner: boolean).".formatted(
                lineInfo, columnInfo);
    }

    private String buildMessageFromException(ErrorContext context) {
        var exceptionMessage = context.exceptionMessage();
        
        if (exceptionMessage.contains("Required request body is missing") || 
            exceptionMessage.contains("body is missing")) {
            return "Request body is required. Please provide a JSON body with the following structure: {\"year\": 2024, \"title\": \"string\", \"studios\": \"string\", \"producers\": \"string\", \"winner\": true}.";
        }

        return "Invalid JSON format. Please ensure the request body is valid JSON.";
    }

    private FieldInfo getFieldTypeAndValidValues(String fieldName) {
        int currentYear = java.time.Year.now().getValue();
        return switch (fieldName.toLowerCase()) {
            case "year" -> new FieldInfo("integer", 
                    "a number between 1900 and %d (current year, no future years allowed) (e.g., %d)".formatted(currentYear, currentYear));
            case "title" -> new FieldInfo("string", 
                    "a non-empty text with 1 to 500 characters (e.g., \"The Matrix\")");
            case "studios" -> new FieldInfo("string", 
                    "a non-empty text with 1 to 500 characters (e.g., \"Warner Bros. Pictures\")");
            case "producers" -> new FieldInfo("string", 
                    "a non-empty text with 1 to 1000 characters (e.g., \"Lana Wachowski, Grant Hill\")");
            case "winner" -> new FieldInfo("boolean", "true or false (e.g., true)");
            default -> new FieldInfo("unknown", "valid value according to field specification");
        };
    }

    private ResponseEntity<ApiErrorDTO> buildErrorResponse(
            HttpStatus status, String errorType, String message, WebRequest request) {

        var error = ApiErrorDTO.of(status.value(), errorType, message, extractPath(request));
        return ResponseEntity.status(status).body(error);
    }

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }

    private record ErrorContext(
            String rootCauseMessage,
            String exceptionMessage,
            String allMessages,
            Position position,
            ErrorType errorType,
            String fieldName
    ) {}

    private record Position(int line, int column) {}

    private record FieldInfo(String type, String validValues) {}

    private enum ErrorType {
        UNRECOGNIZED_TOKEN,
        EXPECTED_VALUE,
        COMMA_ERROR,
        UNEXPECTED_CHARACTER,
        TYPE_MISMATCH,
        GENERIC
    }
}
