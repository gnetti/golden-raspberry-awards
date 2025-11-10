package golden.raspberry.awards.shared.exception;

/**
 * Base exception for domain layer errors.
 * Represents business rule violations.
 * <p>This exception is thrown when domain business rules are violated.
 * It is part of the shared kernel and can be used across layers.
 * *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public class DomainException extends RuntimeException {

    /**
     * Constructs a new domain exception with the specified message.
     * @param message Error message
     */
    public DomainException(String message) {
        super(message);
    }

    /**
     * Constructs a new domain exception with the specified message and cause.
     * @param message Error message
     * @param cause Cause of the exception
     */
    public DomainException(String message, Throwable cause) {
        super(message, cause);
    }
}

