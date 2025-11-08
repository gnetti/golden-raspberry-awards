package golden.raspberry.awards.adapter.driving.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * DTO for API error responses.
 * Using Java 21 record for immutability.
 *
 * <p>Follows Richardson Level 2: structured and informative error messages.
 *
 * <p>Uses Java 21 features: Records, String Templates, static factory method.
 *
 * @param timestamp Timestamp when the error occurred
 * @param status HTTP status code
 * @param error Error type
 * @param message Error message
 * @param path Request path that caused the error
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record ApiErrorDTO(
        @JsonProperty("timestamp")
        LocalDateTime timestamp,

        @JsonProperty("status")
        Integer status,

        @JsonProperty("error")
        String error,

        @JsonProperty("message")
        String message,

        @JsonProperty("path")
        String path
) {
    /**
     * Compact constructor for validation.
     *
     * @param timestamp Timestamp (non-null)
     * @param status HTTP status code (non-null)
     * @param error Error type (non-null, non-blank)
     * @param message Error message (non-null, non-blank)
     * @param path Request path (non-null)
     * @throws IllegalArgumentException if validation fails
     */
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
     *
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

