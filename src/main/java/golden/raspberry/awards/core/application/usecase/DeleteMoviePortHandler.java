package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.in.DeleteMoviePort;
import golden.raspberry.awards.core.application.port.out.CsvFileWriterPort;
import golden.raspberry.awards.core.application.port.out.MovieRepositoryPort;
import golden.raspberry.awards.core.application.usecase.validation.MovieValidation;
import golden.raspberry.awards.core.application.usecase.validation.MovieValidator;

import java.util.Objects;

/**
 * Use Case implementation for deleting a movie.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record DeleteMoviePortHandler(
        MovieValidator validator,
        MovieRepositoryPort repository,
        CsvFileWriterPort csvFileWriterPort) implements DeleteMoviePort {

    /**
     * Constructor for dependency injection.
     *
     * @param validator          Movie validator
     * @param repository         Movie repository port
     * @param csvFileWriterPort Port for writing movies to CSV file
     */
    public DeleteMoviePortHandler {
        Objects.requireNonNull(validator, "MovieValidator cannot be null");
        Objects.requireNonNull(repository, "Repository cannot be null");
        Objects.requireNonNull(csvFileWriterPort, "CsvFileWriterPort cannot be null");
    }

    /**
     * Executes movie deletion use case.
     * Validates ID and verifies movie exists before attempting deletion.
     *
     * @param id Movie ID
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException    if movie not found or deletion fails
     */
    @Override
    public void execute(Long id) {
        MovieValidation.validateId(id);

        validator.validateExists(id);

        var deleted = repository.deleteById(id);
        if (!deleted) {
            throw new IllegalStateException(
                    "Failed to delete movie with ID %d".formatted(id)
            );
        }

        csvFileWriterPort.removeMovie(id);
    }
}

