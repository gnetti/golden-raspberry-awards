package golden.raspberry.awards.core.application.port.in;

/**
 * Input Port for deleting a movie.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface DeleteMoviePort {

    /**
     * Deletes a movie by ID.
     *
     * @param id Movie ID
     * @throws IllegalStateException if movie not found
     */
    void execute(Long id);
}

