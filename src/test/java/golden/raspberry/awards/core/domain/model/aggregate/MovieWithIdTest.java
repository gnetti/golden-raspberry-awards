package golden.raspberry.awards.core.domain.model.aggregate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MovieWithId Domain Model Tests")
class MovieWithIdTest {

    @Test
    @DisplayName("Should create movie with ID and all valid fields")
    void shouldCreateMovieWithIdAndValidFields() {
        var movie = new MovieWithId(1L, 2020, "Test Movie", "Studio", "Producer", true);

        assertEquals(1L, movie.id());
        assertEquals(2020, movie.year());
        assertEquals("Test Movie", movie.title());
        assertEquals("Studio", movie.studios());
        assertEquals("Producer", movie.producers());
        assertTrue(movie.winner());
    }

    @Test
    @DisplayName("Should trim title, studios and producers")
    void shouldTrimTitleStudiosAndProducers() {
        var movie = new MovieWithId(1L, 2020, "  Test Movie  ", "  Studio  ", "  Producer  ", true);

        assertEquals("Test Movie", movie.title());
        assertEquals("Studio", movie.studios());
        assertEquals("Producer", movie.producers());
    }

    @Test
    @DisplayName("Should throw exception when id is null")
    void shouldThrowExceptionWhenIdIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new MovieWithId(null, 2020, "Test Movie", "Studio", "Producer", true));

        assertEquals("ID cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when year is null")
    void shouldThrowExceptionWhenYearIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new MovieWithId(1L, null, "Test Movie", "Studio", "Producer", true));

        assertEquals("Year cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when title is null")
    void shouldThrowExceptionWhenTitleIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new MovieWithId(1L, 2020, null, "Studio", "Producer", true));

        assertEquals("Title cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when title is blank")
    void shouldThrowExceptionWhenTitleIsBlank() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new MovieWithId(1L, 2020, "   ", "Studio", "Producer", true));

        assertEquals("Title cannot be blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when studios is blank")
    void shouldThrowExceptionWhenStudiosIsBlank() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new MovieWithId(1L, 2020, "Test Movie", "   ", "Producer", true));

        assertEquals("Studios cannot be blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when producers is blank")
    void shouldThrowExceptionWhenProducersIsBlank() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new MovieWithId(1L, 2020, "Test Movie", "Studio", "   ", true));

        assertEquals("Producers cannot be blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when year is below minimum")
    void shouldThrowExceptionWhenYearIsBelowMinimum() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new MovieWithId(1L, 1899, "Test Movie", "Studio", "Producer", true));

        assertTrue(exception.getMessage().contains("Year must be between 1900 and 2100"));
    }

    @Test
    @DisplayName("Should throw exception when year is above maximum")
    void shouldThrowExceptionWhenYearIsAboveMaximum() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new MovieWithId(1L, 2101, "Test Movie", "Studio", "Producer", true));

        assertTrue(exception.getMessage().contains("Year must be between 1900 and 2100"));
    }

    @Test
    @DisplayName("Should accept year at minimum boundary")
    void shouldAcceptYearAtMinimumBoundary() {
        var movie = new MovieWithId(1L, 1900, "Test Movie", "Studio", "Producer", true);

        assertEquals(1900, movie.year());
    }

    @Test
    @DisplayName("Should accept year at maximum boundary")
    void shouldAcceptYearAtMaximumBoundary() {
        var movie = new MovieWithId(1L, 2100, "Test Movie", "Studio", "Producer", true);

        assertEquals(2100, movie.year());
    }

    @Test
    @DisplayName("Should throw exception when winner is null")
    void shouldThrowExceptionWhenWinnerIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new MovieWithId(1L, 2020, "Test Movie", "Studio", "Producer", null));

        assertEquals("Winner cannot be null", exception.getMessage());
    }
}

