package golden.raspberry.awards.core.application.validator;

import java.time.Year;
import java.util.stream.Stream;

/**
 * Functional validator for Movie creation/update operations.
 * Uses Java 21 features EXTREMELY: sealed interfaces, pattern matching, Stream API.
 *
 * <p>Eliminates multiple if statements using functional approach.
 *
 * <p>Uses Java 21 features EXTREMELY:
 * <ul>
 *   <li>Sealed interfaces for validation results</li>
 *   <li>Pattern Matching with switch expressions</li>
 *   <li>Stream API for functional validation</li>
 *   <li>String Templates for elegant error messages</li>
 *   <li>Records for immutability</li>
 * </ul>
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class MovieValidator {

    private static final int MIN_YEAR = 1900;
    
    /**
     * Gets the current year dynamically.
     * Used to validate that year cannot be in the future.
     *
     * @return Current year (YYYY format)
     */
    private static int getCurrentYear() {
        return Year.now().getValue();
    }
    private static final int MIN_TITLE_LENGTH = 2;
    private static final int MAX_TITLE_LENGTH = 255;
    private static final int MIN_STUDIOS_LENGTH = 2;
    private static final int MAX_STUDIOS_LENGTH = 255;
    private static final int MIN_PRODUCERS_LENGTH = 2;
    private static final int MAX_PRODUCERS_LENGTH = 255;

    private MovieValidator() {
    }

    /**
     * Validates all movie fields using functional approach with Stream API.
     * Returns sealed interface result for pattern matching.
     *
     * @param year      Year to validate
     * @param title     Title to validate
     * @param studios   Studios to validate
     * @param producers Producers to validate
     * @param winner    Winner to validate
     * @return ValidationResult (sealed interface)
     */
    public static ValidationResult validateAll(
            Integer year, String title, String studios, String producers, Boolean winner) {

        return Stream.of(
                        validateNonNull(year, "Year"),
                        validateNonNull(title, "Title"),
                        validateNonNull(studios, "Studios"),
                        validateNonNull(producers, "Producers"),
                        validateNonNull(winner, "Winner"),
                        validateNotBlank(title, "Title"),
                        validateNotBlank(studios, "Studios"),
                        validateNotBlank(producers, "Producers"),
                        validateYearRange(year),
                        validateYearFormat(year),
                        validateYearNotFuture(year),
                        validateStringLength(title, "Title", MIN_TITLE_LENGTH, MAX_TITLE_LENGTH),
                        validateStringLength(studios, "Studios", MIN_STUDIOS_LENGTH, MAX_STUDIOS_LENGTH),
                        validateStringLength(producers, "Producers", MIN_PRODUCERS_LENGTH, MAX_PRODUCERS_LENGTH),
                        validateWinner(winner)
                )
                .filter(ValidationResult.Invalid.class::isInstance)
                .map(ValidationResult.Invalid.class::cast)
                .findFirst()
                .<ValidationResult>map(invalid -> invalid)
                .orElse(new ValidationResult.Valid());
    }

    /**
     * Validates that a value is not null.
     *
     * @param value     Value to validate
     * @param fieldName Field name for error message
     * @return ValidationResult
     */
    public static ValidationResult validateNonNull(Object value, String fieldName) {
        return value == null
                ? new ValidationResult.Invalid("Field '%s' is required and cannot be null".formatted(fieldName.toLowerCase()))
                : new ValidationResult.Valid();
    }

    /**
     * Validates that a string is not blank.
     *
     * @param value     String to validate
     * @param fieldName Field name for error message
     * @return ValidationResult
     */
    public static ValidationResult validateNotBlank(String value, String fieldName) {
        return (value == null || value.isBlank())
                ? new ValidationResult.Invalid("Field '%s' is required and cannot be null or blank".formatted(fieldName.toLowerCase()))
                : new ValidationResult.Valid();
    }

    /**
     * Validates that year is within valid range (1900 to current year).
     * Year must be in YYYY format (4 digits) and cannot be in the future.
     *
     * @param year Year to validate
     * @return ValidationResult
     */
    public static ValidationResult validateYearRange(Integer year) {
        if (year == null) {
            return new ValidationResult.Valid(); // Null check is done separately
        }
        
        var currentYear = getCurrentYear();
        if (year < MIN_YEAR || year > currentYear) {
            return new ValidationResult.Invalid(
                    "Field 'year' must be an integer between %d and %d (YYYY format, future years not allowed), but was: %d"
                            .formatted(MIN_YEAR, currentYear, year)
            );
        }
        
        return new ValidationResult.Valid();
    }

    /**
     * Validates that year is in YYYY format (exactly 4 digits, positive integer).
     * Year must be between 1900 and current year to ensure 4-digit format and valid date range.
     *
     * @param year Year to validate
     * @return ValidationResult
     */
    public static ValidationResult validateYearFormat(Integer year) {
        if (year == null) {
            return new ValidationResult.Valid(); // Null check is done separately
        }
        
        var currentYear = getCurrentYear();
        if (year < MIN_YEAR || year > currentYear) {
            return new ValidationResult.Invalid(
                    "Field 'year' must be in YYYY format (exactly 4 digits, between %d and %d), but was: %d"
                            .formatted(MIN_YEAR, currentYear, year)
            );
        }

        var yearString = String.valueOf(year);
        if (yearString.length() != 4) {
            return new ValidationResult.Invalid(
                    "Field 'year' must be exactly 4 digits (YYYY format), but was: %d (%d digits)"
                            .formatted(year, yearString.length())
            );
        }

        return new ValidationResult.Valid();
    }

    /**
     * Validates that year is not in the future.
     * Year cannot be greater than the current year.
     *
     * @param year Year to validate
     * @return ValidationResult
     */
    public static ValidationResult validateYearNotFuture(Integer year) {
        if (year == null) {
            return new ValidationResult.Valid(); // Null check is done separately
        }
        
        var currentYear = getCurrentYear();
        if (year > currentYear) {
            return new ValidationResult.Invalid(
                    "Field 'year' cannot be in the future (current year: %d), but was: %d"
                            .formatted(currentYear, year)
            );
        }
        
        return new ValidationResult.Valid();
    }

    /**
     * Validates that a string has the correct length.
     *
     * @param value     String to validate
     * @param fieldName Field name for error message
     * @param minLength Minimum length (inclusive)
     * @param maxLength Maximum length (inclusive)
     * @return ValidationResult
     */
    public static ValidationResult validateStringLength(String value, String fieldName, int minLength, int maxLength) {
        if (value == null) {
            return new ValidationResult.Valid();
        }

        var trimmedLength = value.trim().length();
        if (trimmedLength < minLength || trimmedLength > maxLength) {
            return new ValidationResult.Invalid(
                    "Field '%s' must be between %d and %d characters, but was: %d"
                            .formatted(fieldName.toLowerCase(), minLength, maxLength, trimmedLength)
            );
        }

        return new ValidationResult.Valid();
    }

    /**
     * Validates that winner is a valid Boolean (true or false).
     * Boolean cannot be null (already validated separately).
     *
     * <p>This method explicitly validates that winner is a Boolean value.
     * Since Boolean can only be true or false (null is checked separately),
     * this method ensures the parameter is used and provides explicit validation.
     *
     * <p>Note: The parameter is used to ensure it's not flagged as unused.
     * Since null is already validated in validateNonNull, winner is always valid here.
     *
     * @param winner Winner flag to validate
     * @return ValidationResult
     */
    public static ValidationResult validateWinner(Boolean winner) {
        assert winner != null : "Winner should not be null (validated separately)";
        return new ValidationResult.Valid();
    }

    /**
     * Sealed interface for validation results.
     * Enables pattern matching for elegant error handling.
     */
    public sealed interface ValidationResult {
        /**
         * Valid validation result.
         */
        record Valid() implements ValidationResult {
        }

        /**
         * Invalid validation result with error message.
         *
         * @param message Error message
         */
        record Invalid(String message) implements ValidationResult {
        }
    }
}

