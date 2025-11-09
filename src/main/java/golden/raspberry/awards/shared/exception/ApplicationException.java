package golden.raspberry.awards.shared.exception;

/**
 * Base exception for application layer errors.
 * Represents application-level violations.
 *
 * <p>This exception is thrown when application-level rules are violated,
 * such as validation failures or orchestration errors.
 *
 * <p>Uses Java 21 features: Standard exception handling.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public class ApplicationException extends RuntimeException {

    /**
     * Constructs a new application exception with the specified message.
     *
     * @param message Error message
     */
    public ApplicationException(String message) {
        super(message);
    }

    /**
     * Constructs a new application exception with the specified message and cause.
     *
     * @param message Error message
     * @param cause Cause of the exception
     */
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}

