package golden.raspberry.awards.core.application.exception;

import java.time.Year;

/**
 * Exception thrown when a year field has an invalid format or is in the future.
 * Represents application-level validation errors for year format violations.
 *
 * <p>This exception should be used when:
 * <ul>
 *   <li>Year is not in YYYY format (exactly 4 digits)</li>
 *   <li>Year is outside valid range (1900 to current year)</li>
 *   <li>Year is in the future</li>
 * </ul>
 *
 * <p>It is a runtime exception for easier propagation through layers.
 *
 * <p>Uses Java 21 features: String Templates for error messages.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public class InvalidYearFormatException extends InvalidFieldException {

    private final Integer year;
    private final int currentYear;

    /**
     * Creates a new InvalidYearFormatException for invalid format.
     *
     * @param fieldName Name of the year field
     * @param year      The invalid year value
     * @param reason    Reason for invalidity (e.g., "not 4 digits", "in the future", "out of range")
     */
    public InvalidYearFormatException(String fieldName, Integer year, String reason) {
        super(fieldName, "Field '%s' has invalid year format: %s. Year was: %d. Please provide a valid year (YYYY format, between 1900 and %d)."
                .formatted(fieldName.toLowerCase(), reason, year, Year.now().getValue()));
        this.year = year;
        this.currentYear = Year.now().getValue();
    }

    /**
     * Creates a new InvalidYearFormatException for future year.
     *
     * @param fieldName Name of the year field
     * @param year      The future year value
     */
    public InvalidYearFormatException(String fieldName, Integer year) {
        super(fieldName, "Field '%s' cannot be in the future. Current year is %d, but provided year was: %d. Please provide a year from 1900 to %d."
                .formatted(fieldName.toLowerCase(), Year.now().getValue(), year, Year.now().getValue()));
        this.year = year;
        this.currentYear = Year.now().getValue();
    }

    /**
     * Gets the invalid year value.
     *
     * @return Year value
     */
    public Integer getYear() {
        return year;
    }

    /**
     * Gets the current year.
     *
     * @return Current year
     */
    public int getCurrentYear() {
        return currentYear;
    }
}

