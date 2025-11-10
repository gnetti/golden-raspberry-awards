package golden.raspberry.awards.core.application.port.in;

import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;

/**
 * Input Port for getting a movie by ID.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface GetMoviePort {

    /**
     * Gets a movie by ID.
     *
     * @param id Movie ID
     * @return MovieWithId with ID
     * @throws IllegalStateException if movie not found
     */
    MovieWithId execute(Long id);
}

