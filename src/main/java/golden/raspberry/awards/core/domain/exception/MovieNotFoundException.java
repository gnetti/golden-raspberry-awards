package golden.raspberry.awards.core.domain.exception;

/**
 * Exception thrown when a Movie is not found by ID.
 * Represents a domain-level "not found" error.
 *
 * <p>This exception should be used when a movie with the given ID does not exist.
 * It is a checked exception to force explicit handling.
 *
 * <p>Uses Java 21 features: String Templates for error messages.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public class MovieNotFoundException extends Exception {

    private final Long movieId;

    /**
     * Creates a new MovieNotFoundException.
     *
     * @param movieId The ID of the movie that was not found
     */
    public MovieNotFoundException(Long movieId) {
        super("Movie with ID %d not found".formatted(movieId));
        this.movieId = movieId;
    }

    /**
     * Creates a new MovieNotFoundException with a cause.
     *
     * @param movieId The ID of the movie that was not found
     * @param cause   The cause of this exception
     */
    public MovieNotFoundException(Long movieId, Throwable cause) {
        super("Movie with ID %d not found".formatted(movieId), cause);
        this.movieId = movieId;
    }

    /**
     * Gets the ID of the movie that was not found.
     *
     * @return Movie ID
     */
    public Long getMovieId() {
        return movieId;
    }
}

