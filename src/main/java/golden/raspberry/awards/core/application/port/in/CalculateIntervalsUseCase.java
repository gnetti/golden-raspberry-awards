package golden.raspberry.awards.core.application.port.in;

import golden.raspberry.awards.core.domain.model.ProducerIntervalResponse;

/**
 * Input Port (Use Case) for calculating producer intervals.
 * Defined by Application layer, called by Adapters.
 * Pure interface - no Spring dependencies.
 *
 * <p>This port defines the contract for calculating minimum and maximum
 * intervals between consecutive wins for producers.
 *
 * <p>Uses Java 21 features: clean interfaces, Records for responses.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface CalculateIntervalsUseCase {
    /**
     * Calculates minimum and maximum intervals between consecutive wins.
     *
     * @return ProducerIntervalResponse with min and max intervals
     */
    ProducerIntervalResponse execute();
}

