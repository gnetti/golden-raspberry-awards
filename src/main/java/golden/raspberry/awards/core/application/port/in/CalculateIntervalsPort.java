package golden.raspberry.awards.core.application.port.in;

import golden.raspberry.awards.core.domain.model.valueobject.ProducerIntervalResponse;

/**
 * Input Port for calculating producer intervals.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface CalculateIntervalsPort {
    /**
     * Calculates minimum and maximum intervals between consecutive wins.
     *
     * @return ProducerIntervalResponse with min and max intervals
     */
    ProducerIntervalResponse execute();
}

