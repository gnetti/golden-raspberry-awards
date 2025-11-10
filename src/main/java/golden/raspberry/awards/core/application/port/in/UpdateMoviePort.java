package golden.raspberry.awards.core.application.port.in;

import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;

/**
 * Input Port for updating an existing movie.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface UpdateMoviePort {

    /**
     * Updates an existing movie by ID.
     *
     * @param id        Movie ID
     * @param year      Movie release year
     * @param title     Movie title
     * @param studios   Movie studios
     * @param producers Movie producers
     * @param winner    Whether the movie is a winner
     * @return Updated MovieWithId with ID
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException    if movie not found
     */
    MovieWithId execute(Long id, Integer year, String title, String studios, String producers, Boolean winner);
}

