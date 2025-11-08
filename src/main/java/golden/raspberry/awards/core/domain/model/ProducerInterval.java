package golden.raspberry.awards.core.domain.model;

import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * Represents an interval between two consecutive wins for a producer.
 * Using Java 21 record for immutability and elegance.
 * 
 * @param producer Producer name
 * @param interval Interval in years between consecutive wins
 * @param previousWin Year of the previous win
 * @param followingWin Year of the following win
 */
public record ProducerInterval(
    @NonNull String producer,
    @NonNull Integer interval,
    @NonNull Integer previousWin,
    @NonNull Integer followingWin
) {
    public ProducerInterval {

        Objects.requireNonNull(producer, "Producer cannot be null");
        Objects.requireNonNull(interval, "Interval cannot be null");
        Objects.requireNonNull(previousWin, "PreviousWin cannot be null");
        Objects.requireNonNull(followingWin, "FollowingWin cannot be null");
        

        if (producer.isBlank()) {
            throw new IllegalArgumentException("Producer cannot be blank");
        }
        if (interval < 0) {
            throw new IllegalArgumentException("Interval must be non-negative");
        }
        if (followingWin <= previousWin) {
            throw new IllegalArgumentException(
                "FollowingWin (%d) must be greater than PreviousWin (%d)"
                    .formatted(followingWin, previousWin)
            );
        }

        var calculatedInterval = followingWin - previousWin;
        if (!interval.equals(calculatedInterval)) {
            throw new IllegalArgumentException(
                "Interval (%d) must equal the difference between followingWin (%d) and previousWin (%d)"
                    .formatted(interval, followingWin, previousWin)
            );
        }
    }
    
    /**
     * Factory method to create ProducerInterval by automatically calculating the interval.
     * More elegant and safe - avoids manual calculation errors.
     * 
     * @param producer Producer name
     * @param previousWin Year of the previous win
     * @param followingWin Year of the following win
     * @return ProducerInterval with automatically calculated interval
     * @throws IllegalArgumentException if validation fails
     */
    public static ProducerInterval of(String producer, Integer previousWin, Integer followingWin) {
        Objects.requireNonNull(producer, "Producer cannot be null");
        Objects.requireNonNull(previousWin, "PreviousWin cannot be null");
        Objects.requireNonNull(followingWin, "FollowingWin cannot be null");
        
        var interval = followingWin - previousWin;
        return new ProducerInterval(producer, interval, previousWin, followingWin);
    }
}
