package golden.raspberry.awards.core.application.command;

import java.util.Objects;

/**
 * Command for deleting a movie.
 * Part of CQRS pattern for write operations.
 *
 * <p>This command is part of the Application layer and represents
 * an intent to delete a movie following hexagonal architecture principles.
 *
 * <p>Uses Java 21 features: Records, Objects.requireNonNull.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record DeleteMovieCommand(
        Long id
) {
    /**
     * Compact constructor for validation.
     */
    public DeleteMovieCommand {
        Objects.requireNonNull(id, "ID cannot be null");
    }
}

