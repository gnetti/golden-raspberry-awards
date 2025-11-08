package golden.raspberry.awards.core.application.port.in;

/**
 * Input Port (Use Case) for deleting a movie.
 * Part of the Application layer - defines the contract for deleting movies.
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
public interface DeleteMovieUseCase {

    /**
     * Deletes a movie by ID.
     *
     * @param id Movie ID (non-null)
     * @throws IllegalStateException if movie not found
     */
    void execute(Long id);
}

