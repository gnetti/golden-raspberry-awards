package golden.raspberry.awards.core.application.usecase.validation;

import java.time.Year;
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
    private static final int MIN_STRING_LENGTH = 2;
    private static final int MAX_STRING_LENGTH = 255;

    private MovieValidation() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    /**
     * Gets the current year.
     *
     * @return Current year
     */
    private static int getCurrentYear() {
        return Year.now().getValue();
    }

    /**
     * Validates year is not null and within valid range.
     * Year must be between 1900 and the current year (no future years allowed).
     * <p>
     * Uses Java 21 functional programming with Optional and Predicates for robust validation.
     *
     * @param year Year to validate
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateYear(Integer year) {
        if (year == null) {
            throw new IllegalArgumentException("Year cannot be null");
        }
        
        var currentYear = getCurrentYear();
        var validationContext = new YearValidationContext(year, MIN_YEAR, currentYear);
        
        validateYearRange(validationContext);
        validateYearNotInFuture(validationContext);
    }

    /**
     * Validates that year is not below minimum allowed year.
     *
     * @param context Validation context with year and constraints
     * @throws IllegalArgumentException if year is below minimum
     */
    private static void validateYearRange(YearValidationContext context) {
        Optional.of(context.year())
                .filter(year -> year >= context.minYear())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Year must be at least %d, but was: %d".formatted(
                                context.minYear(), context.year()
                        )
                ));
    }

    /**
     * Validates that year is not in the future.
     *
     * @param context Validation context with year and current year
     * @throws IllegalArgumentException if year is in the future
     */
    private static void validateYearNotInFuture(YearValidationContext context) {
        Optional.of(context.year())
                .filter(year -> year <= context.currentYear())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Year cannot be in the future. Maximum allowed year is %d (current year), but was: %d".formatted(
                                context.currentYear(), context.year()
                        )
                ));
    }

    /**
     * Record encapsulating year validation context.
     *
     * @param year        Year to validate
     * @param minYear     Minimum allowed year
     * @param currentYear Current year (maximum allowed)
     */
    private record YearValidationContext(
            int year,
            int minYear,
            int currentYear
    ) {}

    /**
     * Validates title is not null, not blank, and within valid length.
     *
     * @param title Title to validate
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateTitle(String title) {
        if (title == null) {
            throw new IllegalArgumentException("Title cannot be null");
        }
        var trimmed = title.trim();
        Optional.of(trimmed)
                .filter(Predicate.not(String::isBlank))
                .orElseThrow(() -> new IllegalArgumentException("Title cannot be blank"));

        Optional.of(trimmed.length())
                .filter(length -> length >= MIN_STRING_LENGTH)
                .filter(length -> length <= MAX_STRING_LENGTH)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Title must be between %d and %d characters, but was: %d".formatted(
                                MIN_STRING_LENGTH, MAX_STRING_LENGTH, trimmed.length()
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
        if (studios == null) {
            throw new IllegalArgumentException("Studios cannot be null");
        }
        var trimmed = studios.trim();
        Optional.of(trimmed)
                .filter(Predicate.not(String::isBlank))
                .orElseThrow(() -> new IllegalArgumentException("Studios cannot be blank"));

        Optional.of(trimmed.length())
                .filter(length -> length >= MIN_STRING_LENGTH)
                .filter(length -> length <= MAX_STRING_LENGTH)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Studios must be between %d and %d characters, but was: %d".formatted(
                                MIN_STRING_LENGTH, MAX_STRING_LENGTH, trimmed.length()
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
        if (producers == null) {
            throw new IllegalArgumentException("Producers cannot be null");
        }
        var trimmed = producers.trim();
        Optional.of(trimmed)
                .filter(Predicate.not(String::isBlank))
                .orElseThrow(() -> new IllegalArgumentException("Producers cannot be blank"));

        Optional.of(trimmed.length())
                .filter(length -> length >= MIN_STRING_LENGTH)
                .filter(length -> length <= MAX_STRING_LENGTH)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Producers must be between %d and %d characters, but was: %d".formatted(
                                MIN_STRING_LENGTH, MAX_STRING_LENGTH, trimmed.length()
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
        if (winner == null) {
            throw new IllegalArgumentException("Winner cannot be null");
        }
    }

    /**
     * Validates ID is not null and positive.
     *
     * @param id ID to validate
     * @throws IllegalArgumentException if validation fails
     */
    public static void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }
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

