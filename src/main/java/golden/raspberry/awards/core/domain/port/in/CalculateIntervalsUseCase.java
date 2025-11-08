package golden.raspberry.awards.core.domain.port.in;

import golden.raspberry.awards.core.domain.model.ProducerIntervalResponse;

/**
 * Input Port (Use Case) for calculating producer intervals.
 * Defined by Domain, called by Adapters.
 * Pure interface - no Spring dependencies.
 */
public interface CalculateIntervalsUseCase {
    /**
     * Calculates minimum and maximum intervals between consecutive wins.
     * @return ProducerIntervalResponse with min and max intervals
     */
    ProducerIntervalResponse execute();
}

