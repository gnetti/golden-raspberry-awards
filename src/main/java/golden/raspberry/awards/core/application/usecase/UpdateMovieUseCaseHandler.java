package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.in.UpdateMovieUseCase;
import golden.raspberry.awards.core.application.port.out.CsvFileWriterPort;
import golden.raspberry.awards.core.application.port.out.GetMovieWithIdPort;
import golden.raspberry.awards.core.application.port.out.SaveMovieWithIdPort;
import golden.raspberry.awards.core.domain.model.Movie;
import golden.raspberry.awards.core.domain.model.MovieWithId;
import golden.raspberry.awards.core.domain.port.out.MovieRepositoryPort;

import java.util.Objects;

/**
 * Use Case implementation for updating an existing movie.
 * Orchestrates the update of a movie through the repository port.
 *
 * <p>This use case is part of the Application layer and orchestrates
 * the update of movies following hexagonal architecture principles.
 *
 * <p><strong>Flow:</strong>
 * <pre>
 * Adapter IN (Controller)
 *     ↓
 * UpdateMovieUseCase (this - Application)
 *     ↓
 * MovieRepositoryPort (Domain - Port OUT)
 * GetMovieWithIdPort (Application - Port OUT)
 * </pre>
 *
 * <p>Uses Java 21 features: var, Objects.requireNonNull, String Templates.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record UpdateMovieUseCaseHandler(
        MovieRepositoryPort repository,
        GetMovieWithIdPort getMovieWithIdPort,
        SaveMovieWithIdPort saveMovieWithIdPort,
        CsvFileWriterPort csvFileWriterPort) implements UpdateMovieUseCase {

    /**
     * Constructor for dependency injection.
     *
     * @param repository          Movie repository port (output port)
     * @param getMovieWithIdPort  Port for getting movie with ID
     * @param saveMovieWithIdPort Port for saving movie with specific ID
     * @param csvFileWriterPort   Port for writing movies to CSV file
     */
    public UpdateMovieUseCaseHandler {
        Objects.requireNonNull(repository, "Repository cannot be null");
        Objects.requireNonNull(getMovieWithIdPort, "GetMovieWithIdPort cannot be null");
        Objects.requireNonNull(saveMovieWithIdPort, "SaveMovieWithIdPort cannot be null");
        Objects.requireNonNull(csvFileWriterPort, "CsvFileWriterPort cannot be null");
    }

    @Override
    public MovieWithId execute(Long id, Integer year, String title, String studios, String producers, Boolean winner) {
        Objects.requireNonNull(id, "ID cannot be null");
        repository.findById(id)
                .orElseThrow(() -> new IllegalStateException(
                        "Movie with ID %d not found".formatted(id)
                ));

        var updatedMovie = new Movie(
                year,
                title.trim(),
                studios.trim(),
                producers.trim(),
                winner
        );

        var updatedMovieWithId = saveMovieWithIdPort.saveWithId(updatedMovie, id);
        csvFileWriterPort.updateMovie(updatedMovieWithId);
        return updatedMovieWithId;
    }
}

