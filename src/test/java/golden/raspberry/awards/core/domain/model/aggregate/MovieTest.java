package golden.raspberry.awards.core.domain.model.aggregate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Movie Domain Model Tests")
class MovieTest {

    @Test
    @DisplayName("Should create movie with all valid fields")
    void shouldCreateMovieWithValidFields() {
        var movie = new Movie(2020, "Test Movie", "Studio", "Producer", true);

        assertEquals(2020, movie.year());
        assertEquals("Test Movie", movie.title());
        assertEquals("Studio", movie.studios());
        assertEquals("Producer", movie.producers());
        assertTrue(movie.winner());
    }

    @Test
    @DisplayName("Should default winner to false when null")
    void shouldDefaultWinnerToFalseWhenNull() {
        var movie = new Movie(2020, "Test Movie", "Studio", "Producer", null);

        assertFalse(movie.winner());
    }

    @Test
    @DisplayName("Should throw exception when year is null")
    void shouldThrowExceptionWhenYearIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new Movie(null, "Test Movie", "Studio", "Producer", true));

        assertEquals("Year cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when title is null")
    void shouldThrowExceptionWhenTitleIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new Movie(2020, null, "Studio", "Producer", true));

        assertEquals("Title cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when studios is null")
    void shouldThrowExceptionWhenStudiosIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new Movie(2020, "Test Movie", null, "Producer", true));

        assertEquals("Studios cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when producers is null")
    void shouldThrowExceptionWhenProducersIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new Movie(2020, "Test Movie", "Studio", null, true));

        assertEquals("Producers cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should be equal when year and title are same")
    void shouldBeEqualWhenYearAndTitleAreSame() {
        var movie1 = new Movie(2020, "Test Movie", "Studio1", "Producer1", true);
        var movie2 = new Movie(2020, "Test Movie", "Studio2", "Producer2", false);

        assertEquals(movie1, movie2);
        assertEquals(movie1.hashCode(), movie2.hashCode());
    }

    @Test
    @DisplayName("Should not be equal when year is different")
    void shouldNotBeEqualWhenYearIsDifferent() {
        var movie1 = new Movie(2020, "Test Movie", "Studio", "Producer", true);
        var movie2 = new Movie(2021, "Test Movie", "Studio", "Producer", true);

        assertNotEquals(movie1, movie2);
    }

    @Test
    @DisplayName("Should not be equal when title is different")
    void shouldNotBeEqualWhenTitleIsDifferent() {
        var movie1 = new Movie(2020, "Test Movie 1", "Studio", "Producer", true);
        var movie2 = new Movie(2020, "Test Movie 2", "Studio", "Producer", true);

        assertNotEquals(movie1, movie2);
    }

    @Test
    @DisplayName("Should return formatted string representation")
    void shouldReturnFormattedStringRepresentation() {
        var movie = new Movie(2020, "Test Movie", "Studio", "Producer", true);
        var result = movie.toString();

        assertTrue(result.contains("2020"));
        assertTrue(result.contains("Test Movie"));
        assertTrue(result.contains("true"));
    }

    @Test
    @DisplayName("Should not be equal to null")
    void shouldNotBeEqualToString() {
        var movie = new Movie(2020, "Test Movie", "Studio", "Producer", true);

        assertNotEquals(movie, null);
    }

    @Test
    @DisplayName("Should be equal to itself")
    void shouldBeEqualToItself() {
        var movie = new Movie(2020, "Test Movie", "Studio", "Producer", true);

        assertEquals(movie, movie);
    }
}

