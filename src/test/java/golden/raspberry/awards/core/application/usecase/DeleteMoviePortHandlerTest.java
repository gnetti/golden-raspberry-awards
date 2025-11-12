package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.out.CsvFileWriterPort;
import golden.raspberry.awards.core.application.port.out.MovieQueryPort;
import golden.raspberry.awards.core.application.port.out.MovieRepositoryPort;
import golden.raspberry.awards.core.application.usecase.validation.MovieValidator;
import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("DeleteMoviePortHandler Tests")
class DeleteMoviePortHandlerTest {

    private MovieValidator validator;
    private MovieRepositoryPort repository;
    private CsvFileWriterPort csvFileWriterPort;
    private DeleteMoviePortHandler handler;

    private MovieQueryPort movieQueryPort;

    @BeforeEach
    void setUp() {
        movieQueryPort = mock(MovieQueryPort.class);
        validator = new MovieValidator(movieQueryPort);
        repository = mock(MovieRepositoryPort.class);
        csvFileWriterPort = mock(CsvFileWriterPort.class);
        handler = new DeleteMoviePortHandler(validator, repository, csvFileWriterPort);
    }

    @Test
    @DisplayName("Should delete movie successfully")
    void shouldDeleteMovieSuccessfully() {
        var movieId = 1L;
        var existingMovie = new MovieWithId(movieId, 2020, "Title", "Studio", "Producer", true);

        when(movieQueryPort.findByIdWithId(movieId)).thenReturn(Optional.of(existingMovie));
        when(repository.deleteById(movieId)).thenReturn(true);

        assertDoesNotThrow(() -> handler.execute(movieId));

        verify(repository).deleteById(movieId);
        verify(csvFileWriterPort).removeMovie(movieId);
    }

    @Test
    @DisplayName("Should throw exception when deletion fails")
    void shouldThrowExceptionWhenDeletionFails() {
        var movieId = 1L;
        var existingMovie = new MovieWithId(movieId, 2020, "Title", "Studio", "Producer", true);

        when(movieQueryPort.findByIdWithId(movieId)).thenReturn(Optional.of(existingMovie));
        when(repository.deleteById(movieId)).thenReturn(false);

        var exception = assertThrows(IllegalStateException.class, () ->
                handler.execute(movieId));

        assertTrue(exception.getMessage().contains("Failed to delete movie"));
        verify(repository).deleteById(movieId);
        verifyNoInteractions(csvFileWriterPort);
    }

    @Test
    @DisplayName("Should throw exception when validation fails")
    void shouldThrowExceptionWhenValidationFails() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                handler.execute(null));

        assertTrue(exception.getMessage().contains("ID cannot be null"));
        verifyNoInteractions(repository);
        verifyNoInteractions(csvFileWriterPort);
    }
}

