package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.in.CalculateIntervalsUseCase;
import golden.raspberry.awards.core.application.service.ProducerIntervalCalculationService;
import golden.raspberry.awards.core.domain.model.ProducerIntervalResponse;

import java.util.Objects;

/**
 * Use Case for calculating producer intervals.
 * Delegates to application service for orchestration.
 *
 * <p>This use case is part of the Application layer and delegates
 * to application service following hexagonal architecture principles.
 *
 * <p><strong>Flow:</strong>
 * <pre>
 * Adapter IN (Controller)
 *     ↓
 * CalculateIntervalsUseCase (this - Application Handler)
 *     ↓
 * ProducerIntervalCalculationService (Application - Service)
 *     ↓
 * ProducerIntervalResponse
 * </pre>
 *
 * <p>Uses Java 21 features: Objects.requireNonNull.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record CalculateIntervalsUseCaseHandler(
        ProducerIntervalCalculationService calculationService) implements CalculateIntervalsUseCase {

    /**
     * Constructor for dependency injection.
     *
     * @param calculationService Application service for orchestrating interval calculations
     */
    public CalculateIntervalsUseCaseHandler(ProducerIntervalCalculationService calculationService) {
        this.calculationService = Objects.requireNonNull(calculationService, "ProducerIntervalCalculationService cannot be null");
    }

    @Override
    public ProducerIntervalResponse execute() {
        return calculationService.calculate();
    }
}

