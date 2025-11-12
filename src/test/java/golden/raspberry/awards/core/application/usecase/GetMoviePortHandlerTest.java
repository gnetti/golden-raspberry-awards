package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.out.MovieQueryPort;
import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("GetMoviePortHandler Tests")
class GetMoviePortHandlerTest {

    private MovieQueryPort movieQueryPort;
    private GetMoviePortHandler handler;

    @BeforeEach
    void setUp() {
        movieQueryPort = mock(MovieQueryPort.class);
        handler = new GetMoviePortHandler(movieQueryPort);
    }

    @Test
    @DisplayName("Should return movie when found")
    void shouldReturnMovieWhenFound() {
        var movieId = 1L;
        var movie = new MovieWithId(movieId, 2020, "Title", "Studios", "Producers", true);

        when(movieQueryPort.findByIdWithId(movieId)).thenReturn(Optional.of(movie));

        var result = handler.execute(movieId);

        assertEquals(movie, result);
        verify(movieQueryPort).findByIdWithId(movieId);
    }

    @Test
    @DisplayName("Should throw exception when movie not found")
    void shouldThrowExceptionWhenMovieNotFound() {
        var movieId = 1L;

        when(movieQueryPort.findByIdWithId(movieId)).thenReturn(Optional.empty());

        var exception = assertThrows(IllegalStateException.class, () ->
                handler.execute(movieId));

        assertTrue(exception.getMessage().contains("Movie with ID %d not found".formatted(movieId)));
        verify(movieQueryPort).findByIdWithId(movieId);
    }

    @Test
    @DisplayName("Should throw exception when ID is null")
    void shouldThrowExceptionWhenIdIsNull() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                handler.execute(null));

        assertTrue(exception.getMessage().contains("ID cannot be null"));
        verifyNoInteractions(movieQueryPort);
    }

    @Test
    @DisplayName("Should return page of movies")
    void shouldReturnPageOfMovies() {
        var pageable = PageRequest.of(0, 10);
        var movies = List.of(
                new MovieWithId(1L, 2020, "Title 1", "Studios", "Producers", true),
                new MovieWithId(2L, 2021, "Title 2", "Studios", "Producers", false)
        );
        var page = new PageImpl<>(movies, pageable, 2);

        when(movieQueryPort.findAll(pageable)).thenReturn(page);

        var result = handler.executeAll(pageable);

        assertEquals(page, result);
        verify(movieQueryPort).findAll(pageable);
    }

    @Test
    @DisplayName("Should throw exception when pageable is null")
    void shouldThrowExceptionWhenPageableIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                handler.executeAll(null));

        assertEquals("Pageable cannot be null", exception.getMessage());
        verifyNoInteractions(movieQueryPort);
    }

    @Test
    @DisplayName("Should return filtered page of movies")
    void shouldReturnFilteredPageOfMovies() {
        var pageable = PageRequest.of(0, 10);
        var movies = List.of(
                new MovieWithId(1L, 2020, "Title", "Studios", "Producers", true)
        );
        var page = new PageImpl<>(movies, pageable, 1);

        when(movieQueryPort.findAllWithFilter("title", "Title", pageable)).thenReturn(page);

        var result = handler.executeAllWithFilter("title", "Title", pageable);

        assertEquals(page, result);
        verify(movieQueryPort).findAllWithFilter("title", "Title", pageable);
    }

    @Test
    @DisplayName("Should throw exception when filter type is null")
    void shouldThrowExceptionWhenFilterTypeIsNull() {
        var pageable = PageRequest.of(0, 10);

        var exception = assertThrows(NullPointerException.class, () ->
                handler.executeAllWithFilter(null, "value", pageable));

        assertEquals("Filter type cannot be null", exception.getMessage());
        verifyNoInteractions(movieQueryPort);
    }

    @Test
    @DisplayName("Should throw exception when filter value is null")
    void shouldThrowExceptionWhenFilterValueIsNull() {
        var pageable = PageRequest.of(0, 10);

        var exception = assertThrows(NullPointerException.class, () ->
                handler.executeAllWithFilter("title", null, pageable));

        assertEquals("Filter value cannot be null", exception.getMessage());
        verifyNoInteractions(movieQueryPort);
    }

    @Test
    @DisplayName("Should throw exception when pageable is null in filtered query")
    void shouldThrowExceptionWhenPageableIsNullInFilteredQuery() {
        var exception = assertThrows(NullPointerException.class, () ->
                handler.executeAllWithFilter("title", "value", null));

        assertEquals("Pageable cannot be null", exception.getMessage());
        verifyNoInteractions(movieQueryPort);
    }
}

