package golden.raspberry.awards.adapter.driving.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

/**
 * DTO for ProducerIntervalResponse in REST API. *
 * <p>Follows Richardson Level 2: structured response format.
 * Matches the specification format:
 * <pre>
 * {
 *   "min": [...],
 *   "max": [...]
 * }
 * </pre>
 * *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record ProducerIntervalResponseDTO(
        @JsonProperty("min")
        List<ProducerIntervalDTO> min,

        @JsonProperty("max")
        List<ProducerIntervalDTO> max
) {
    /**
     * Compact constructor for validation.
     * @param min List of minimum intervals (non-null)
     * @param max List of maximum intervals (non-null)
     * @throws IllegalArgumentException if validation fails
     */
    public ProducerIntervalResponseDTO {
        Objects.requireNonNull(min, "Min list cannot be null");
        Objects.requireNonNull(max, "Max list cannot be null");
    }
}

