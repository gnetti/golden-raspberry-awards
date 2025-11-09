package golden.raspberry.awards.core.domain.model.valueobject;

import java.util.Objects;

/**
 * Value object representing movie studios.
 * Immutable value object with business rules.
 *
 * <p>This value object is part of the Domain layer and encapsulates
 * the concept of movie studios following hexagonal architecture principles.
 *
 * <p>Uses Java 21 features: Records, Objects.requireNonNull.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record Studios(String value) {
    private static final int MIN_LENGTH = 2;
    private static final int MAX_LENGTH = 255;

    /**
     * Compact constructor for validation.
     */
    public Studios {
        Objects.requireNonNull(value, "Studios value cannot be null");
        var trimmed = value.trim();
        if (trimmed.isBlank()) {
            throw new IllegalArgumentException("Studios cannot be blank");
        }
        if (trimmed.length() < MIN_LENGTH) {
            throw new IllegalArgumentException("Studios must be at least %d characters".formatted(MIN_LENGTH));
        }
        if (trimmed.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Studios must be at most %d characters".formatted(MAX_LENGTH));
        }
    }

    /**
     * Factory method to create a Studios.
     *
     * @param value String value
     * @return Studios instance
     */
    public static Studios of(String value) {
        return new Studios(value);
    }
}

