package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.out.CsvFileWriterPort;
import golden.raspberry.awards.core.application.port.out.MovieQueryPort;
import golden.raspberry.awards.core.application.port.out.SaveMovieWithIdPort;
import golden.raspberry.awards.core.application.usecase.validation.MovieValidator;
import golden.raspberry.awards.core.domain.model.aggregate.Movie;
import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("UpdateMoviePortHandler Tests")
class UpdateMoviePortHandlerTest {

    private MovieValidator validator;
    private SaveMovieWithIdPort saveMovieWithIdPort;
    private CsvFileWriterPort csvFileWriterPort;
    private UpdateMoviePortHandler handler;

    private MovieQueryPort movieQueryPort;

    @BeforeEach
    void setUp() {
        movieQueryPort = mock(MovieQueryPort.class);
        validator = new MovieValidator(movieQueryPort);
        saveMovieWithIdPort = mock(SaveMovieWithIdPort.class);
        csvFileWriterPort = mock(CsvFileWriterPort.class);
        handler = new UpdateMoviePortHandler(validator, saveMovieWithIdPort, csvFileWriterPort);
    }

    @Test
    @DisplayName("Should update movie successfully")
    void shouldUpdateMovieSuccessfully() {
        var movieId = 1L;
        var existingMovie = new MovieWithId(movieId, 2020, "Old Title", "Studio", "Producer", true);
        var updatedMovie = new MovieWithId(movieId, 2021, "New Title", "Studio", "Producer", false);

        when(movieQueryPort.findByIdWithId(movieId)).thenReturn(Optional.of(existingMovie));
        when(saveMovieWithIdPort.saveWithId(any(Movie.class), eq(movieId))).thenReturn(updatedMovie);

        var result = handler.execute(movieId, 2021, "New Title", "Studio", "Producer", false);

        assertNotNull(result);
        assertEquals(movieId, result.id());
        assertEquals("New Title", result.title());
        verify(saveMovieWithIdPort).saveWithId(any(Movie.class), eq(movieId));
        verify(csvFileWriterPort).updateMovie(updatedMovie);
    }

    @Test
    @DisplayName("Should trim title, studios and producers")
    void shouldTrimTitleStudiosAndProducers() {
        var movieId = 1L;
        var existingMovie = new MovieWithId(movieId, 2020, "Title", "Studio", "Producer", true);
        var updatedMovie = new MovieWithId(movieId, 2020, "Title", "Studio", "Producer", true);

        when(movieQueryPort.findByIdWithId(movieId)).thenReturn(Optional.of(existingMovie));
        when(saveMovieWithIdPort.saveWithId(any(Movie.class), eq(movieId))).thenReturn(updatedMovie);

        handler.execute(movieId, 2020, "  Title  ", "  Studio  ", "  Producer  ", true);

        verify(saveMovieWithIdPort).saveWithId(argThat(movie ->
                movie.title().equals("Title") &&
                movie.studios().equals("Studio") &&
                movie.producers().equals("Producer")
        ), eq(movieId));
    }

    @Test
    @DisplayName("Should throw exception when validation fails")
    void shouldThrowExceptionWhenValidationFails() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                handler.execute(null, 2020, "Title", "Studios", "Producers", true));

        assertTrue(exception.getMessage().contains("ID cannot be null"));
        verifyNoInteractions(saveMovieWithIdPort);
        verifyNoInteractions(csvFileWriterPort);
    }
}

