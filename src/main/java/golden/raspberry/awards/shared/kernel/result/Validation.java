package golden.raspberry.awards.shared.kernel.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Functional validation using Result pattern.
 * Accumulates validation errors.
 *
 * <p>This provides a functional approach to validation that
 * accumulates all errors instead of failing on the first error.
 *
 * <p>Uses Java 21 features: Sealed Classes, Pattern Matching, Records, Streams.
 *
 * @param <T> Valid type
 * @param <E> Error type
 * @author Luiz Generoso
 * @since 1.0.0
 */
public sealed interface Validation<T, E> permits Validation.Valid, Validation.Invalid {

    /**
     * Valid validation result.
     *
     * @param <T> Valid type
     * @param <E> Error type
     */
    record Valid<T, E>(T value) implements Validation<T, E> {
        public Valid {
            Objects.requireNonNull(value, "Valid value cannot be null");
        }
    }

    /**
     * Invalid validation result with accumulated errors.
     *
     * @param <T> Valid type
     * @param <E> Error type
     */
    record Invalid<T, E>(List<E> errors) implements Validation<T, E> {
        public Invalid {
            Objects.requireNonNull(errors, "Errors cannot be null");
            if (errors.isEmpty()) {
                throw new IllegalArgumentException("Invalid validation must have at least one error");
            }
        }
    }

    /**
     * Creates a valid validation.
     *
     * @param value Valid value
     * @param <T> Valid type
     * @param <E> Error type
     * @return Valid validation
     */
    static <T, E> Validation<T, E> valid(T value) {
        return new Valid<>(value);
    }

    /**
     * Creates an invalid validation with a single error.
     *
     * @param error Error
     * @param <T> Valid type
     * @param <E> Error type
     * @return Invalid validation
     */
    static <T, E> Validation<T, E> invalid(E error) {
        Objects.requireNonNull(error, "Error cannot be null");
        return new Invalid<>(List.of(error));
    }

    /**
     * Creates an invalid validation with multiple errors.
     *
     * @param errors List of errors
     * @param <T> Valid type
     * @param <E> Error type
     * @return Invalid validation
     */
    static <T, E> Validation<T, E> invalid(List<E> errors) {
        Objects.requireNonNull(errors, "Errors cannot be null");
        if (errors.isEmpty()) {
            throw new IllegalArgumentException("Invalid validation must have at least one error");
        }
        return new Invalid<>(List.copyOf(errors));
    }

    /**
     * Checks if this validation is valid.
     *
     * @return true if valid, false otherwise
     */
    default boolean isValid() {
        return this instanceof Valid<T, E>;
    }

    /**
     * Checks if this validation is invalid.
     *
     * @return true if invalid, false otherwise
     */
    default boolean isInvalid() {
        return this instanceof Invalid<T, E>;
    }

    /**
     * Gets the value if valid.
     *
     * @return Optional containing value if valid
     */
    default java.util.Optional<T> getValue() {
        return switch (this) {
            case Valid<T, E>(T value) -> java.util.Optional.of(value);
            case Invalid<T, E> ignored -> java.util.Optional.empty();
        };
    }

    /**
     * Gets the errors if invalid.
     *
     * @return List of errors (empty if valid)
     */
    default List<E> getErrors() {
        return switch (this) {
            case Valid<T, E> ignored -> Collections.emptyList();
            case Invalid<T, E>(List<E> errors) -> errors;
        };
    }

    /**
     * Maps the value if valid.
     *
     * @param mapper Function to map value
     * @param <U> New valid type
     * @return New validation with mapped value
     */
    default <U> Validation<U, E> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "Mapper cannot be null");
        return switch (this) {
            case Valid<T, E>(T value) -> Validation.valid(mapper.apply(value));
            case Invalid<T, E>(List<E> errors) -> Validation.invalid(errors);
        };
    }

    /**
     * Maps the errors if invalid.
     *
     * @param mapper Function to map errors
     * @param <F> New error type
     * @return New validation with mapped errors
     */
    default <F> Validation<T, F> mapErrors(Function<? super E, ? extends F> mapper) {
        Objects.requireNonNull(mapper, "Mapper cannot be null");
        return switch (this) {
            case Valid<T, E>(T value) -> Validation.valid(value);
            case Invalid<T, E>(List<E> errors) -> Validation.invalid(
                    errors.stream().map(mapper).collect(Collectors.toList())
            );
        };
    }

    /**
     * Flat maps the value if valid.
     *
     * @param mapper Function that returns a validation
     * @param <U> New valid type
     * @return New validation
     */
    default <U> Validation<U, E> flatMap(Function<? super T, ? extends Validation<U, E>> mapper) {
        Objects.requireNonNull(mapper, "Mapper cannot be null");
        return switch (this) {
            case Valid<T, E>(T value) -> mapper.apply(value);
            case Invalid<T, E>(List<E> errors) -> Validation.invalid(errors);
        };
    }

    /**
     * Combines two validations, accumulating errors if both are invalid.
     *
     * @param other Other validation
     * @param combiner Function to combine valid values
     * @param <U> Other valid type
     * @param <R> Combined valid type
     * @return Combined validation
     */
    default <U, R> Validation<R, E> combine(Validation<U, E> other,
                                             Function<? super T, Function<? super U, ? extends R>> combiner) {
        Objects.requireNonNull(other, "Other validation cannot be null");
        Objects.requireNonNull(combiner, "Combiner cannot be null");

        return switch (this) {
            case Valid<T, E>(T value) -> switch (other) {
                case Valid<U, E>(U otherValue) -> Validation.valid(combiner.apply(value).apply(otherValue));
                case Invalid<U, E>(List<E> otherErrors) -> Validation.invalid(otherErrors);
            };
            case Invalid<T, E>(List<E> errors) -> switch (other) {
                case Valid<U, E> ignored -> Validation.invalid(errors);
                case Invalid<U, E>(List<E> otherErrors) -> {
                    var allErrors = new ArrayList<E>(errors);
                    allErrors.addAll(otherErrors);
                    yield Validation.invalid(allErrors);
                }
            };
        };
    }

    /**
     * Converts this validation to a Result.
     *
     * @return Result with value or first error
     */
    default Result<T, E> toResult() {
        return switch (this) {
            case Valid<T, E>(T value) -> Result.success(value);
            case Invalid<T, E>(List<E> errors) -> Result.failure(errors.get(0));
        };
    }

    /**
     * Converts this validation to a Result with all errors.
     *
     * @return Result with value or list of errors
     */
    default Result<T, List<E>> toResultWithAllErrors() {
        return switch (this) {
            case Valid<T, E>(T value) -> Result.success(value);
            case Invalid<T, E>(List<E> errors) -> Result.failure(errors);
        };
    }
}

