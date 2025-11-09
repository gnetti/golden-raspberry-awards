package golden.raspberry.awards.core.domain.model.event;

import golden.raspberry.awards.core.domain.model.MovieWithId;

import java.time.Instant;
import java.util.Objects;

/**
 * Domain event representing a movie update.
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
public record MovieUpdatedEvent(
        MovieWithId movie,
        Instant occurredAt
) {
    /**
     * Compact constructor for validation.
     */
    public MovieUpdatedEvent {
        Objects.requireNonNull(movie, "Movie cannot be null");
        Objects.requireNonNull(occurredAt, "OccurredAt cannot be null");
    }

    /**
     * Factory method to create a MovieUpdatedEvent with current timestamp.
     *
     * @param movie Updated movie
     * @return MovieUpdatedEvent instance
     */
    public static MovieUpdatedEvent of(MovieWithId movie) {
        return new MovieUpdatedEvent(movie, Instant.now());
    }
}

