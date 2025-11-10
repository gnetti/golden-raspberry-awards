package golden.raspberry.awards.adapter.driving.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

/**
 * DTO for ProducerInterval in REST API. *
 * <p>Follows Richardson Level 2: structured response format.
 * *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Schema(description = "Data Transfer Object for producer interval information")
public record ProducerIntervalDTO(
        @JsonProperty("producer")
        @Schema(description = "Producer name", example = "Joel Silver")
        String producer,

        @JsonProperty("interval")
        @Schema(description = "Interval in years between consecutive wins", example = "1", minimum = "0")
        Integer interval,

        @JsonProperty("previousWin")
        @Schema(description = "Year of the previous win", example = "1990")
        Integer previousWin,

        @JsonProperty("followingWin")
        @Schema(description = "Year of the following win", example = "1991")
        Integer followingWin
) {
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

