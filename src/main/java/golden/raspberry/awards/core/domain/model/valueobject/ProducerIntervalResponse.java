package golden.raspberry.awards.core.domain.model.valueobject;

import java.util.List;
import java.util.Objects;

/**
 * Response containing minimum and maximum intervals for producers.
 *
 * @param min List of producers with minimum intervals
 * @param max List of producers with maximum intervals
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record ProducerIntervalResponse(
        List<ProducerInterval> min,
        List<ProducerInterval> max
) {
    /**
     * Compact constructor with validation.
     *
     * @param min List of producers with minimum intervals
     * @param max List of producers with maximum intervals
     * @throws NullPointerException if any parameter is null
     */
    public ProducerIntervalResponse {
        Objects.requireNonNull(min, "Min list cannot be null");
        Objects.requireNonNull(max, "Max list cannot be null");
    }
}

