package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.in.GetMovieUseCase;
import golden.raspberry.awards.core.application.port.out.GetMovieWithIdPort;
import golden.raspberry.awards.core.domain.model.MovieWithId;
import golden.raspberry.awards.core.domain.port.out.MovieRepositoryPort;

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
        MovieRepositoryPort repository,
        GetMovieWithIdPort getMovieWithIdPort) implements GetMovieUseCase {

    /**
     * Constructor for dependency injection.
     *
     * @param repository        Movie repository port (output port)
     * @param getMovieWithIdPort Port for getting movie with ID
     */
    public GetMovieUseCaseHandler {
        Objects.requireNonNull(repository, "Repository cannot be null");
        Objects.requireNonNull(getMovieWithIdPort, "GetMovieWithIdPort cannot be null");
    }

    @Override
    public MovieWithId execute(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        repository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        "Movie with ID %d not found".formatted(id)
                ));

        return getMovieWithIdPort.findByIdWithId(id)
                .orElseThrow(() -> new IllegalStateException(
                        "Movie with ID %d not found".formatted(id)
                ));
    }
}

