package golden.raspberry.awards.adapter.driving.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

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
@Schema(
        description = "Data Transfer Object for producer interval response containing min and max intervals",
        example = """
                {
                  "min": [
                    {
                      "producer": "Joel Silver",
                      "interval": 1,
                      "previousWin": 1990,
                      "followingWin": 1991
                    }
                  ],
                  "max": [
                    {
                      "producer": "Matthew Vaughn",
                      "interval": 13,
                      "previousWin": 2002,
                      "followingWin": 2015
                    }
                  ]
                }
                """
)
public record ProducerIntervalResponseDTO(
        @JsonProperty("min")
        @Schema(description = "List of producers with minimum interval between consecutive wins")
        List<ProducerIntervalDTO> min,

        @JsonProperty("max")
        @Schema(description = "List of producers with maximum interval between consecutive wins")
        List<ProducerIntervalDTO> max
) {
    public ProducerIntervalResponseDTO {
        Objects.requireNonNull(min, "Min list cannot be null");
        Objects.requireNonNull(max, "Max list cannot be null");
    }
}

