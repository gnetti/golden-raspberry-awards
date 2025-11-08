package golden.raspberry.awards.core.domain.model;

import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * Domain model representing a Movie.
 * Pure Java Record (Java 21) - no Spring dependencies.
 * Uses Java 21 features: Records, String Templates for toString.
 *
 * @author Golden Raspberry Awards Team
 * @since 1.0.0
 */
public record Movie(Integer year, String title, String studios, String producers, Boolean winner) {
    public Movie(Integer year, String title, String studios, String producers, Boolean winner) {
        this.year = Objects.requireNonNull(year, "Year cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.studios = Objects.requireNonNull(studios, "Studios cannot be null");
        this.producers = Objects.requireNonNull(producers, "Producers cannot be null");
        this.winner = Objects.requireNonNullElse(winner, false);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(year, movie.year) &&
                Objects.equals(title, movie.title);
    }

    @Override
    public int hashCode() {
        return Objects.hash(year, title);
    }

    @Override
    @NonNull
    public String toString() {
        return "Movie{year=%d, title='%s', winner=%s}".formatted(year, title, winner);
    }
}

