package golden.raspberry.awards.core.application.port.out;

import golden.raspberry.awards.core.domain.model.Movie;
import golden.raspberry.awards.core.domain.model.MovieWithId;

/**
 * Output Port for saving a movie with a specific ID.
 *
 * <p>This port is part of the Application layer and defines the contract
 * for saving movies with externally managed IDs (from XML key manager).
 *
 * <p>Used by CREATE operations to ensure IDs are never reused and
 * are properly synchronized with the XML key file.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface SaveMovieWithIdPort {

    /**
     * Saves a movie with a specific ID.
     * The ID is managed externally (via IdKeyManagerPort) to ensure
     * data integrity and prevent ID reuse.
     *
     * @param movie Movie to save (without ID)
     * @param id    ID to assign to the movie
     * @return Saved MovieWithId containing the assigned ID
     * @throws IllegalStateException if movie cannot be saved with the provided ID
     */
    MovieWithId saveWithId(Movie movie, Long id);
}

