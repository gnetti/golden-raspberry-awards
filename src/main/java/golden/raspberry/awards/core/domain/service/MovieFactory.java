package golden.raspberry.awards.core.domain.service;

import golden.raspberry.awards.core.domain.model.Movie;
import golden.raspberry.awards.core.domain.model.MovieWithId;
import golden.raspberry.awards.shared.constant.BusinessRules;

import java.util.Objects;

/**
 * Factory for creating Movie aggregates.
 * Encapsulates creation logic and business rules.
 *
 * <p>This factory is part of the Domain layer and contains
 * pure business logic for creating movie aggregates following hexagonal architecture principles.
 *
 * <p><strong>Domain Factory Rules:</strong>
 * <ul>
 *   <li>Contains creation logic with business rules</li>
 *   <li>Zero external dependencies</li>
 *   <li>Validates business rules during creation</li>
 *   <li>Returns domain entities</li>
 * </ul>
 *
 * <p>Uses Java 21 features: Objects.requireNonNull, String Templates.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public class MovieFactory {

    /**
     * Creates a Movie aggregate from raw data.
     * Validates business rules during creation.
     *
     * @param year Movie release year
     * @param title Movie title
     * @param studios Movie studios
     * @param producers Movie producers
     * @param winner Whether the movie is a winner
     * @return Created Movie aggregate
     * @throws IllegalArgumentException if validation fails
     */
    public Movie create(Integer year, String title, String studios, String producers, Boolean winner) {
        Objects.requireNonNull(year, "Year cannot be null");
        Objects.requireNonNull(title, "Title cannot be null");
        Objects.requireNonNull(studios, "Studios cannot be null");
        Objects.requireNonNull(producers, "Producers cannot be null");
        Objects.requireNonNull(winner, "Winner cannot be null");

        var trimmedTitle = title.trim();
        var trimmedStudios = studios.trim();
        var trimmedProducers = producers.trim();

        validateYear(year);
        validateTitle(trimmedTitle);
        validateStudios(trimmedStudios);
        validateProducers(trimmedProducers);

        return new Movie(year, trimmedTitle, trimmedStudios, trimmedProducers, winner);
    }

    /**
     * Creates a MovieWithId aggregate from raw data.
     * Validates business rules during creation.
     *
     * @param id Movie ID
     * @param year Movie release year
     * @param title Movie title
     * @param studios Movie studios
     * @param producers Movie producers
     * @param winner Whether the movie is a winner
     * @return Created MovieWithId aggregate
     * @throws IllegalArgumentException if validation fails
     */
    public MovieWithId createWithId(Long id, Integer year, String title, String studios, String producers, Boolean winner) {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(year, "Year cannot be null");
        Objects.requireNonNull(title, "Title cannot be null");
        Objects.requireNonNull(studios, "Studios cannot be null");
        Objects.requireNonNull(producers, "Producers cannot be null");
        Objects.requireNonNull(winner, "Winner cannot be null");

        if (id <= 0) {
            throw new IllegalArgumentException("ID must be positive, but was: %d".formatted(id));
        }

        var trimmedTitle = title.trim();
        var trimmedStudios = studios.trim();
        var trimmedProducers = producers.trim();

        validateYear(year);
        validateTitle(trimmedTitle);
        validateStudios(trimmedStudios);
        validateProducers(trimmedProducers);

        return new MovieWithId(id, year, trimmedTitle, trimmedStudios, trimmedProducers, winner);
    }

    /**
     * Validates year according to business rules.
     *
     * @param year Year to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateYear(Integer year) {
        if (year < BusinessRules.MIN_YEAR || year > BusinessRules.MAX_YEAR) {
            throw new IllegalArgumentException(
                    "Year must be between %d and %d, but was: %d".formatted(
                            BusinessRules.MIN_YEAR, BusinessRules.MAX_YEAR, year
                    )
            );
        }
    }

    /**
     * Validates title according to business rules.
     *
     * @param title Title to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateTitle(String title) {
        if (title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be blank");
        }
        if (title.length() < BusinessRules.MIN_TITLE_LENGTH) {
            throw new IllegalArgumentException(
                    "Title must be at least %d characters, but was: %d".formatted(
                            BusinessRules.MIN_TITLE_LENGTH, title.length()
                    )
            );
        }
        if (title.length() > BusinessRules.MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException(
                    "Title must be at most %d characters, but was: %d".formatted(
                            BusinessRules.MAX_TITLE_LENGTH, title.length()
                    )
            );
        }
    }

    /**
     * Validates studios according to business rules.
     *
     * @param studios Studios to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateStudios(String studios) {
        if (studios.isBlank()) {
            throw new IllegalArgumentException("Studios cannot be blank");
        }
        if (studios.length() < BusinessRules.MIN_STUDIOS_LENGTH) {
            throw new IllegalArgumentException(
                    "Studios must be at least %d characters, but was: %d".formatted(
                            BusinessRules.MIN_STUDIOS_LENGTH, studios.length()
                    )
            );
        }
        if (studios.length() > BusinessRules.MAX_STUDIOS_LENGTH) {
            throw new IllegalArgumentException(
                    "Studios must be at most %d characters, but was: %d".formatted(
                            BusinessRules.MAX_STUDIOS_LENGTH, studios.length()
                    )
            );
        }
    }

    /**
     * Validates producers according to business rules.
     *
     * @param producers Producers to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateProducers(String producers) {
        if (producers.isBlank()) {
            throw new IllegalArgumentException("Producers cannot be blank");
        }
        if (producers.length() < BusinessRules.MIN_PRODUCERS_LENGTH) {
            throw new IllegalArgumentException(
                    "Producers must be at least %d characters, but was: %d".formatted(
                            BusinessRules.MIN_PRODUCERS_LENGTH, producers.length()
                    )
            );
        }
        if (producers.length() > BusinessRules.MAX_PRODUCERS_LENGTH) {
            throw new IllegalArgumentException(
                    "Producers must be at most %d characters, but was: %d".formatted(
                            BusinessRules.MAX_PRODUCERS_LENGTH, producers.length()
                    )
            );
        }
    }
}

