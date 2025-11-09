package golden.raspberry.awards.core.application.query;

import java.util.Objects;

/**
 * Query for getting a movie by ID.
 * Part of CQRS pattern for read operations.
 *
 * <p>This query is part of the Application layer and represents
 * a request to get a movie following hexagonal architecture principles.
 *
 * <p>Uses Java 21 features: Records, Objects.requireNonNull.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record GetMovieQuery(
        Long id
) {
    /**
     * Compact constructor for validation.
     */
    public GetMovieQuery {
        Objects.requireNonNull(id, "ID cannot be null");
    }

    /**
     * Factory method to create a GetMovieQuery.
     *
     * @param id Movie ID
     * @return GetMovieQuery instance
     */
    public static GetMovieQuery of(Long id) {
        return new GetMovieQuery(id);
    }
}

