package golden.raspberry.awards.adapter.driving.rest.dto;

import java.util.stream.Stream;

/**
 * Utility class for Movie DTO validation.
 * Provides shared validation methods to eliminate code duplication.
 *
 * <p>Uses Java 21 features EXTREMELY:
 * <ul>
 *   <li>Sealed interfaces for validation results</li>
 *   <li>Pattern Matching for validation</li>
 *   <li>Stream API for functional validation</li>
 *   <li>String Templates for elegant error messages</li>
 * </ul>
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class MovieDTOValidator {

    private MovieDTOValidator() {
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

        var validationResults = Stream.of(
                validateNonNull(year, "Year"),
                validateNonNull(title, "Title"),
                validateNonNull(studios, "Studios"),
                validateNonNull(producers, "Producers"),
                validateNonNull(winner, "Winner"),
                validateNotBlank(title, "Title"),
                validateNotBlank(studios, "Studios"),
                validateNotBlank(producers, "Producers"),
                validateYearRange(year)
        );

        return processValidationResults(validationResults);
    }

    /**
     * Processes a stream of validation results and returns the first invalid result,
     * or a valid result if all validations pass.
     *
     * <p>This auxiliary method eliminates code duplication in validation processing.
     *
     * @param validationResults Stream of validation results
     * @return First invalid result, or Valid if all pass
     */
    private static ValidationResult processValidationResults(Stream<ValidationResult> validationResults) {
        return validationResults
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
                ? new ValidationResult.Invalid("%s cannot be null".formatted(fieldName))
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
                ? new ValidationResult.Invalid("%s cannot be blank".formatted(fieldName))
                : new ValidationResult.Valid();
    }

    /**
     * Validates that year is within valid range (1900-2100).
     *
     * @param year Year to validate
     * @return ValidationResult
     */
    public static ValidationResult validateYearRange(Integer year) {
        return (year != null && (year < 1900 || year > 2100))
                ? new ValidationResult.Invalid(
                "Year must be between 1900 and 2100, but was: %d".formatted(year)
        )
                : new ValidationResult.Valid();
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

