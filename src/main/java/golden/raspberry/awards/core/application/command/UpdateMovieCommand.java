package golden.raspberry.awards.core.application.command;

import java.util.Objects;

/**
 * Command for updating an existing movie.
 * Part of CQRS pattern for write operations.
 *
 * <p>This command is part of the Application layer and represents
 * an intent to update a movie following hexagonal architecture principles.
 *
 * <p>Uses Java 21 features: Records, Objects.requireNonNull.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record UpdateMovieCommand(
        Long id,
        Integer year,
        String title,
        String studios,
        String producers,
        Boolean winner
) {
    /**
     * Compact constructor for validation.
     */
    public UpdateMovieCommand {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(year, "Year cannot be null");
        Objects.requireNonNull(title, "Title cannot be null");
        Objects.requireNonNull(studios, "Studios cannot be null");
        Objects.requireNonNull(producers, "Producers cannot be null");
        Objects.requireNonNull(winner, "Winner cannot be null");
    }
}

