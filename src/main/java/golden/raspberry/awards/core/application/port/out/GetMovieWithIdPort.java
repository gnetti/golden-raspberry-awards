package golden.raspberry.awards.core.application.port.out;

import golden.raspberry.awards.core.domain.model.MovieWithId;

import java.util.Optional;

/**
 * Output Port for getting Movie with ID.
 * Defined by Application, implemented by Adapters.
 *
 * <p>This port allows Application layer to retrieve movies with their IDs
 * without directly depending on adapters or JPA entities.
 *
 * <p>Follows Hexagonal Architecture:
 * <ul>
 *   <li>Output Port (Secondary) - defined by Application</li>
 *   <li>Implemented by Output Adapters</li>
 *   <li>Used by Use Cases (Application layer)</li>
 * </ul>
 *
 * <p>Uses Java 21 features: Records, Optional.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface GetMovieWithIdPort {

    /**
     * Finds a movie with ID by its ID.
     *
     * @param id Movie ID (non-null)
     * @return Optional containing MovieWithId if found, empty otherwise
     */
    Optional<MovieWithId> findByIdWithId(Long id);

    /**
     * Finds a movie with ID by year and title.
     *
     * @param year  Movie release year (non-null)
     * @param title Movie title (non-null)
     * @return Optional containing MovieWithId if found, empty otherwise
     */
    Optional<MovieWithId> findByYearAndTitle(Integer year, String title);
}

