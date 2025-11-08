package golden.raspberry.awards.adapter.driven.persistence.mapper;

import golden.raspberry.awards.adapter.driven.persistence.entity.MovieEntity;
import golden.raspberry.awards.core.domain.model.MovieWithId;

import java.util.Objects;
import java.util.Optional;

/**
 * Mapper between MovieEntity (JPA) and MovieWithId (Domain).
 * Converts between persistence layer and domain layer.
 *
 * <p>This mapper is in the Adapter layer because it knows about JPA entities.
 * It converts MovieEntity to MovieWithId for use by Application layer.
 *
 * <p>Uses Java 21 features: Optional, Pattern Matching.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class MovieWithIdMapper {

    private MovieWithIdMapper() {
    }

    /**
     * Converts MovieEntity to MovieWithId.
     *
     * @param entity MovieEntity (can be null)
     * @return Optional containing MovieWithId if entity is not null, empty otherwise
     */
    public static Optional<MovieWithId> toDomain(MovieEntity entity) {
        return Optional.ofNullable(entity)
                .map(MovieWithIdMapper::mapToDomain);
    }

    /**
     * Maps MovieEntity to MovieWithId.
     * Internal method for actual conversion.
     *
     * @param entity MovieEntity (non-null)
     * @return MovieWithId
     */
    private static MovieWithId mapToDomain(MovieEntity entity) {
        Objects.requireNonNull(entity, "MovieEntity cannot be null");
        return new MovieWithId(
                entity.getId(),
                entity.getYear(),
                entity.getTitle(),
                entity.getStudios(),
                entity.getProducers(),
                entity.getWinner()
        );
    }
}

