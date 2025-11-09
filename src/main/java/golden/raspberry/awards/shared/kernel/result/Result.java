package golden.raspberry.awards.shared.kernel.result;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Result pattern for functional error handling.
 * Eliminates exceptions for control flow.
 *
 * <p>This is a functional approach to error handling that makes
 * errors explicit in the type system, following functional programming principles.
 *
 * <p>Uses Java 21 features: Sealed Classes, Pattern Matching, Records.
 *
 * @param <T> Success type
 * @param <E> Error type
 * @author Luiz Generoso
 * @since 1.0.0
 */
public sealed interface Result<T, E> permits Result.Success, Result.Failure {

    /**
     * Success result containing a value.
     *
     * @param <T> Value type
     * @param <E> Error type
     */
    record Success<T, E>(T value) implements Result<T, E> {
        public Success {
            Objects.requireNonNull(value, "Success value cannot be null");
        }
    }

    /**
     * Failure result containing an error.
     *
     * @param <T> Value type
     * @param <E> Error type
     */
    record Failure<T, E>(E error) implements Result<T, E> {
        public Failure {
            Objects.requireNonNull(error, "Failure error cannot be null");
        }
    }

    /**
     * Creates a success result.
     *
     * @param value Success value
     * @param <T> Value type
     * @param <E> Error type
     * @return Success result
     */
    static <T, E> Result<T, E> success(T value) {
        return new Success<>(value);
    }

    /**
     * Creates a failure result.
     *
     * @param error Error value
     * @param <T> Value type
     * @param <E> Error type
     * @return Failure result
     */
    static <T, E> Result<T, E> failure(E error) {
        return new Failure<>(error);
    }

    /**
     * Checks if this result is a success.
     *
     * @return true if success, false otherwise
     */
    default boolean isSuccess() {
        return this instanceof Success<T, E>;
    }

    /**
     * Checks if this result is a failure.
     *
     * @return true if failure, false otherwise
     */
    default boolean isFailure() {
        return this instanceof Failure<T, E>;
    }

    /**
     * Gets the value if success, otherwise returns empty.
     *
     * @return Optional containing value if success
     */
    default Optional<T> getValue() {
        return switch (this) {
            case Success<T, E>(T value) -> Optional.of(value);
            case Failure<T, E> ignored -> Optional.empty();
        };
    }

    /**
     * Gets the error if failure, otherwise returns empty.
     *
     * @return Optional containing error if failure
     */
    default Optional<E> getError() {
        return switch (this) {
            case Success<T, E> ignored -> Optional.empty();
            case Failure<T, E>(E error) -> Optional.of(error);
        };
    }

    /**
     * Maps the value if success.
     *
     * @param mapper Function to map value
     * @param <U> New value type
     * @return New result with mapped value
     */
    default <U> Result<U, E> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper, "Mapper cannot be null");
        return switch (this) {
            case Success<T, E>(T value) -> Result.success(mapper.apply(value));
            case Failure<T, E>(E error) -> Result.failure(error);
        };
    }

    /**
     * Maps the error if failure.
     *
     * @param mapper Function to map error
     * @param <F> New error type
     * @return New result with mapped error
     */
    default <F> Result<T, F> mapError(Function<? super E, ? extends F> mapper) {
        Objects.requireNonNull(mapper, "Mapper cannot be null");
        return switch (this) {
            case Success<T, E>(T value) -> Result.success(value);
            case Failure<T, E>(E error) -> Result.failure(mapper.apply(error));
        };
    }

    /**
     * Flat maps the value if success.
     *
     * @param mapper Function that returns a result
     * @param <U> New value type
     * @return New result
     */
    default <U> Result<U, E> flatMap(Function<? super T, ? extends Result<U, E>> mapper) {
        Objects.requireNonNull(mapper, "Mapper cannot be null");
        return switch (this) {
            case Success<T, E>(T value) -> mapper.apply(value);
            case Failure<T, E>(E error) -> Result.failure(error);
        };
    }

    /**
     * Executes consumer if success.
     *
     * @param consumer Consumer for success value
     * @return This result for chaining
     */
    default Result<T, E> peek(Consumer<? super T> consumer) {
        Objects.requireNonNull(consumer, "Consumer cannot be null");
        if (this instanceof Success<T, E>(T value)) {
            consumer.accept(value);
        }
        return this;
    }

    /**
     * Executes consumer if failure.
     *
     * @param consumer Consumer for error
     * @return This result for chaining
     */
    default Result<T, E> peekError(Consumer<? super E> consumer) {
        Objects.requireNonNull(consumer, "Consumer cannot be null");
        if (this instanceof Failure<T, E>(E error)) {
            consumer.accept(error);
        }
        return this;
    }

    /**
     * Gets the value if success, otherwise returns default value.
     *
     * @param defaultValue Default value
     * @return Value or default
     */
    default T orElse(T defaultValue) {
        Objects.requireNonNull(defaultValue, "Default value cannot be null");
        return switch (this) {
            case Success<T, E>(T value) -> value;
            case Failure<T, E> ignored -> defaultValue;
        };
    }

    /**
     * Gets the value if success, otherwise throws exception.
     *
     * @param exceptionSupplier Supplier for exception
     * @param <X> Exception type
     * @return Value if success
     * @throws X if failure
     */
    default <X extends Throwable> T orElseThrow(Function<? super E, ? extends X> exceptionSupplier) throws X {
        Objects.requireNonNull(exceptionSupplier, "Exception supplier cannot be null");
        return switch (this) {
            case Success<T, E>(T value) -> value;
            case Failure<T, E>(E error) -> {
                throw exceptionSupplier.apply(error);
            }
        };
    }
}

