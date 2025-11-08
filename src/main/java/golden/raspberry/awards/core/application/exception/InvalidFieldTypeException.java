package golden.raspberry.awards.core.application.exception;

/**
 * Exception thrown when a field has an invalid type (e.g., string where integer expected).
 * Represents application-level validation errors for type mismatches.
 *
 * <p>This exception should be used when a field has the wrong data type.
 * It is a runtime exception for easier propagation through layers.
 *
 * <p>Uses Java 21 features: String Templates for error messages.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public class InvalidFieldTypeException extends InvalidFieldException {

    private final String expectedType;
    private final String actualType;

    /**
     * Creates a new InvalidFieldTypeException.
     *
     * @param fieldName   Name of the field with invalid type
     * @param expectedType Expected type (e.g., "Integer", "String", "Boolean")
     * @param actualType   Actual type received
     */
    public InvalidFieldTypeException(String fieldName, String expectedType, String actualType) {
        super(fieldName, "Field '%s' must be of type %s, but received %s. Please provide a valid %s value."
                .formatted(fieldName.toLowerCase(), expectedType, actualType, expectedType));
        this.expectedType = expectedType;
        this.actualType = actualType;
    }

    /**
     * Gets the expected type.
     *
     * @return Expected type
     */
    public String getExpectedType() {
        return expectedType;
    }

    /**
     * Gets the actual type received.
     *
     * @return Actual type
     */
    public String getActualType() {
        return actualType;
    }
}

