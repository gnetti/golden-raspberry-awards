package golden.raspberry.awards.core.application.exception;

/**
 * Exception thrown when a required field is missing (null or not provided).
 * Represents application-level validation errors for missing required fields.
 *
 * <p>This exception should be used when a required field is null or not provided.
 * It is a runtime exception for easier propagation through layers.
 *
 * <p>Uses Java 21 features: String Templates for error messages.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public class MissingFieldException extends InvalidFieldException {

    /**
     * Creates a new MissingFieldException.
     *
     * @param fieldName Name of the field that is missing
     */
    public MissingFieldException(String fieldName) {
        super(fieldName, "Field '%s' is required and cannot be null. Please provide a valid value for this field."
                .formatted(fieldName.toLowerCase()));
    }

    /**
     * Creates a new MissingFieldException with a custom message.
     *
     * @param fieldName Name of the field that is missing
     * @param message   Custom error message (en-US)
     */
    public MissingFieldException(String fieldName, String message) {
        super(fieldName, message);
    }
}

