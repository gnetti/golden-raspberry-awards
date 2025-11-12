package golden.raspberry.awards.core.application.usecase.validation;

import golden.raspberry.awards.core.application.port.out.MovieQueryPort;
import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("MovieValidator Tests")
class MovieValidatorTest {

    private MovieQueryPort movieQueryPort;
    private MovieValidator validator;

    @BeforeEach
    void setUp() {
        movieQueryPort = mock(MovieQueryPort.class);
        validator = new MovieValidator(movieQueryPort);
    }

    @Test
    @DisplayName("Should validate when movie exists")
    void shouldValidateWhenMovieExists() {
        var movieId = 1L;
        var movie = new MovieWithId(movieId, 2020, "Title", "Studios", "Producers", true);

        when(movieQueryPort.findByIdWithId(movieId)).thenReturn(Optional.of(movie));

        assertDoesNotThrow(() -> validator.validateExists(movieId));
        verify(movieQueryPort).findByIdWithId(movieId);
    }

    @Test
    @DisplayName("Should throw exception when movie does not exist")
    void shouldThrowExceptionWhenMovieDoesNotExist() {
        var movieId = 1L;

        when(movieQueryPort.findByIdWithId(movieId)).thenReturn(Optional.empty());

        var exception = assertThrows(IllegalStateException.class, () ->
                validator.validateExists(movieId));

        assertTrue(exception.getMessage().contains("Movie with ID %d not found".formatted(movieId)));
        verify(movieQueryPort).findByIdWithId(movieId);
    }

    @Test
    @DisplayName("Should throw exception when ID is null")
    void shouldThrowExceptionWhenIdIsNull() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                validator.validateExists(null));

        assertTrue(exception.getMessage().contains("ID cannot be null"));
        verifyNoInteractions(movieQueryPort);
    }

    @Test
    @DisplayName("Should throw exception when ID is invalid")
    void shouldThrowExceptionWhenIdIsInvalid() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                validator.validateExists(0L));

        assertTrue(exception.getMessage().contains("ID must be positive"));
        verifyNoInteractions(movieQueryPort);
    }
}

