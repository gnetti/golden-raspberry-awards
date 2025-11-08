package golden.raspberry.awards.adapter.driving.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * DTO for ProducerInterval in REST API.
 * Using Java 21 record for immutability.
 *
 * <p>Follows Richardson Level 2: structured response format.
 *
 * <p>Uses Java 21 features: Records, compact constructor for validation.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record ProducerIntervalDTO(
        @JsonProperty("producer")
        String producer,

        @JsonProperty("interval")
        Integer interval,

        @JsonProperty("previousWin")
        Integer previousWin,

        @JsonProperty("followingWin")
        Integer followingWin
) {
    /**
     * Compact constructor for validation.
     *
     * @param producer Producer name (non-null, non-blank)
     * @param interval Interval in years (non-null, non-negative)
     * @param previousWin Previous win year (non-null)
     * @param followingWin Following win year (non-null)
     * @throws IllegalArgumentException if validation fails
     */
    public ProducerIntervalDTO {
        Objects.requireNonNull(producer, "Producer cannot be null");
        Objects.requireNonNull(interval, "Interval cannot be null");
        Objects.requireNonNull(previousWin, "PreviousWin cannot be null");
        Objects.requireNonNull(followingWin, "FollowingWin cannot be null");

        if (producer.isBlank()) {
            throw new IllegalArgumentException("Producer cannot be blank");
        }
        if (interval < 0) {
            throw new IllegalArgumentException("Interval must be non-negative, but was: %d".formatted(interval));
        }
    }
}

