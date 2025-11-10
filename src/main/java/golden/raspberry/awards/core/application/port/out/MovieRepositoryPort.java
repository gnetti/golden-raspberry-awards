package golden.raspberry.awards.core.application.port.out;

import golden.raspberry.awards.core.domain.model.aggregate.Movie;

import java.util.List;
import java.util.Optional;

/**
 * Output Port for Movie repository operations.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface MovieRepositoryPort {

    /**
     * Finds all movies that are winners.
     *
     * @return List of winning movies
     */
    List<Movie> findByWinnerTrue();

    /**
     * Finds a movie by ID.
     *
     * @param id Movie ID
     * @return Optional containing the movie if found, empty otherwise
     */
    Optional<Movie> findById(Long id);

    /**
     * Deletes a movie by ID.
     *
     * @param id Movie ID
     * @return true if movie was deleted, false if not found
     */
    boolean deleteById(Long id);
}

