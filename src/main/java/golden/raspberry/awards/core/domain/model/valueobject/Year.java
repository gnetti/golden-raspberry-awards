package golden.raspberry.awards.core.domain.model.valueobject;

import java.time.Year;
import java.util.Objects;

/**
 * Value object representing a year.
 * Immutable value object with business rules.
 *
 * <p>This value object is part of the Domain layer and encapsulates
 * the concept of a year following hexagonal architecture principles.
 *
 * <p>Uses Java 21 features: Records, Objects.requireNonNull.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record YearValue(Integer value) {
    private static final int MIN_YEAR = 1900;

    /**
     * Compact constructor for validation.
     */
    public YearValue {
        Objects.requireNonNull(value, "Year value cannot be null");
        if (value < MIN_YEAR) {
            throw new IllegalArgumentException("Year must be at least %d".formatted(MIN_YEAR));
        }
        if (value > Year.now().getValue()) {
            throw new IllegalArgumentException("Year cannot be in the future");
        }
    }

    /**
     * Factory method to create a YearValue.
     *
     * @param value Integer value
     * @return YearValue instance
     */
    public static YearValue of(Integer value) {
        return new YearValue(value);
    }
}

