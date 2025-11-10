package golden.raspberry.awards.infrastructure.adapter.driven.persistence.mapper;

import golden.raspberry.awards.infrastructure.adapter.driven.persistence.entity.MovieEntity;
import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Mapper between MovieEntity (JPA) and MovieWithId (Domain).
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
     * @param entity MovieEntity
     * @return Optional containing MovieWithId if entity is not null, empty otherwise
     */
    public static Optional<MovieWithId> toDomain(MovieEntity entity) {
        return Optional.ofNullable(entity)
                .map(MovieWithIdMapper::mapToDomain);
    }

    /**
     * Maps MovieEntity to MovieWithId.
     *
     * @param entity MovieEntity
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

    /**
     * Converts a list of MovieEntity to a list of MovieWithId.
     *
     * @param entities List of MovieEntity
     * @return List of MovieWithId
     */
    public static List<MovieWithId> toDomainList(List<MovieEntity> entities) {
        return entities.stream()
                .map(MovieWithIdMapper::mapToDomain)
                .toList();
    }
}

