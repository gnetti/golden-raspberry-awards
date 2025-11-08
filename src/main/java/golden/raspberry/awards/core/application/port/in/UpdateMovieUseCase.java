package golden.raspberry.awards.core.application.port.in;

import golden.raspberry.awards.core.domain.model.MovieWithId;

/**
 * Input Port (Use Case) for updating an existing movie.
 * Part of the Application layer - defines the contract for updating movies.
 *
 * <p>Follows Hexagonal Architecture:
 * <ul>
 *   <li>Input Port (Primary) - defines what the application can do</li>
 *   <li>Used by Input Adapters (Controllers)</li>
 *   <li>Implemented by Use Case implementations</li>
 * </ul>
 *
 * <p>Uses Java 21 features: Records, Objects.requireNonNull.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface UpdateMovieUseCase {

    /**
     * Updates an existing movie by ID.
     *
     * @param id        Movie ID (non-null)
     * @param year      Movie release year (non-null, 1900-2100)
     * @param title     Movie title (non-null, non-blank)
     * @param studios   Movie studios (non-null, non-blank)
     * @param producers Movie producers (non-null, non-blank)
     * @param winner    Whether the movie is a winner (non-null)
     * @return Updated MovieWithId with ID
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException    if movie not found
     */
    MovieWithId execute(Long id, Integer year, String title, String studios, String producers, Boolean winner);
}

