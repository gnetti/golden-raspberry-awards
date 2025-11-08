package golden.raspberry.awards.core.application.port.in;

import golden.raspberry.awards.core.domain.model.MovieWithId;

/**
 * Input Port (Use Case) for getting a movie by ID.
 * Part of the Application layer - defines the contract for retrieving movies.
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
public interface GetMovieUseCase {

    /**
     * Gets a movie by ID.
     *
     * @param id Movie ID (non-null)
     * @return MovieWithId with ID
     * @throws IllegalStateException if movie not found
     */
    MovieWithId execute(Long id);
}

