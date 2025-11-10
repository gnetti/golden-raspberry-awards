package golden.raspberry.awards.core.domain.model.valueobject;

import org.springframework.lang.NonNull;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Represents an interval between two consecutive wins for a producer.
 *
 * @param producer     Producer name
 * @param interval     Interval in years between consecutive wins
 * @param previousWin  Year of the previous win
 * @param followingWin Year of the following win
 * @author Luiz Generoso
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
     *
     * @throws IllegalArgumentException if any validation fails
     */
    public ProducerInterval {
        Objects.requireNonNull(producer, "Producer cannot be null");
        Objects.requireNonNull(interval, "Interval cannot be null");
        Objects.requireNonNull(previousWin, "PreviousWin cannot be null");
        Objects.requireNonNull(followingWin, "FollowingWin cannot be null");

        validateNotBlank(producer);
        validateNonNegative(interval);
        validateFollowingWinGreaterThanPrevious(followingWin, previousWin);
        validateIntervalMatchesCalculation(interval, previousWin, followingWin);
    }

    /**
     * Validates that a string field is not blank.
     *
     * @param value String value to validate
     * @throws IllegalArgumentException if value is blank
     */
    private static void validateNotBlank(String value) {
        Optional.of(value)
                .filter(Predicate.not(String::isBlank))
                .orElseThrow(() -> new IllegalArgumentException("%s cannot be blank".formatted("Producer")));
    }

    /**
     * Validates that interval is non-negative.
     *
     * @param interval Interval to validate
     * @throws IllegalArgumentException if interval is negative
     */
    private static void validateNonNegative(Integer interval) {
        Optional.of(interval)
                .filter(i -> i >= 0)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Interval must be non-negative, but was: %d".formatted(interval)
                ));
    }

    /**
     * Validates that followingWin is greater than previousWin.
     *
     * @param followingWin Following win year
     * @param previousWin  Previous win year
     * @throws IllegalArgumentException if followingWin is not greater than previousWin
     */
    private static void validateFollowingWinGreaterThanPrevious(Integer followingWin, Integer previousWin) {
        Optional.of(followingWin)
                .filter(fw -> fw > previousWin)
                .orElseThrow(() -> new IllegalArgumentException(
                        "FollowingWin (%d) must be greater than PreviousWin (%d), but was not"
                                .formatted(followingWin, previousWin)
                ));
    }

    /**
     * Validates that interval matches the calculated difference between wins.
     *
     * @param interval     Interval to validate
     * @param previousWin  Previous win year
     * @param followingWin Following win year
     * @throws IllegalArgumentException if interval does not match calculated difference
     */
    private static void validateIntervalMatchesCalculation(Integer interval, Integer previousWin, Integer followingWin) {
        var calculatedInterval = followingWin - previousWin;
        Optional.of(interval)
                .filter(i -> i.equals(calculatedInterval))
                .orElseThrow(() -> new IllegalArgumentException(
                        "Interval (%d) must equal the difference between followingWin (%d) and previousWin (%d), but calculated interval is %d"
                                .formatted(interval, followingWin, previousWin, calculatedInterval)
                ));
    }

    /**
     * Static factory method to create ProducerInterval by automatically calculating the interval.
     *
     * @param producer     Producer name
     * @param previousWin  Year of the previous win
     * @param followingWin Year of the following win
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
     * @return Formatted string representation
     */
    @Override
    @NonNull
    public String toString() {
        return "ProducerInterval{producer='%s', interval=%d years (%d -> %d)}"
                .formatted(producer, interval, previousWin, followingWin);
    }
}
