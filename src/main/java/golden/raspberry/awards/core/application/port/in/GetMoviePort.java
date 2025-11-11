package golden.raspberry.awards.core.application.port.in;

import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Input Port for getting movies.
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

    /**
     * Gets all movies.
     *
     * @return List of all MovieWithId
     */
    List<MovieWithId> executeAll();

    /**
     * Gets all movies with pagination and sorting.
     *
     * @param pageable Pagination and sorting parameters
     * @return Page of MovieWithId
     */
    Page<MovieWithId> executeAll(Pageable pageable);
    
    /**
     * Gets all movies with pagination, sorting and filtering.
     * Filters are applied to ALL database records before pagination.
     *
     * @param filterType Type of filter (all, title, year, studios, producers, id)
     * @param filterValue Value to search for
     * @param pageable Pagination and sorting parameters
     * @return Page of MovieWithId
     */
    Page<MovieWithId> executeAllWithFilter(String filterType, String filterValue, Pageable pageable);
}

