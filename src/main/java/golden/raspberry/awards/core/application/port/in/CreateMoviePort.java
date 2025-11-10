package golden.raspberry.awards.core.application.port.in;

import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;

/**
 * Input Port for creating a new movie.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface CreateMoviePort {

    /**
     * Creates a new movie.
     *
     * @param year      Movie release year
     * @param title     Movie title
     * @param studios   Movie studios
     * @param producers Movie producers
     * @param winner    Whether the movie is a winner
     * @return Created MovieWithId with generated ID
     * @throws IllegalArgumentException if validation fails
     */
    MovieWithId execute(Integer year, String title, String studios, String producers, Boolean winner);
}

