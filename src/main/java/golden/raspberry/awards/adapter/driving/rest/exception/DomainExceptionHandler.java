package golden.raspberry.awards.adapter.driving.rest.exception;

import golden.raspberry.awards.adapter.driving.rest.dto.ApiErrorDTO;
import golden.raspberry.awards.shared.exception.DomainException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Exception handler for DomainException and ConstraintViolationException.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@RestControllerAdvice
public class DomainExceptionHandler {

    /**
     * Handles DomainException.
     *
     * @param exception DomainException to handle
     * @param request Web request
     * @return ResponseEntity with ApiErrorDTO
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ApiErrorDTO> handleDomainException(
            DomainException exception, WebRequest request) {
        Objects.requireNonNull(exception, "DomainException cannot be null");
        Objects.requireNonNull(request, "WebRequest cannot be null");
        var errorDTO = ApiErrorDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                "Domain Validation Error",
                exception.getMessage(),
                extractPath(request)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    /**
     * Handles ConstraintViolationException.
     *
     * @param exception ConstraintViolationException to handle
     * @param request Web request
     * @return ResponseEntity with ApiErrorDTO
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiErrorDTO> handleConstraintViolationException(
            ConstraintViolationException exception, WebRequest request) {
        Objects.requireNonNull(exception, "ConstraintViolationException cannot be null");
        Objects.requireNonNull(request, "WebRequest cannot be null");
        
        var errorMessage = exception.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        
        var errorDTO = ApiErrorDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                errorMessage.isEmpty() ? "Invalid request parameter" : errorMessage,
                extractPath(request)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    /**
     * Handles IllegalArgumentException.
     * Returns 400 (Bad Request) for validation errors from Use Cases.
     *
     * @param exception IllegalArgumentException to handle
     * @param request Web request
     * @return ResponseEntity with ApiErrorDTO
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorDTO> handleIllegalArgumentException(
            IllegalArgumentException exception, WebRequest request) {
        Objects.requireNonNull(exception, "IllegalArgumentException cannot be null");
        Objects.requireNonNull(request, "WebRequest cannot be null");
        
        var errorDTO = ApiErrorDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Error",
                exception.getMessage() != null ? exception.getMessage() : "Invalid argument",
                extractPath(request)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    /**
     * Handles IllegalStateException.
     * Returns 404 (Not Found) if message contains "not found", otherwise 400 (Bad Request).
     *
     * @param exception IllegalStateException to handle
     * @param request Web request
     * @return ResponseEntity with ApiErrorDTO
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiErrorDTO> handleIllegalStateException(
            IllegalStateException exception, WebRequest request) {
        Objects.requireNonNull(exception, "IllegalStateException cannot be null");
        Objects.requireNonNull(request, "WebRequest cannot be null");
        
        var message = exception.getMessage();
        var isNotFound = message != null && message.toLowerCase().contains("not found");
        var status = isNotFound ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
        
        var errorDTO = ApiErrorDTO.of(
                status.value(),
                isNotFound ? "Not Found" : "Bad Request",
                message != null ? message : "Illegal state",
                extractPath(request)
        );
        return ResponseEntity.status(status).body(errorDTO);
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

