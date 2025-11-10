package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.in.CalculateIntervalsPort;
import golden.raspberry.awards.core.application.service.IntervalProcessorService;
import golden.raspberry.awards.core.domain.model.valueobject.ProducerIntervalResponse;

import java.util.Objects;

/**
 * Use Case for calculating producer intervals.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record CalculateIntervalsPortHandler(
        IntervalProcessorService calculationService) implements CalculateIntervalsPort {

    /**
     * Constructor for dependency injection.
     *
     * @param calculationService Application service for orchestrating interval calculations
     */
    public CalculateIntervalsPortHandler(IntervalProcessorService calculationService) {
        this.calculationService = Objects.requireNonNull(calculationService, "IntervalProcessorService cannot be null");
    }

    @Override
    public ProducerIntervalResponse execute() {
        return calculationService.calculate();
    }
}

