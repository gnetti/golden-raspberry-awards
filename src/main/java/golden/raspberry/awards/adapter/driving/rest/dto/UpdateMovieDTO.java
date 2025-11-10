package golden.raspberry.awards.adapter.driving.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import java.util.Optional;

/**
 * DTO for updating an existing movie (request body). *
 * <p>Follows Richardson Level 2: structured request format.
 * Does not include ID (passed as path variable).
 * *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record UpdateMovieDTO(
        @JsonProperty("year")
        @NotNull(message = "Field 'year' is required and cannot be null")
        @Min(value = 1900, message = "Field 'year' must be at least 1900")
        Integer year,

        @JsonProperty("title")
        @NotNull(message = "Field 'title' is required and cannot be null")
        @NotBlank(message = "Field 'title' cannot be empty or contain only whitespace")
        @Size(min = 2, max = 255, message = "Field 'title' must be between 2 and 255 characters")
        String title,

        @JsonProperty("studios")
        @NotNull(message = "Field 'studios' is required and cannot be null")
        @NotBlank(message = "Field 'studios' cannot be empty or contain only whitespace")
        @Size(min = 2, max = 255, message = "Field 'studios' must be between 2 and 255 characters")
        String studios,

        @JsonProperty("producers")
        @NotNull(message = "Field 'producers' is required and cannot be null")
        @NotBlank(message = "Field 'producers' cannot be empty or contain only whitespace")
        @Size(min = 2, max = 255, message = "Field 'producers' must be between 2 and 255 characters")
        String producers,

        @JsonProperty("winner")
        @NotNull(message = "Field 'winner' is required and cannot be null")
        Boolean winner
) {
    /**
     * Trims string fields automatically.
     *
     * @param year      Movie release year (validated by Jakarta Validation)
     * @param title     Movie title (validated by Jakarta Validation)
     * @param studios   Movie studios (validated by Jakarta Validation)
     * @param producers Movie producers (validated by Jakarta Validation)
     * @param winner    Whether the movie is a winner (validated by Jakarta Validation)
     */
    public UpdateMovieDTO {
        title = Optional.ofNullable(title).map(String::trim).orElse(title);
        studios = Optional.ofNullable(studios).map(String::trim).orElse(studios);
        producers = Optional.ofNullable(producers).map(String::trim).orElse(producers);
    }

}
