package golden.raspberry.awards.core.application.exception;

/**
 * Exception thrown when a field has an invalid length (min/max constraints).
 * Represents application-level validation errors for string length violations.
 *
 * <p>This exception should be used when a string field violates length constraints.
 * It is a runtime exception for easier propagation through layers.
 *
 * <p>Uses Java 21 features: String Templates for error messages.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public class InvalidFieldLengthException extends InvalidFieldException {

    private final int minLength;
    private final int maxLength;
    private final int actualLength;

    /**
     * Creates a new InvalidFieldLengthException.
     *
     * @param fieldName   Name of the field with invalid length
     * @param minLength   Minimum allowed length (inclusive)
     * @param maxLength   Maximum allowed length (inclusive)
     * @param actualLength Actual length of the field value
     */
    public InvalidFieldLengthException(String fieldName, int minLength, int maxLength, int actualLength) {
        super(fieldName, "Field '%s' must have between %d and %d character(s) (after removing leading/trailing spaces), but had: %d. Please provide a value within the allowed length."
                .formatted(fieldName.toLowerCase(), minLength, maxLength, actualLength));
        this.minLength = minLength;
        this.maxLength = maxLength;
        this.actualLength = actualLength;
    }

    /**
     * Gets the minimum allowed length.
     *
     * @return Minimum length
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * Gets the maximum allowed length.
     *
     * @return Maximum length
     */
    public int getMaxLength() {
        return maxLength;
    }

    /**
     * Gets the actual length of the field value.
     *
     * @return Actual length
     */
    public int getActualLength() {
        return actualLength;
    }
}

