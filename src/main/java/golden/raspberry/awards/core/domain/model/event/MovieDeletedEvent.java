package golden.raspberry.awards.core.domain.model.event;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain event representing a movie deletion.
 * Pure domain event with zero external dependencies.
 *
 * <p>This event is part of the Domain layer and represents
 * a business event that occurred in the domain.
 *
 * <p>Uses Java 21 features: Records, Objects.requireNonNull.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record MovieDeletedEvent(
        Long movieId,
        Instant occurredAt
) {
    /**
     * Compact constructor for validation.
     */
    public MovieDeletedEvent {
        Objects.requireNonNull(movieId, "MovieId cannot be null");
        Objects.requireNonNull(occurredAt, "OccurredAt cannot be null");
    }

    /**
     * Factory method to create a MovieDeletedEvent with current timestamp.
     *
     * @param movieId Deleted movie ID
     * @return MovieDeletedEvent instance
     */
    public static MovieDeletedEvent of(Long movieId) {
        return new MovieDeletedEvent(movieId, Instant.now());
    }
}

