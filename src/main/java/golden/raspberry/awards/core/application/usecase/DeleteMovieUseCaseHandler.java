package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.in.DeleteMovieUseCase;
import golden.raspberry.awards.core.application.port.out.CsvFileWriterPort;
import golden.raspberry.awards.core.domain.port.out.MovieRepositoryPort;

import java.util.Objects;

/**
 * Use Case implementation for deleting a movie.
 * Orchestrates the deletion of a movie through the repository port.
 *
 * <p>This use case is part of the Application layer and orchestrates
 * the deletion of movies following hexagonal architecture principles.
 *
 * <p><strong>Flow:</strong>
 * <pre>
 * Adapter IN (Controller)
 *     ↓
 * DeleteMovieUseCase (this - Application)
 *     ↓
 * MovieRepositoryPort (Domain - Port OUT)
 * </pre>
 *
 * <p>Uses Java 21 features: var, Objects.requireNonNull, String Templates.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record DeleteMovieUseCaseHandler(
        MovieRepositoryPort repository,
        CsvFileWriterPort csvFileWriterPort) implements DeleteMovieUseCase {

    /**
     * Constructor for dependency injection.
     *
     * @param repository      Movie repository port (output port)
     * @param csvFileWriterPort Port for writing movies to CSV file
     */
    public DeleteMovieUseCaseHandler {
        Objects.requireNonNull(repository, "Repository cannot be null");
        Objects.requireNonNull(csvFileWriterPort, "CsvFileWriterPort cannot be null");
    }

    @Override
    public void execute(Long id) {
        Objects.requireNonNull(id, "ID cannot be null");
        repository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        "Movie with ID %d not found".formatted(id)
                ));

        var deleted = repository.deleteById(id);
        if (!deleted) {
            throw new IllegalStateException(
                    "Failed to delete movie with ID %d".formatted(id)
            );
        }

        csvFileWriterPort.removeMovie(id);
    }
}

