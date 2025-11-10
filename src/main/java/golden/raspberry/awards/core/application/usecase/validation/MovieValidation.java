package golden.raspberry.awards.core.application.usecase.validation;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Validation utility for Movie Use Cases.
 * Protects Domain from invalid data.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class MovieValidation {

    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR = 2100;
    private static final int MIN_STRING_LENGTH = 1;
    private static final int MAX_TITLE_LENGTH = 500;
    private static final int MAX_STUDIOS_LENGTH = 500;
    private static final int MAX_PRODUCERS_LENGTH = 1000;

    private MovieValidation() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    /**
     * Validates year is not null and within valid range.
     *
     * @param year Year to validate
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateYear(Integer year) {
        Objects.requireNonNull(year, "Year cannot be null");
        Optional.of(year)
                .filter(y -> y >= MIN_YEAR && y <= MAX_YEAR)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Year must be between %d and %d, but was: %d".formatted(MIN_YEAR, MAX_YEAR, year)
                ));
    }

    /**
     * Validates title is not null, not blank, and within valid length.
     *
     * @param title Title to validate
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateTitle(String title) {
        Objects.requireNonNull(title, "Title cannot be null");
        var trimmed = title.trim();
        Optional.of(trimmed)
                .filter(Predicate.not(String::isBlank))
                .orElseThrow(() -> new IllegalArgumentException("Title cannot be blank"));

        Optional.of(trimmed.length())
                .filter(length -> length >= MIN_STRING_LENGTH && length <= MAX_TITLE_LENGTH)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Title must be between %d and %d characters, but was: %d".formatted(
                                MIN_STRING_LENGTH, MAX_TITLE_LENGTH, trimmed.length()
                        )
                ));
    }

    /**
     * Validates studios is not null, not blank, and within valid length.
     *
     * @param studios Studios to validate
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateStudios(String studios) {
        Objects.requireNonNull(studios, "Studios cannot be null");
        var trimmed = studios.trim();
        Optional.of(trimmed)
                .filter(Predicate.not(String::isBlank))
                .orElseThrow(() -> new IllegalArgumentException("Studios cannot be blank"));

        Optional.of(trimmed.length())
                .filter(length -> length >= MIN_STRING_LENGTH && length <= MAX_STUDIOS_LENGTH)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Studios must be between %d and %d characters, but was: %d".formatted(
                                MIN_STRING_LENGTH, MAX_STUDIOS_LENGTH, trimmed.length()
                        )
                ));
    }

    /**
     * Validates producers is not null, not blank, and within valid length.
     *
     * @param producers Producers to validate
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateProducers(String producers) {
        Objects.requireNonNull(producers, "Producers cannot be null");
        var trimmed = producers.trim();
        Optional.of(trimmed)
                .filter(Predicate.not(String::isBlank))
                .orElseThrow(() -> new IllegalArgumentException("Producers cannot be blank"));

        Optional.of(trimmed.length())
                .filter(length -> length >= MIN_STRING_LENGTH && length <= MAX_PRODUCERS_LENGTH)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Producers must be between %d and %d characters, but was: %d".formatted(
                                MIN_STRING_LENGTH, MAX_PRODUCERS_LENGTH, trimmed.length()
                        )
                ));
    }

    /**
     * Validates winner is not null.
     *
     * @param winner Winner flag to validate
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateWinner(Boolean winner) {
        Objects.requireNonNull(winner, "Winner cannot be null");
    }

    /**
     * Validates ID is not null and positive.
     *
     * @param id ID to validate
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateId(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        Optional.of(id)
                .filter(Predicate.not(i -> i <= 0))
                .orElseThrow(() -> new IllegalArgumentException(
                        "ID must be positive, but was: %d".formatted(id)
                ));
    }

    /**
     * Validates all movie fields for creation.
     *
     * @param year      Year to validate
     * @param title     Title to validate
     * @param studios   Studios to validate
     * @param producers Producers to validate
     * @param winner    Winner flag to validate
     * @throws IllegalArgumentException if any validation fails
     */
    public static void validateMovieData(Integer year, String title, String studios, String producers, Boolean winner) {
        validateYear(year);
        validateTitle(title);
        validateStudios(studios);
        validateProducers(producers);
        validateWinner(winner);
    }

    /**
     * Validates all movie fields for update.
     *
     * @param id        ID to validate
     * @param year      Year to validate
     * @param title     Title to validate
     * @param studios   Studios to validate
     * @param producers Producers to validate
     * @param winner    Winner flag to validate
     * @throws IllegalArgumentException if any validation fails
     */
    public static void validateMovieUpdateData(Long id, Integer year, String title, String studios, String producers, Boolean winner) {
        validateId(id);
        validateMovieData(year, title, studios, producers, winner);
    }
}

