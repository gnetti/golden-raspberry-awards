package golden.raspberry.awards.adapter.driving.rest;

import golden.raspberry.awards.adapter.driving.rest.dto.ApiErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global Exception Handler for REST API.
 *
 * <p>Implements Richardson Level 2: structured and informative error messages.
 * Maps exceptions to appropriate HTTP status codes.
 *
 * <p><strong>Status Codes:</strong>
 * <ul>
 *   <li>400 Bad Request: IllegalArgumentException, IllegalStateException, NullPointerException</li>
 *   <li>500 Internal Server Error: All other exceptions</li>
 * </ul>
 *
 * <p>Uses Java 21 features: Records, var, String Templates.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handles IllegalArgumentException (400 Bad Request).
     * Used for validation errors.
     *
     * @param ex Exception thrown
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
     * Handles IllegalStateException (400 Bad Request).
     * Used for business logic errors.
     *
     * @param ex Exception thrown
     * @param request Web request
     * @return ResponseEntity with ApiErrorDTO
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorDTO> handleIllegalStateException(
            IllegalStateException ex, WebRequest request) {

        var error = ApiErrorDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                extractPath(request)
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Handles NullPointerException (400 Bad Request).
     * Should not occur in production, but handled for safety.
     *
     * @param ex Exception thrown
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
     * Handles all other exceptions (500 Internal Server Error).
     * Generic handler for unexpected errors.
     *
     * @param ex Exception thrown
     * @param request Web request
     * @return ResponseEntity with ApiErrorDTO
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorDTO> handleGenericException(
            Exception ex, WebRequest request) {

        var message = ex.getMessage() != null && !ex.getMessage().isBlank()
                ? "An unexpected error occurred: %s".formatted(ex.getMessage())
                : "An unexpected error occurred";

        var error = ApiErrorDTO.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                message,
                extractPath(request)
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * Extracts path from WebRequest description.
     *
     * @param request Web request
     * @return Request path
     */
    private String extractPath(WebRequest request) {
        var description = request.getDescription(false);
        return description.replace("uri=", "");
    }
}

