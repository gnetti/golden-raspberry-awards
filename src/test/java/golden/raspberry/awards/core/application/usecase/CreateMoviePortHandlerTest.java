package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.out.CsvFileWriterPort;
import golden.raspberry.awards.core.application.port.out.IdKeyManagerPort;
import golden.raspberry.awards.core.application.port.out.SaveMovieWithIdPort;
import golden.raspberry.awards.core.domain.model.aggregate.Movie;
import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CreateMoviePortHandler Tests")
class CreateMoviePortHandlerTest {

    private SaveMovieWithIdPort saveMovieWithIdPort;
    private IdKeyManagerPort idKeyManagerPort;
    private CsvFileWriterPort csvFileWriterPort;
    private CreateMoviePortHandler handler;

    @BeforeEach
    void setUp() {
        saveMovieWithIdPort = mock(SaveMovieWithIdPort.class);
        idKeyManagerPort = mock(IdKeyManagerPort.class);
        csvFileWriterPort = mock(CsvFileWriterPort.class);
        handler = new CreateMoviePortHandler(saveMovieWithIdPort, idKeyManagerPort, csvFileWriterPort);
    }

    @Test
    @DisplayName("Should create movie successfully")
    void shouldCreateMovieSuccessfully() {
        var nextId = 1L;
        var movie = new Movie(2020, "Test Movie", "Studio", "Producer", true);
        var savedMovie = new MovieWithId(nextId, 2020, "Test Movie", "Studio", "Producer", true);

        when(idKeyManagerPort.getNextId()).thenReturn(nextId);
        when(saveMovieWithIdPort.saveWithId(any(Movie.class), eq(nextId))).thenReturn(savedMovie);

        var result = handler.execute(2020, "Test Movie", "Studio", "Producer", true);

        assertNotNull(result);
        assertEquals(nextId, result.id());
        verify(idKeyManagerPort).getNextId();
        verify(saveMovieWithIdPort).saveWithId(any(Movie.class), eq(nextId));
        verify(csvFileWriterPort).appendMovie(savedMovie);
    }

    @Test
    @DisplayName("Should trim title, studios and producers")
    void shouldTrimTitleStudiosAndProducers() {
        var nextId = 1L;
        var savedMovie = new MovieWithId(nextId, 2020, "Test Movie", "Studio", "Producer", true);

        when(idKeyManagerPort.getNextId()).thenReturn(nextId);
        when(saveMovieWithIdPort.saveWithId(any(Movie.class), eq(nextId))).thenReturn(savedMovie);

        handler.execute(2020, "  Test Movie  ", "  Studio  ", "  Producer  ", true);

        verify(saveMovieWithIdPort).saveWithId(argThat(movie ->
                movie.title().equals("Test Movie") &&
                movie.studios().equals("Studio") &&
                movie.producers().equals("Producer")
        ), eq(nextId));
    }

    @Test
    @DisplayName("Should throw exception when validation fails")
    void shouldThrowExceptionWhenValidationFails() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                handler.execute(null, "Title", "Studios", "Producers", true));

        assertTrue(exception.getMessage().contains("Year cannot be null"));
        verifyNoInteractions(idKeyManagerPort);
        verifyNoInteractions(saveMovieWithIdPort);
        verifyNoInteractions(csvFileWriterPort);
    }
}

