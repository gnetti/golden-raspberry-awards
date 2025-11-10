package golden.raspberry.awards.adapter.driving.rest.exception;

import golden.raspberry.awards.adapter.driving.rest.dto.ApiErrorDTO;
import golden.raspberry.awards.shared.exception.ApplicationException;
import golden.raspberry.awards.shared.exception.InfrastructureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Objects;

/**
 * Exception handler for ApplicationException and InfrastructureException.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@RestControllerAdvice
public class HexagonalExceptionHandler {

    /**
     * Handles ApplicationException.
     *
     * @param exception ApplicationException to handle
     * @param request Web request
     * @return ResponseEntity with ApiErrorDTO
     */
    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ApiErrorDTO> handleApplicationException(
            ApplicationException exception, WebRequest request) {
        Objects.requireNonNull(exception, "ApplicationException cannot be null");
        Objects.requireNonNull(request, "WebRequest cannot be null");
        var errorDTO = ApiErrorDTO.of(
                HttpStatus.BAD_REQUEST.value(),
                "Application Error",
                exception.getMessage(),
                extractPath(request)
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    /**
     * Handles InfrastructureException.
     *
     * @param exception InfrastructureException to handle
     * @param request Web request
     * @return ResponseEntity with ApiErrorDTO
     */
    @ExceptionHandler(InfrastructureException.class)
    public ResponseEntity<ApiErrorDTO> handleInfrastructureException(
            InfrastructureException exception, WebRequest request) {
        Objects.requireNonNull(exception, "InfrastructureException cannot be null");
        Objects.requireNonNull(request, "WebRequest cannot be null");
        var errorDTO = ApiErrorDTO.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Infrastructure Error",
                exception.getMessage(),
                extractPath(request)
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
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

