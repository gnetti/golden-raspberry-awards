package golden.raspberry.awards.shared.kernel.result;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/**
 * Either monad for alternative results.
 * Represents a value that can be either Left or Right.
 *
 * <p>This is a functional approach to representing alternative outcomes,
 * commonly used for error handling or validation.
 *
 * <p>Uses Java 21 features: Sealed Classes, Pattern Matching, Records.
 *
 * @param <L> Left type (typically error)
 * @param <R> Right type (typically success)
 * @author Luiz Generoso
 * @since 1.0.0
 */
public sealed interface Either<L, R> permits Either.Left, Either.Right {

    /**
     * Left side of Either (typically error).
     *
     * @param <L> Left type
     * @param <R> Right type
     */
    record Left<L, R>(L value) implements Either<L, R> {
        public Left {
            Objects.requireNonNull(value, "Left value cannot be null");
        }
    }

    /**
     * Right side of Either (typically success).
     *
     * @param <L> Left type
     * @param <R> Right type
     */
    record Right<L, R>(R value) implements Either<L, R> {
        public Right {
            Objects.requireNonNull(value, "Right value cannot be null");
        }
    }

    /**
     * Creates a Left Either.
     *
     * @param value Left value
     * @param <L> Left type
     * @param <R> Right type
     * @return Left Either
     */
    static <L, R> Either<L, R> left(L value) {
        return new Left<>(value);
    }

    /**
     * Creates a Right Either.
     *
     * @param value Right value
     * @param <L> Left type
     * @param <R> Right type
     * @return Right Either
     */
    static <L, R> Either<L, R> right(R value) {
        return new Right<>(value);
    }

    /**
     * Checks if this is a Left.
     *
     * @return true if Left, false otherwise
     */
    default boolean isLeft() {
        return this instanceof Left<L, R>;
    }

    /**
     * Checks if this is a Right.
     *
     * @return true if Right, false otherwise
     */
    default boolean isRight() {
        return this instanceof Right<L, R>;
    }

    /**
     * Gets the Left value if present.
     *
     * @return Optional containing Left value
     */
    default Optional<L> getLeft() {
        return switch (this) {
            case Left<L, R>(L value) -> Optional.of(value);
            case Right<L, R> ignored -> Optional.empty();
        };
    }

    /**
     * Gets the Right value if present.
     *
     * @return Optional containing Right value
     */
    default Optional<R> getRight() {
        return switch (this) {
            case Left<L, R> ignored -> Optional.empty();
            case Right<L, R>(R value) -> Optional.of(value);
        };
    }

    /**
     * Maps the Right value.
     *
     * @param mapper Function to map Right value
     * @param <R2> New Right type
     * @return New Either with mapped Right value
     */
    default <R2> Either<L, R2> map(Function<? super R, ? extends R2> mapper) {
        Objects.requireNonNull(mapper, "Mapper cannot be null");
        return switch (this) {
            case Left<L, R>(L value) -> Either.left(value);
            case Right<L, R>(R value) -> Either.right(mapper.apply(value));
        };
    }

    /**
     * Maps the Left value.
     *
     * @param mapper Function to map Left value
     * @param <L2> New Left type
     * @return New Either with mapped Left value
     */
    default <L2> Either<L2, R> mapLeft(Function<? super L, ? extends L2> mapper) {
        Objects.requireNonNull(mapper, "Mapper cannot be null");
        return switch (this) {
            case Left<L, R>(L value) -> Either.left(mapper.apply(value));
            case Right<L, R>(R value) -> Either.right(value);
        };
    }

    /**
     * Flat maps the Right value.
     *
     * @param mapper Function that returns an Either
     * @param <R2> New Right type
     * @return New Either
     */
    default <R2> Either<L, R2> flatMap(Function<? super R, ? extends Either<L, R2>> mapper) {
        Objects.requireNonNull(mapper, "Mapper cannot be null");
        return switch (this) {
            case Left<L, R>(L value) -> Either.left(value);
            case Right<L, R>(R value) -> mapper.apply(value);
        };
    }

    /**
     * Folds this Either into a single value.
     *
     * @param leftMapper Function for Left value
     * @param rightMapper Function for Right value
     * @param <T> Result type
     * @return Folded value
     */
    default <T> T fold(Function<? super L, ? extends T> leftMapper,
                       Function<? super R, ? extends T> rightMapper) {
        Objects.requireNonNull(leftMapper, "Left mapper cannot be null");
        Objects.requireNonNull(rightMapper, "Right mapper cannot be null");
        return switch (this) {
            case Left<L, R>(L value) -> leftMapper.apply(value);
            case Right<L, R>(R value) -> rightMapper.apply(value);
        };
    }
}

