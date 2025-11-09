package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.in.GetMovieUseCase;
import golden.raspberry.awards.core.application.port.out.MovieQueryPort;
import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;

import java.util.Objects;

/**
 * Use Case implementation for getting a movie by ID.
 * Orchestrates the retrieval of a movie through the repository port.
 *
 * <p>This use case is part of the Application layer and orchestrates
 * the retrieval of movies following hexagonal architecture principles.
 *
 * <p><strong>Flow:</strong>
 * <pre>
 * Adapter IN (Controller)
 *     ↓
 * GetMovieUseCase (this - Application)
 *     ↓
 * GetMovieWithIdPort (Application - Port OUT)
 * </pre>
 *
 * <p>Uses Java 21 features: var, Objects.requireNonNull, String Templates.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record GetMovieUseCaseHandler(
        MovieQueryPort movieQueryPort) implements GetMovieUseCase {

    /**
     * Constructor for dependency injection.
     *
     * @param movieQueryPort Port for querying movies (output port)
     */
    public GetMovieUseCaseHandler {
        Objects.requireNonNull(movieQueryPort, "MovieQueryPort cannot be null");
    }

    @Override
    public MovieWithId execute(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        return movieQueryPort.findByIdWithId(id)
                .orElseThrow(() -> new IllegalStateException(
                        "Movie with ID %d not found".formatted(id)
                ));
    }
}

