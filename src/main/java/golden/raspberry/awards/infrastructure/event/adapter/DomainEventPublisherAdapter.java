package golden.raspberry.awards.infrastructure.event.adapter;

import golden.raspberry.awards.core.application.port.out.DomainEventPublisherPort;
import golden.raspberry.awards.core.domain.model.event.MovieCreatedEvent;
import golden.raspberry.awards.core.domain.model.event.MovieDeletedEvent;
import golden.raspberry.awards.core.domain.model.event.MovieUpdatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Infrastructure adapter for publishing domain events.
 * Implements DomainEventPublisherPort using Spring ApplicationEventPublisher.
 *
 * <p>This adapter is part of the Infrastructure layer and implements
 * the domain event publishing port following hexagonal architecture principles.
 *
 * <p><strong>Hexagonal Architecture:</strong>
 * <ul>
 *   <li>Implements Port defined by Application layer</li>
 *   <li>Uses Spring ApplicationEventPublisher for event publishing</li>
 *   <li>Translates domain events to Spring events</li>
 * </ul>
 *
 * <p>Uses Java 21 features: Objects.requireNonNull.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class DomainEventPublisherAdapter implements DomainEventPublisherPort {

    private final ApplicationEventPublisher eventPublisher;

    /**
     * Constructor for dependency injection.
     *
     * @param eventPublisher Spring ApplicationEventPublisher
     */
    public DomainEventPublisherAdapter(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "ApplicationEventPublisher cannot be null");
    }

    @Override
    public void publish(MovieCreatedEvent event) {
        Objects.requireNonNull(event, "MovieCreatedEvent cannot be null");
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publish(MovieUpdatedEvent event) {
        Objects.requireNonNull(event, "MovieUpdatedEvent cannot be null");
        eventPublisher.publishEvent(event);
    }

    @Override
    public void publish(MovieDeletedEvent event) {
        Objects.requireNonNull(event, "MovieDeletedEvent cannot be null");
        eventPublisher.publishEvent(event);
    }
}

