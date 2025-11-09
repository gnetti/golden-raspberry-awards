package golden.raspberry.awards.core.domain.model.valueobject;

import java.util.Objects;

/**
 * Value object representing winner status.
 * Immutable value object with business rules.
 *
 * <p>This value object is part of the Domain layer and encapsulates
 * the concept of winner status following hexagonal architecture principles.
 *
 * <p>Uses Java 21 features: Records, Objects.requireNonNull.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record Winner(Boolean value) {
    /**
     * Compact constructor for validation.
     */
    public Winner {
        Objects.requireNonNull(value, "Winner value cannot be null");
    }

    /**
     * Factory method to create a Winner.
     *
     * @param value Boolean value
     * @return Winner instance
     */
    public static Winner of(Boolean value) {
        return new Winner(value);
    }

    /**
     * Checks if the movie is a winner.
     *
     * @return true if winner, false otherwise
     */
    public boolean isWinner() {
        return value;
    }
}

