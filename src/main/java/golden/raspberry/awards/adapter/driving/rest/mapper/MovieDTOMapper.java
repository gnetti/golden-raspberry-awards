package golden.raspberry.awards.adapter.driving.rest.mapper;

import golden.raspberry.awards.adapter.driving.rest.dto.MovieDTO;
import golden.raspberry.awards.core.domain.model.MovieWithId;

import java.util.Objects;
import java.util.Optional;

/**
 * Mapper between MovieWithId (Domain) and MovieDTO (Adapter).
 * Converts between domain layer and REST API layer.
 *
 * <p>This mapper is in the Adapter layer because it knows about DTOs.
 * It converts MovieWithId to MovieDTO for REST API responses.
 *
 * <p>Uses Java 21 features: Optional, Pattern Matching.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class MovieDTOMapper {

    private MovieDTOMapper() {
        // Utility class - prevent instantiation
    }

    /**
     * Converts MovieWithId to MovieDTO.
     *
     * @param movieWithId MovieWithId (can be null)
     * @return Optional containing MovieDTO if movieWithId is not null, empty otherwise
     */
    public static Optional<MovieDTO> toDTO(MovieWithId movieWithId) {
        return Optional.ofNullable(movieWithId)
                .map(MovieDTOMapper::mapToDTO);
    }

    /**
     * Maps MovieWithId to MovieDTO.
     * Internal method for actual conversion.
     *
     * @param movieWithId MovieWithId (non-null)
     * @return MovieDTO
     */
    private static MovieDTO mapToDTO(MovieWithId movieWithId) {
        Objects.requireNonNull(movieWithId, "MovieWithId cannot be null");
        return new MovieDTO(
                movieWithId.id(),
                movieWithId.year(),
                movieWithId.title(),
                movieWithId.studios(),
                movieWithId.producers(),
                movieWithId.winner()
        );
    }
}

