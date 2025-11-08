package golden.raspberry.awards.core.domain.model;

import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * Represents an interval between two consecutive wins for a producer.
 *
 * <p>Immutable Value Object using Java 21 Record for elegance and robustness.
 *
 * <p>Uses Java 21 features:
 * <ul>
 *   <li>Records for immutability</li>
 *   <li>String Templates for error messages</li>
 *   <li>Compact constructor for validation</li>
 *   <li>Static factory method for safe creation</li>
 * </ul>
 *
 * @param producer     Producer name (non-null, non-blank)
 * @param interval     Interval in years between consecutive wins (non-null, non-negative)
 * @param previousWin  Year of the previous win (non-null)
 * @param followingWin Year of the following win (non-null, must be greater than previousWin)
 * @author Golden Raspberry Awards Team
 * @since 1.0.0
 */
public record ProducerInterval(
        String producer,
        Integer interval,
        Integer previousWin,
        Integer followingWin
) {

    /**
     * Compact constructor for validation.
     * Ensures all invariants are maintained.
     *
     * <p>Validations:
     * <ul>
     *   <li>All fields must be non-null</li>
     *   <li>Producer name must not be blank</li>
     *   <li>Interval must be non-negative</li>
     *   <li>FollowingWin must be greater than PreviousWin</li>
     *   <li>Interval must equal the difference between followingWin and previousWin</li>
     * </ul>
     *
     * @throws IllegalArgumentException if any validation fails
     */
    public ProducerInterval {
        // Validate non-null constraints
        Objects.requireNonNull(producer, "Producer cannot be null");
        Objects.requireNonNull(interval, "Interval cannot be null");
        Objects.requireNonNull(previousWin, "PreviousWin cannot be null");
        Objects.requireNonNull(followingWin, "FollowingWin cannot be null");

        // Validate business rules with descriptive error messages using String Templates
        if (producer.isBlank()) {
            throw new IllegalArgumentException("Producer cannot be blank");
        }

        if (interval < 0) {
            throw new IllegalArgumentException(
                    "Interval must be non-negative, but was: %d".formatted(interval)
            );
        }

        if (followingWin <= previousWin) {
            throw new IllegalArgumentException(
                    "FollowingWin (%d) must be greater than PreviousWin (%d), but was not"
                            .formatted(followingWin, previousWin)
            );
        }

        // Validate interval consistency
        var calculatedInterval = followingWin - previousWin;
        if (!interval.equals(calculatedInterval)) {
            throw new IllegalArgumentException(
                    "Interval (%d) must equal the difference between followingWin (%d) and previousWin (%d), but calculated interval is %d"
                            .formatted(interval, followingWin, previousWin, calculatedInterval)
            );
        }
    }

    /**
     * Static factory method to create ProducerInterval by automatically calculating the interval.
     *
     * <p>This is the recommended way to create instances as it:
     * <ul>
     *   <li>Automatically calculates the interval, avoiding manual calculation errors</li>
     *   <li>Provides a cleaner API</li>
     *   <li>Ensures consistency between interval and year values</li>
     * </ul>
     *
     * <p>Example usage:
     * <pre>{@code
     * var interval = ProducerInterval.of("Producer Name", 2010, 2015);
     * // Creates ProducerInterval with interval = 5
     * }</pre>
     *
     * @param producer     Producer name (non-null, non-blank)
     * @param previousWin  Year of the previous win (non-null)
     * @param followingWin Year of the following win (non-null, must be greater than previousWin)
     * @return ProducerInterval with automatically calculated interval
     * @throws NullPointerException     if any parameter is null
     * @throws IllegalArgumentException if validation fails
     */
    public static ProducerInterval of(String producer, Integer previousWin, Integer followingWin) {
        Objects.requireNonNull(producer, "Producer cannot be null");
        Objects.requireNonNull(previousWin, "PreviousWin cannot be null");
        Objects.requireNonNull(followingWin, "FollowingWin cannot be null");

        var interval = followingWin - previousWin;
        return new ProducerInterval(producer, interval, previousWin, followingWin);
    }

    /**
     * Returns a human-readable string representation of this interval.
     *
     * <p>Uses String Templates (Java 21) for elegant formatting.
     *
     * @return Formatted string representation
     */
    @Override
    @NonNull
    public String toString() {
        return "ProducerInterval{producer='%s', interval=%d years (%d -> %d)}"
                .formatted(producer, interval, previousWin, followingWin);
    }

    /**
     * Checks if this interval is the minimum possible (1 year).
     *
     * @return true if interval is 1 year, false otherwise
     */
    public boolean isMinimumInterval() {
        return interval == 1;
    }

    /**
     * Checks if this interval is greater than a given threshold.
     *
     * @param threshold Threshold to compare against
     * @return true if this interval is greater than threshold, false otherwise
     */
    public boolean isGreaterThan(int threshold) {
        return interval > threshold;
    }

    /**
     * Checks if this interval is less than a given threshold.
     *
     * @param threshold Threshold to compare against
     * @return true if this interval is less than threshold, false otherwise
     */
    public boolean isLessThan(int threshold) {
        return interval < threshold;
    }
}
