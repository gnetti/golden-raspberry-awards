package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.in.CreateMovieUseCase;
import golden.raspberry.awards.core.application.port.out.GetMovieWithIdPort;
import golden.raspberry.awards.core.application.validator.MovieValidator;
import golden.raspberry.awards.core.domain.model.Movie;
import golden.raspberry.awards.core.domain.model.MovieWithId;
import golden.raspberry.awards.core.domain.port.out.MovieRepositoryPort;

import java.util.Objects;

import static golden.raspberry.awards.core.application.validator.MovieValidator.ValidationResult;

/**
 * Use Case implementation for creating a new movie.
 * Orchestrates the creation of a movie through the repository port.
 *
 * <p>This use case is part of the Application layer and orchestrates
 * the creation of movies following hexagonal architecture principles.
 *
 * <p><strong>Flow:</strong>
 * <pre>
 * Adapter IN (Controller)
 *     ↓
 * CreateMovieUseCase (this - Application)
 *     ↓
 * MovieRepositoryPort (Domain - Port OUT)
 * GetMovieWithIdPort (Application - Port OUT)
 * </pre>
 *
 * <p>Uses Java 21 features EXTREMELY:
 * <ul>
 *   <li>Sealed interfaces for validation results</li>
 *   <li>Pattern Matching with switch expressions</li>
 *   <li>Stream API for functional validation</li>
 *   <li>Optional for null-safe operations</li>
 *   <li>String Templates for elegant error messages</li>
 *   <li>Method references for clean code</li>
 * </ul>
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record CreateMovieUseCaseImpl(
        MovieRepositoryPort repository,
        GetMovieWithIdPort getMovieWithIdPort) implements CreateMovieUseCase {

    /**
     * Constructor for dependency injection.
     *
     * @param repository        Movie repository port (output port)
     * @param getMovieWithIdPort Port for getting movie with ID
     */
    public CreateMovieUseCaseImpl {
        Objects.requireNonNull(repository, "Repository cannot be null");
        Objects.requireNonNull(getMovieWithIdPort, "GetMovieWithIdPort cannot be null");
    }

    @Override
    public MovieWithId execute(Integer year, String title, String studios, String producers, Boolean winner) {
        var validationResult = MovieValidator.validateAll(year, title, studios, producers, winner);
        switch (validationResult) {
            case ValidationResult.Valid ignored -> {
            }
            case ValidationResult.Invalid invalid -> throw new IllegalArgumentException(
                    "Validation failed: %s".formatted(invalid.message())
            );
        }

        var movie = new Movie(
                year,
                title.trim(),
                studios.trim(),
                producers.trim(),
                winner
        );

        repository.save(movie);
        return getMovieWithIdPort.findByYearAndTitle(movie.year(), movie.title())
                .orElseThrow(() -> new IllegalStateException(
                        "Movie was created but could not be found: year=%d, title='%s'".formatted(
                                movie.year(), movie.title()
                        )
                ));
    }
}

