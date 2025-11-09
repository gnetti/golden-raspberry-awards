package golden.raspberry.awards.core.domain.model.valueobject;

import java.util.Objects;

/**
 * Value object representing a movie identifier.
 * Immutable value object with business rules.
 *
 * <p>This value object is part of the Domain layer and encapsulates
 * the concept of a movie identifier following hexagonal architecture principles.
 *
 * <p>Uses Java 21 features: Records, Objects.requireNonNull.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record MovieId(Long value) {
    /**
     * Compact constructor for validation.
     */
    public MovieId {
        Objects.requireNonNull(value, "MovieId value cannot be null");
        if (value <= 0) {
            throw new IllegalArgumentException("MovieId must be positive");
        }
    }

    /**
     * Factory method to create a MovieId.
     *
     * @param value Long value
     * @return MovieId instance
     */
    public static MovieId of(Long value) {
        return new MovieId(value);
    }
}

