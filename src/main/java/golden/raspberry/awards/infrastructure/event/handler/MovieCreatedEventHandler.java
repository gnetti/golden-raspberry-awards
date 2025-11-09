package golden.raspberry.awards.infrastructure.event.handler;

import golden.raspberry.awards.core.domain.model.event.MovieCreatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Infrastructure event handler for MovieCreatedEvent.
 * Handles side effects of movie creation events.
 *
 * <p>This handler is part of the Infrastructure layer and processes
 * domain events following hexagonal architecture principles.
 *
 * <p><strong>Hexagonal Architecture:</strong>
 * <ul>
 *   <li>Listens to domain events published by Application layer</li>
 *   <li>Executes side effects (logging, notifications, etc.)</li>
 *   <li>Does not contain business logic</li>
 * </ul>
 *
 * <p>Uses Java 21 features: Pattern Matching.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class MovieCreatedEventHandler {

    /**
     * Handles MovieCreatedEvent.
     * Executes side effects for movie creation.
     *
     * @param event MovieCreatedEvent
     */
    @EventListener
    public void handle(MovieCreatedEvent event) {
    }
}

