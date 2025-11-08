package golden.raspberry.awards.core.domain.exception;

/**
 * Base exception for validation errors in Domain layer.
 * Represents business rule violations.
 *
 * <p>This exception should be used when domain validation fails.
 * It is a checked exception to force explicit handling.
 *
 * <p>Uses Java 21 features: Records for immutability.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public class ValidationException extends Exception {

    private final String fieldName;
    private final String message;

    /**
     * Creates a new ValidationException.
     *
     * @param fieldName Name of the field that failed validation
     * @param message   Validation error message (en-US)
     */
    public ValidationException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
        this.message = message;
    }

    /**
     * Creates a new ValidationException with a cause.
     *
     * @param fieldName Name of the field that failed validation
     * @param message   Validation error message (en-US)
     * @param cause     The cause of this exception
     */
    public ValidationException(String fieldName, String message, Throwable cause) {
        super(message, cause);
        this.fieldName = fieldName;
        this.message = message;
    }

    /**
     * Gets the field name that failed validation.
     *
     * @return Field name
     */
    public String getFieldName() {
        return fieldName;
    }

    @Override
    public String getMessage() {
        return message;
    }
}

