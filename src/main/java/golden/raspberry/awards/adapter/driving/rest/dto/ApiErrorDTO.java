package golden.raspberry.awards.adapter.driving.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO for API error responses. *
 * <p>Follows Richardson Level 2: structured and informative error messages.
 * *
 * @param timestamp Timestamp when the error occurred
 * @param status HTTP status code
 * @param error Error type
 * @param message Error message
 * @param path Request path that caused the error
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Schema(description = "Data Transfer Object for API error responses")
public record ApiErrorDTO(
        @JsonProperty("timestamp")
        @Schema(description = "Timestamp when the error occurred", example = "2025-11-10T15:56:31.7641082")
        LocalDateTime timestamp,

        @JsonProperty("status")
        @Schema(description = "HTTP status code", example = "400")
        Integer status,

        @JsonProperty("error")
        @Schema(description = "Error type", example = "Bad Request")
        String error,

        @JsonProperty("message")
        @Schema(description = "Detailed error message", example = "Field 'year' is missing from request body")
        String message,

        @JsonProperty("path")
        @Schema(description = "Request path that caused the error", example = "/api/movies")
        String path
) {
    public ApiErrorDTO {
        Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        Objects.requireNonNull(status, "Status cannot be null");
        Objects.requireNonNull(error, "Error cannot be null");
        Objects.requireNonNull(message, "Message cannot be null");
        Objects.requireNonNull(path, "Path cannot be null");

        if (error.isBlank()) {
            throw new IllegalArgumentException("Error cannot be blank");
        }
        if (message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be blank");
        }
    }

    /**
     * Static factory method to create ApiErrorDTO with current timestamp.
     * @param status HTTP status code
     * @param error Error type
     * @param message Error message
     * @param path Request path
     * @return ApiErrorDTO with current timestamp
     */
    public static ApiErrorDTO of(Integer status, String error, String message, String path) {
        return new ApiErrorDTO(LocalDateTime.now(), status, error, message, path);
    }
}

