package golden.raspberry.awards.core.application.port.out;

import golden.raspberry.awards.core.domain.model.aggregate.Movie;
import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;

import java.util.List;
import java.util.Optional;

/**
 * Output Port specialized for movie queries (read operations).
 * Part of CQRS pattern - separates read operations from write operations.
 *
 * <p>This port is part of the Application layer and defines the contract
 * for querying movies without modifying them. It follows the CQRS pattern
 * by separating read operations from write operations.
 *
 * <p><strong>Hexagonal Architecture:</strong>
 * <ul>
 *   <li>Output Port (Secondary) - defined by Application</li>
 *   <li>Implemented by Output Adapters</li>
 *   <li>Used by Query Handlers and Use Cases (Application layer)</li>
 * </ul>
 *
 * <p>Uses Java 21 features: Records, Optional, List.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface MovieQueryPort {

    /**
     * Finds a movie by its ID.
     *
     * @param id Movie ID (non-null)
     * @return Optional containing Movie if found, empty otherwise
     */
    Optional<Movie> findById(Long id);

    /**
     * Finds a movie with ID by its ID.
     *
     * @param id Movie ID (non-null)
     * @return Optional containing MovieWithId if found, empty otherwise
     */
    Optional<MovieWithId> findByIdWithId(Long id);

    /**
     * Finds all movies.
     *
     * @return List of all movies
     */
    List<Movie> findAll();

    /**
     * Finds all movies with IDs.
     *
     * @return List of all movies with IDs
     */
    List<MovieWithId> findAllWithId();

    /**
     * Finds movies by year.
     *
     * @param year Movie release year (non-null)
     * @return List of movies released in the specified year
     */
    List<Movie> findByYear(Integer year);

    /**
     * Finds movies by year and title.
     *
     * @param year  Movie release year (non-null)
     * @param title Movie title (non-null)
     * @return Optional containing Movie if found, empty otherwise
     */
    Optional<Movie> findByYearAndTitle(Integer year, String title);

    /**
     * Finds movies with ID by year and title.
     *
     * @param year  Movie release year (non-null)
     * @param title Movie title (non-null)
     * @return Optional containing MovieWithId if found, empty otherwise
     */
    Optional<MovieWithId> findByYearAndTitleWithId(Integer year, String title);

    /**
     * Finds all winning movies.
     *
     * @return List of movies where winner is true
     */
    List<Movie> findWinners();

    /**
     * Finds all winning movies with IDs.
     *
     * @return List of movies with IDs where winner is true
     */
    List<MovieWithId> findWinnersWithId();
}

