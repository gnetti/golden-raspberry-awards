package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.in.UpdateMoviePort;
import golden.raspberry.awards.core.application.port.out.CsvFileWriterPort;
import golden.raspberry.awards.core.application.port.out.SaveMovieWithIdPort;
import golden.raspberry.awards.core.application.usecase.validation.MovieValidation;
import golden.raspberry.awards.core.application.usecase.validation.MovieValidator;
import golden.raspberry.awards.core.domain.model.aggregate.Movie;
import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;

import java.util.Objects;

/**
 * Use Case implementation for updating an existing movie.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record UpdateMoviePortHandler(
        MovieValidator validator,
        SaveMovieWithIdPort saveMovieWithIdPort,
        CsvFileWriterPort csvFileWriterPort) implements UpdateMoviePort {

    /**
     * Constructor for dependency injection.
     *
     * @param validator           Movie validator
     * @param saveMovieWithIdPort Port for saving movie with specific ID
     * @param csvFileWriterPort   Port for writing movies to CSV file
     */
    public UpdateMoviePortHandler {
        Objects.requireNonNull(validator, "MovieValidator cannot be null");
        Objects.requireNonNull(saveMovieWithIdPort, "SaveMovieWithIdPort cannot be null");
        Objects.requireNonNull(csvFileWriterPort, "CsvFileWriterPort cannot be null");
    }

    /**
     * Executes movie update use case.
     * Validates all input data and verifies movie exists before updating.
     *
     * @param id        Movie ID
     * @param year      Movie release year
     * @param title     Movie title
     * @param studios   Movie studios
     * @param producers Movie producers
     * @param winner    Whether the movie is a winner
     * @return Updated movie with ID
     * @throws IllegalArgumentException if validation fails
     * @throws IllegalStateException    if movie not found
     */
    @Override
    public MovieWithId execute(Long id, Integer year, String title, String studios, String producers, Boolean winner) {
        MovieValidation.validateMovieUpdateData(id, year, title, studios, producers, winner);

        validator.validateExists(id);

        var trimmedTitle = title.trim();
        var trimmedStudios = studios.trim();
        var trimmedProducers = producers.trim();

        var updatedMovie = new Movie(
                year,
                trimmedTitle,
                trimmedStudios,
                trimmedProducers,
                winner
        );

        var updatedMovieWithId = saveMovieWithIdPort.saveWithId(updatedMovie, id);
        csvFileWriterPort.updateMovie(updatedMovieWithId);
        return updatedMovieWithId;
    }
}

