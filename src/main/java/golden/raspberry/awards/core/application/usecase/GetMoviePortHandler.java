package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.in.GetMoviePort;
import golden.raspberry.awards.core.application.port.out.MovieQueryPort;
import golden.raspberry.awards.core.application.usecase.validation.MovieValidation;
import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Objects;

/**
 * Use Case implementation for getting a movie by ID.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record GetMoviePortHandler(
        MovieQueryPort movieQueryPort) implements GetMoviePort {

    /**
     * Constructor for dependency injection.
     *
     * @param movieQueryPort Port for querying movies
     */
    public GetMoviePortHandler {
        Objects.requireNonNull(movieQueryPort, "MovieQueryPort cannot be null");
    }

    /**
     * Executes movie retrieval use case.
     * Validates ID before querying.
     *
     * @param id Movie ID
     * @return Movie with ID
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException    if movie not found
     */
    @Override
    public MovieWithId execute(Long id) {
        MovieValidation.validateId(id);
        return movieQueryPort.findByIdWithId(id)
                .orElseThrow(() -> new IllegalStateException(
                        "Movie with ID %d not found".formatted(id)
                ));
    }

    /**
     * Executes use case to get all movies.
     *
     * @return List of all MovieWithId
     */
    @Override
    public List<MovieWithId> executeAll() {
        return movieQueryPort.findAll();
    }

    /**
     * Executes use case to get all movies with pagination and sorting.
     *
     * @param pageable Pagination and sorting parameters
     * @return Page of MovieWithId
     */
    @Override
    public Page<MovieWithId> executeAll(Pageable pageable) {
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        return movieQueryPort.findAll(pageable);
    }
    
    /**
     * Executes use case to get all movies with pagination, sorting and filtering.
     * Filters are applied to ALL database records before pagination.
     *
     * @param filterType Type of filter (all, title, year, studios, producers, id)
     * @param filterValue Value to search for
     * @param pageable Pagination and sorting parameters
     * @return Page of MovieWithId
     */
    @Override
    public Page<MovieWithId> executeAllWithFilter(String filterType, String filterValue, Pageable pageable) {
        Objects.requireNonNull(filterType, "Filter type cannot be null");
        Objects.requireNonNull(filterValue, "Filter value cannot be null");
        Objects.requireNonNull(pageable, "Pageable cannot be null");
        return movieQueryPort.findAllWithFilter(filterType, filterValue, pageable);
    }
}

