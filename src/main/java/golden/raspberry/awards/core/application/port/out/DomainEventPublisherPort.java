package golden.raspberry.awards.core.application.port.out;

import golden.raspberry.awards.core.domain.model.event.MovieCreatedEvent;
import golden.raspberry.awards.core.domain.model.event.MovieDeletedEvent;
import golden.raspberry.awards.core.domain.model.event.MovieUpdatedEvent;

/**
 * Output port for publishing domain events.
 * Defines the contract for publishing domain events to external systems.
 *
 * <p>This port is part of the Application layer and defines
 * the contract for domain event publishing following hexagonal architecture principles.
 *
 * <p><strong>Hexagonal Architecture:</strong>
 * <ul>
 *   <li>Port defined by Application layer</li>
 *   <li>Implemented by Infrastructure adapters</li>
 *   <li>Enables event-driven communication</li>
 * </ul>
 *
 * <p>Uses Java 21 features: Records for events.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface DomainEventPublisherPort {

    /**
     * Publishes a MovieCreatedEvent.
     *
     * @param event MovieCreatedEvent to publish
     */
    void publish(MovieCreatedEvent event);

    /**
     * Publishes a MovieUpdatedEvent.
     *
     * @param event MovieUpdatedEvent to publish
     */
    void publish(MovieUpdatedEvent event);

    /**
     * Publishes a MovieDeletedEvent.
     *
     * @param event MovieDeletedEvent to publish
     */
    void publish(MovieDeletedEvent event);
}

