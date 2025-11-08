package golden.raspberry.awards.adapter.driving.rest.dto;

import golden.raspberry.awards.core.application.validator.MovieValidator;

/**
 * DTO Validator that delegates to the ABSOLUTE MovieValidator.
 * Provides a thin adapter layer for DTO validation.
 *
 * <p>Uses Java 21 features EXTREMELY:
 * <ul>
 *   <li>Delegation pattern for code reuse</li>
 *   <li>Sealed interfaces for validation results</li>
 *   <li>Pattern Matching for validation</li>
 * </ul>
 *
 * <p><strong>ABSOLUTE VALIDATION:</strong>
 * All validation logic is delegated to MovieValidator, which provides
 * ABSOLUTE coverage of all possible invalid input variations.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class MovieDTOValidator {

    private MovieDTOValidator() {
    }

    /**
     * Validates all movie fields using ABSOLUTE MovieValidator.
     * Returns sealed interface result for pattern matching.
     *
     * @param year      Year to validate
     * @param title     Title to validate
     * @param studios   Studios to validate
     * @param producers Producers to validate
     * @param winner    Winner to validate
     * @return ValidationResult (sealed interface) - delegates to MovieValidator
     */
    public static ValidationResult validateAll(
            Integer year, String title, String studios, String producers, Boolean winner) {
        
        // Delegate to ABSOLUTE MovieValidator
        var result = MovieValidator.validateAll(year, title, studios, producers, winner);
        
        // Convert MovieValidator.ValidationResult to MovieDTOValidator.ValidationResult
        return switch (result) {
            case MovieValidator.ValidationResult.Valid ignored -> new ValidationResult.Valid();
            case MovieValidator.ValidationResult.Invalid invalid -> new ValidationResult.Invalid(invalid.message());
        };
    }

    /**
     * Sealed interface for validation results.
     * Enables pattern matching for elegant error handling.
     * Mirrors MovieValidator.ValidationResult for consistency.
     */
    public sealed interface ValidationResult {
        /**
         * Valid validation result.
         * All validations passed successfully.
         */
        record Valid() implements ValidationResult {
        }

        /**
         * Invalid validation result with FUNDAMENTALS error message.
         * Message explains exactly what is wrong and how to fix it.
         *
         * @param message Error message (clear, actionable, and informative)
         */
        record Invalid(String message) implements ValidationResult {
        }
    }
}
