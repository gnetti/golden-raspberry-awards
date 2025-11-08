package golden.raspberry.awards.core.domain.port.out;

import golden.raspberry.awards.core.domain.model.Movie;
import java.util.List;

/**
 * Output Port for Movie repository operations.
 * Defined by Domain, implemented by Adapters.
 * Pure interface - no Spring dependencies.
 */
public interface MovieRepositoryPort {
    /**
     * Finds all movies that are winners.
     * @return List of winning movies
     */
    List<Movie> findByWinnerTrue();

    /**
     * Saves all movies to the repository.
     * @param movies List of movies to save
     */
    void saveAll(List<Movie> movies);
}

