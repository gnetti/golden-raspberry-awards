package golden.raspberry.awards.core.domain.model.aggregate;


import jakarta.validation.constraints.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Domain model for Movie with ID.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record MovieWithId(
        @NotNull Long id,
        @NotNull Integer year,
        @NotNull String title,
        @NotNull String studios,
        @NotNull String producers,
        @NotNull Boolean winner
) {
    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR = 2100;

    /**
     * Compact constructor with validation.
     *
     * @param id        Movie ID
     * @param year      Movie release year
     * @param title     Movie title
     * @param studios   Movie studios
     * @param producers Movie producers
     * @param winner    Whether the movie is a winner
     * @throws IllegalArgumentException if validation fails
     */
    public MovieWithId {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(year, "Year cannot be null");
        Objects.requireNonNull(title, "Title cannot be null");
        Objects.requireNonNull(studios, "Studios cannot be null");
        Objects.requireNonNull(producers, "Producers cannot be null");
        Objects.requireNonNull(winner, "Winner cannot be null");

        validateNotBlank(title, "Title");
        validateNotBlank(studios, "Studios");
        validateNotBlank(producers, "Producers");
        validateYearRange(year);

        title = title.trim();
        studios = studios.trim();
        producers = producers.trim();
    }

    /**
     * Validates that a string field is not blank.
     *
     * @param value     String value to validate
     * @param fieldName Field name for error message
     * @throws IllegalArgumentException if value is blank
     */
    private static void validateNotBlank(String value, String fieldName) {
        Optional.of(value)
                .filter(Predicate.not(String::isBlank))
                .orElseThrow(() -> new IllegalArgumentException("%s cannot be blank".formatted(fieldName)));
    }

    /**
     * Validates that year is within valid range.
     *
     * @param year Year to validate
     * @throws IllegalArgumentException if year is out of range
     */
    private static void validateYearRange(Integer year) {
        Optional.of(year)
                .filter(y -> y >= MIN_YEAR && y <= MAX_YEAR)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Year must be between %d and %d, but was: %d".formatted(MIN_YEAR, MAX_YEAR, year)
                ));
    }
}

