package golden.raspberry.awards.core.application.port.out;

import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;

import java.util.List;
import java.util.Optional;

/**
 * Output Port for movie queries.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface MovieQueryPort {

    /**
     * Finds a movie with ID by its ID.
     *
     * @param id Movie ID
     * @return Optional containing MovieWithId if found, empty otherwise
     */
    Optional<MovieWithId> findByIdWithId(Long id);

    /**
     * Finds all movies with ID.
     *
     * @return List of all MovieWithId
     */
    List<MovieWithId> findAll();
}

