package golden.raspberry.awards.core.application.usecase.validation;

import golden.raspberry.awards.core.application.port.out.MovieQueryPort;

import java.util.Objects;

/**
 * Validator for Movie Use Cases.
 * Validates movie existence before domain operations.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class MovieValidator {

    private final MovieQueryPort movieQueryPort;

    /**
     * Constructor for dependency injection.
     *
     * @param movieQueryPort Port for querying movies
     */
    public MovieValidator(MovieQueryPort movieQueryPort) {
        this.movieQueryPort = Objects.requireNonNull(movieQueryPort, "MovieQueryPort cannot be null");
    }

    /**
     * Validates that a movie exists by ID.
     *
     * @param id Movie ID to validate
     * @throws IllegalStateException if movie does not exist
     */
    public void validateExists(Long id) {
        MovieValidation.validateId(id);
        
        var movieExists = movieQueryPort.findByIdWithId(id)
                .isPresent();
        
        if (!movieExists) {
            throw new IllegalStateException(
                    "Movie with ID %d not found".formatted(id)
            );
        }
    }
}

