package golden.raspberry.awards.core.domain.model.aggregate;

import org.springframework.lang.NonNull;

import java.util.Objects;

/**
 * Domain model representing a Movie.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record Movie(Integer year, String title, String studios, String producers, Boolean winner) {
    /**
     * Constructor for Movie record.
     *
     * @param year      Movie release year
     * @param title     Movie title
     * @param studios   Movie studios
     * @param producers Movie producers
     * @param winner    Whether the movie is a winner
     */
    public Movie(Integer year, String title, String studios, String producers, Boolean winner) {
        this.year = Objects.requireNonNull(year, "Year cannot be null");
        this.title = Objects.requireNonNull(title, "Title cannot be null");
        this.studios = Objects.requireNonNull(studios, "Studios cannot be null");
        this.producers = Objects.requireNonNull(producers, "Producers cannot be null");
        this.winner = Objects.requireNonNullElse(winner, false);
    }

    /**
     * Compares this movie with another object for equality.
     * Two movies are equal if they have the same year and title.
     *
     * @param o Object to compare
     * @return true if objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return Objects.equals(year, movie.year) &&
                Objects.equals(title, movie.title);
    }

    /**
     * Returns hash code based on year and title.
     *
     * @return Hash code value
     */
    @Override
    public int hashCode() {
        return Objects.hash(year, title);
    }

    /**
     * Returns string representation of this movie.
     *
     * @return Formatted string representation
     */
    @Override
    @NonNull
    public String toString() {
        return "Movie{year=%d, title='%s', winner=%s}".formatted(year, title, winner);
    }
}

