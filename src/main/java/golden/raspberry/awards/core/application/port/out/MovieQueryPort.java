package golden.raspberry.awards.core.application.port.out;

import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

/**
 * Output Port for movie queries.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface MovieQueryPort {

    /**
     * Finds a movie with ID by its ID.
     *
     * @param id Movie ID
     * @return Optional containing MovieWithId if found, empty otherwise
     */
    Optional<MovieWithId> findByIdWithId(Long id);

    /**
     * Finds all movies with ID.
     *
     * @return List of all MovieWithId
     */
    List<MovieWithId> findAll();

    /**
     * Finds all movies with ID using pagination and sorting.
     *
     * @param pageable Pagination and sorting parameters
     * @return Page of MovieWithId
     */
    Page<MovieWithId> findAll(Pageable pageable);
    
    /**
     * Finds movies with ID using pagination, sorting and filtering.
     *
     * @param filterType Type of filter (all, title, year, studios, producers, id)
     * @param filterValue Value to search for
     * @param pageable Pagination and sorting parameters
     * @return Page of MovieWithId
     */
    Page<MovieWithId> findAllWithFilter(String filterType, String filterValue, Pageable pageable);
}

