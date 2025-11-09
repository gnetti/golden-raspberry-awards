package golden.raspberry.awards.core.application.handler;

import golden.raspberry.awards.core.application.command.CreateMovieCommand;
import golden.raspberry.awards.core.application.command.DeleteMovieCommand;
import golden.raspberry.awards.core.application.command.UpdateMovieCommand;
import golden.raspberry.awards.core.application.port.in.CreateMovieUseCase;
import golden.raspberry.awards.core.application.port.in.DeleteMovieUseCase;
import golden.raspberry.awards.core.application.port.in.UpdateMovieUseCase;
import golden.raspberry.awards.core.domain.model.MovieWithId;

import java.util.Objects;

/**
 * Command handler for movie operations.
 * Handles commands following CQRS pattern.
 *
 * <p>This handler is part of the Application layer and orchestrates
 * command execution following hexagonal architecture principles.
 *
 * <p><strong>CQRS Pattern:</strong>
 * <ul>
 *   <li>Separates command side from query side</li>
 *   <li>Handles write operations (commands)</li>
 *   <li>Delegates to use cases for execution</li>
 * </ul>
 *
 * <p>Uses Java 21 features: Objects.requireNonNull.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public class MovieCommandHandler {

    private final CreateMovieUseCase createMovieUseCase;
    private final UpdateMovieUseCase updateMovieUseCase;
    private final DeleteMovieUseCase deleteMovieUseCase;

    /**
     * Constructor for dependency injection.
     *
     * @param createMovieUseCase Use case for creating movies
     * @param updateMovieUseCase Use case for updating movies
     * @param deleteMovieUseCase Use case for deleting movies
     */
    public MovieCommandHandler(
            CreateMovieUseCase createMovieUseCase,
            UpdateMovieUseCase updateMovieUseCase,
            DeleteMovieUseCase deleteMovieUseCase) {
        this.createMovieUseCase = Objects.requireNonNull(createMovieUseCase, "CreateMovieUseCase cannot be null");
        this.updateMovieUseCase = Objects.requireNonNull(updateMovieUseCase, "UpdateMovieUseCase cannot be null");
        this.deleteMovieUseCase = Objects.requireNonNull(deleteMovieUseCase, "DeleteMovieUseCase cannot be null");
    }

    /**
     * Handles CreateMovieCommand.
     *
     * @param command CreateMovieCommand
     * @return Created MovieWithId
     */
    public MovieWithId handle(CreateMovieCommand command) {
        Objects.requireNonNull(command, "CreateMovieCommand cannot be null");
        return createMovieUseCase.execute(
                command.year(),
                command.title(),
                command.studios(),
                command.producers(),
                command.winner()
        );
    }

    /**
     * Handles UpdateMovieCommand.
     *
     * @param command UpdateMovieCommand
     * @return Updated MovieWithId
     */
    public MovieWithId handle(UpdateMovieCommand command) {
        Objects.requireNonNull(command, "UpdateMovieCommand cannot be null");
        return updateMovieUseCase.execute(
                command.id(),
                command.year(),
                command.title(),
                command.studios(),
                command.producers(),
                command.winner()
        );
    }

    /**
     * Handles DeleteMovieCommand.
     *
     * @param command DeleteMovieCommand
     */
    public void handle(DeleteMovieCommand command) {
        Objects.requireNonNull(command, "DeleteMovieCommand cannot be null");
        deleteMovieUseCase.execute(command.id());
    }
}

