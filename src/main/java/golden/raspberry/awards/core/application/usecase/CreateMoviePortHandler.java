package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.in.CreateMoviePort;
import golden.raspberry.awards.core.application.port.out.CsvFileWriterPort;
import golden.raspberry.awards.core.application.port.out.IdKeyManagerPort;
import golden.raspberry.awards.core.application.port.out.SaveMovieWithIdPort;
import golden.raspberry.awards.core.application.usecase.validation.MovieValidation;
import golden.raspberry.awards.core.domain.model.aggregate.Movie;
import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;

import java.util.Objects;

/**
 * Use Case implementation for creating a new movie.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record CreateMoviePortHandler(
        SaveMovieWithIdPort saveMovieWithIdPort,
        IdKeyManagerPort idKeyManagerPort,
        CsvFileWriterPort csvFileWriterPort) implements CreateMoviePort {

    /**
     * Constructor for dependency injection.
     *
     * @param saveMovieWithIdPort Port for saving movie with specific ID
     * @param idKeyManagerPort    Port for managing ID keys in XML
     * @param csvFileWriterPort   Port for writing movies to CSV file
     */
    public CreateMoviePortHandler {
        Objects.requireNonNull(saveMovieWithIdPort, "SaveMovieWithIdPort cannot be null");
        Objects.requireNonNull(idKeyManagerPort, "IdKeyManagerPort cannot be null");
        Objects.requireNonNull(csvFileWriterPort, "CsvFileWriterPort cannot be null");
    }

    /**
     * Executes movie creation use case.
     * Validates all input data before creating domain object.
     *
     * @param year      Movie release year
     * @param title     Movie title
     * @param studios   Movie studios
     * @param producers Movie producers
     * @param winner    Whether the movie is a winner
     * @return Created movie with ID
     * @throws IllegalArgumentException if validation fails
     */
    @Override
    public MovieWithId execute(Integer year, String title, String studios, String producers, Boolean winner) {
        MovieValidation.validateMovieData(year, title, studios, producers, winner);

        var nextId = idKeyManagerPort.getNextId();

        var trimmedTitle = title.trim();
        var trimmedStudios = studios.trim();
        var trimmedProducers = producers.trim();

        var movie = new Movie(
                year,
                trimmedTitle,
                trimmedStudios,
                trimmedProducers,
                winner
        );

        var savedMovie = saveMovieWithIdPort.saveWithId(movie, nextId);

        csvFileWriterPort.appendMovie(savedMovie);

        return savedMovie;
    }
}

