package golden.raspberry.awards.infrastructure.adapter.driven.persistence.mapper;

import golden.raspberry.awards.infrastructure.adapter.driven.persistence.entity.MovieEntity;
import golden.raspberry.awards.core.domain.model.aggregate.Movie;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Mapper between Domain Model (Movie) and Entity (MovieEntity).
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class MovieMapper {

    private MovieMapper() {

    }

    /**
     * Converts MovieEntity to Domain Movie.
     *
     * @param entity JPA Entity
     * @return Domain Model
     */
    public static Movie toDomain(MovieEntity entity) {
        if (entity == null) {
            return null;
        }

        return new Movie(
                entity.getYear(),
                entity.getTitle(),
                entity.getStudios(),
                entity.getProducers(),
                entity.getWinner()
        );
    }

    /**
     * Converts Domain Movie to MovieEntity.
     *
     * @param domain Domain Model
     * @return JPA Entity
     */
    public static MovieEntity toEntity(Movie domain) {
        if (domain == null) {
            return null;
        }

        return new MovieEntity(
                domain.year(),
                domain.title(),
                domain.studios(),
                domain.producers(),
                domain.winner()
        );
    }

    /**
     * Converts list of MovieEntity to list of Domain Movie.
     *
     * @param entities List of JPA Entities
     * @return List of Domain Models
     */
    public static List<Movie> toDomainList(List<MovieEntity> entities) {
        if (entities == null) {
            return List.of();
        }

        return entities.stream()
                .filter(Objects::nonNull)
                .map(MovieMapper::toDomain)
                .collect(Collectors.toList());
    }

    /**
     * Converts list of Domain Movie to list of MovieEntity.
     *
     * @param domains List of Domain Models
     * @return List of JPA Entities
     */
    public static List<MovieEntity> toEntityList(List<Movie> domains) {
        if (domains == null) {
            return List.of();
        }

        return domains.stream()
                .filter(Objects::nonNull)
                .map(MovieMapper::toEntity)
                .collect(Collectors.toList());
    }
}

