package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.in.CalculateIntervalsUseCase;
import golden.raspberry.awards.core.domain.model.ProducerIntervalResponse;
import golden.raspberry.awards.core.domain.service.ProducerIntervalService;

import java.util.Objects;

/**
 * Use Case for calculating producer intervals.
 * Orchestrates the Domain Service to fulfill the use case.
 *
 * <p>This use case is part of the Application layer and orchestrates
 * the calculation of producer intervals following hexagonal architecture principles.
 *
 * <p><strong>Flow:</strong>
 * <pre>
 * Adapter IN (Controller)
 *     ↓
 * CalculateIntervalsUseCase (this - Application)
 *     ↓
 * ProducerIntervalService (Domain)
 *     ↓
 * MovieRepositoryPort (Domain - Port OUT)
 * </pre>
 *
 * <p>Uses Java 21 features: var, Objects.requireNonNull.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record CalculateIntervalsUseCaseImpl(
        ProducerIntervalService producerIntervalService) implements CalculateIntervalsUseCase {

    /**
     * Constructor for dependency injection.
     *
     * @param producerIntervalService Domain service for calculating intervals
     */
    public CalculateIntervalsUseCaseImpl(ProducerIntervalService producerIntervalService) {
        this.producerIntervalService = Objects.requireNonNull(
                producerIntervalService,
                "ProducerIntervalService cannot be null"
        );
    }

    @Override
    public ProducerIntervalResponse execute() {
        return producerIntervalService.calculateIntervals();
    }
}

