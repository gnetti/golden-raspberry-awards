package golden.raspberry.awards.shared.exception;

/**
 * Base exception for infrastructure layer errors.
 * Represents technical failures.
 *
 * <p>This exception is thrown when infrastructure-level errors occur,
 * such as database connection failures or file I/O errors.
 *
 * <p>Uses Java 21 features: Standard exception handling.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public class InfrastructureException extends RuntimeException {

    /**
     * Constructs a new infrastructure exception with the specified message.
     *
     * @param message Error message
     */
    public InfrastructureException(String message) {
        super(message);
    }

    /**
     * Constructs a new infrastructure exception with the specified message and cause.
     *
     * @param message Error message
     * @param cause Cause of the exception
     */
    public InfrastructureException(String message, Throwable cause) {
        super(message, cause);
    }
}

