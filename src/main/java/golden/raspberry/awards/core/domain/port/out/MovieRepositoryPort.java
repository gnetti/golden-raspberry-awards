package golden.raspberry.awards.core.domain.port.out;

import golden.raspberry.awards.core.domain.model.Movie;
import java.util.List;
import java.util.Optional;

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

    /**
     * Saves a single movie to the repository.
     * @param movie Movie to save
     * @return Saved Movie with generated ID (if applicable)
     */
    Movie save(Movie movie);

    /**
     * Finds a movie by ID.
     * @param id Movie ID
     * @return Optional containing the movie if found, empty otherwise
     */
    Optional<Movie> findById(Long id);

    /**
     * Deletes a movie by ID.
     * @param id Movie ID
     * @return true if movie was deleted, false if not found
     */
    boolean deleteById(Long id);
}

