package golden.raspberry.awards.core.domain.model;

import java.util.List;

/**
 * Response containing minimum and maximum intervals for producers.
 * Using Java 21 record for immutability.
 * 
 * @param min List of producers with minimum intervals
 * @param max List of producers with maximum intervals
 */
public record ProducerIntervalResponse(
    List<ProducerInterval> min,
    List<ProducerInterval> max
) {
    public ProducerIntervalResponse {
        if (min == null) {
            throw new IllegalArgumentException("Min list cannot be null");
        }
        if (max == null) {
            throw new IllegalArgumentException("Max list cannot be null");
        }
    }
}

