package golden.raspberry.awards.adapter.driving.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;

import static golden.raspberry.awards.adapter.driving.rest.dto.MovieDTOValidator.ValidationResult;
import static golden.raspberry.awards.adapter.driving.rest.dto.MovieDTOValidator.validateAll;

/**
 * DTO for updating an existing movie (request body).
 * Using Java 21 record for immutability and extreme elegance.
 *
 * <p>Follows Richardson Level 2: structured request format.
 * Does not include ID (passed as path variable).
 *
 * <p>Uses Java 21 features EXTREMELY:
 * <ul>
 *   <li>Records with compact constructor</li>
 *   <li>String Templates for elegant error messages</li>
 *   <li>Sealed interfaces for validation results</li>
 *   <li>Pattern Matching for validation</li>
 *   <li>Stream API for functional validation</li>
 *   <li>Method references for clean code</li>
 * </ul>
 *
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
     * Compact constructor with EXTREME Java 21 elegance.
     * Uses sealed interfaces, pattern matching, and functional validation.
     *
     * @param year      Movie release year (non-null, 1900-2100)
     * @param title     Movie title (non-null, non-blank)
     * @param studios   Movie studios (non-null, non-blank)
     * @param producers Movie producers (non-null, non-blank)
     * @param winner    Whether the movie is a winner (non-null)
     * @throws IllegalArgumentException if validation fails
     */
    public UpdateMovieDTO {
        var validationResult = validateAll(year, title, studios, producers, winner);
        switch (validationResult) {
            case ValidationResult.Valid ignored -> {
                title = title.trim();
                studios = studios.trim();
                producers = producers.trim();
            }
            case ValidationResult.Invalid invalid -> throw new IllegalArgumentException(
                    "Validation failed: %s".formatted(invalid.message())
            );
        }
    }

}
