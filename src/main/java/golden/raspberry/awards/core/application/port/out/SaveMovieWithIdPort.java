package golden.raspberry.awards.core.application.port.out;

import golden.raspberry.awards.core.domain.model.aggregate.Movie;
import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;

/**
 * Output Port for saving a movie with a specific ID.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface SaveMovieWithIdPort {

    /**
     * Saves a movie with a specific ID.
     *
     * @param movie Movie to save
     * @param id    ID to assign to the movie
     * @return Saved MovieWithId containing the assigned ID
     * @throws IllegalStateException if movie cannot be saved with the provided ID
     */
    MovieWithId saveWithId(Movie movie, Long id);
}

