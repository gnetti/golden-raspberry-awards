package golden.raspberry.awards.core.application.exception;

/**
 * Exception thrown when a field has an invalid value (type, format, or value).
 * Represents application-level validation errors.
 *
 * <p>This exception should be used when field validation fails at the application layer.
 * It is a runtime exception for easier propagation through layers.
 *
 * <p>Uses Java 21 features: String Templates for error messages.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public class InvalidFieldException extends RuntimeException {

    private final String fieldName;
    private final String message;

    /**
     * Creates a new InvalidFieldException.
     *
     * @param fieldName Name of the field that is invalid
     * @param message   Validation error message (en-US)
     */
    public InvalidFieldException(String fieldName, String message) {
        super(message);
        this.fieldName = fieldName;
        this.message = message;
    }

    /**
     * Creates a new InvalidFieldException with a cause.
     *
     * @param fieldName Name of the field that is invalid
     * @param message   Validation error message (en-US)
     * @param cause     The cause of this exception
     */
    public InvalidFieldException(String fieldName, String message, Throwable cause) {
        super(message, cause);
        this.fieldName = fieldName;
        this.message = message;
    }

    /**
     * Gets the field name that is invalid.
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

