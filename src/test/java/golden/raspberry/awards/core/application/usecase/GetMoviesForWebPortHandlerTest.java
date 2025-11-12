package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.in.GetMoviePort;
import golden.raspberry.awards.core.application.port.in.GetMoviesForWebPort;
import golden.raspberry.awards.core.application.port.out.ConverterDtoPort;
import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("GetMoviesForWebPortHandler Tests")
class GetMoviesForWebPortHandlerTest {

    private GetMoviePort getMoviePort;
    private ConverterDtoPort converterDtoPort;
    private GetMoviesForWebPortHandler handler;

    @BeforeEach
    void setUp() {
        getMoviePort = mock(GetMoviePort.class);
        converterDtoPort = mock(ConverterDtoPort.class);
        handler = new GetMoviesForWebPortHandler(getMoviePort, converterDtoPort);
    }

    @Test
    @DisplayName("Should execute query without filter")
    void shouldExecuteQueryWithoutFilter() {
        var request = new GetMoviesForWebPort.MoviesWebRequest(
                0, 10, "id", "asc", null, null
        );
        var movies = List.of(
                new MovieWithId(1L, 2020, "Title", "Studio", "Producer", true)
        );
        var page = new PageImpl<>(movies, PageRequest.of(0, 10), 1);

        when(getMoviePort.executeAll(any())).thenReturn(page);
        when(converterDtoPort.toDTO(any())).thenReturn(new Object());

        var result = handler.execute(request);

        assertNotNull(result);
        assertEquals(0, result.currentPage());
        assertEquals(1, result.totalPages());
        verify(getMoviePort).executeAll(any());
        verify(converterDtoPort, times(movies.size())).toDTO(any());
    }

    @Test
    @DisplayName("Should execute query with filter")
    void shouldExecuteQueryWithFilter() {
        var request = new GetMoviesForWebPort.MoviesWebRequest(
                0, 10, "id", "asc", "title", "Test"
        );
        var movies = List.of(
                new MovieWithId(1L, 2020, "Test Title", "Studio", "Producer", true)
        );
        var page = new PageImpl<>(movies, PageRequest.of(0, 10), 1);

        when(getMoviePort.executeAllWithFilter(eq("title"), eq("Test"), any())).thenReturn(page);
        when(converterDtoPort.toDTO(any())).thenReturn(new Object());

        var result = handler.execute(request);

        assertNotNull(result);
        assertEquals("title", result.filterType());
        assertEquals("Test", result.filterValue());
        verify(getMoviePort).executeAllWithFilter(eq("title"), eq("Test"), any());
    }

    @Test
    @DisplayName("Should map sort direction to desc")
    void shouldMapSortDirectionToDesc() {
        var request = new GetMoviesForWebPort.MoviesWebRequest(
                0, 10, "id", "desc", null, null
        );
        var page = new PageImpl<MovieWithId>(List.of(), PageRequest.of(0, 10, Sort.Direction.DESC, "id"), 0);

        when(getMoviePort.executeAll(any())).thenReturn(page);
        when(converterDtoPort.toDTO(any())).thenReturn(new Object());

        handler.execute(request);

        verify(getMoviePort).executeAll(argThat(pageable ->
                pageable.getSort().getOrderFor("id").getDirection() == Sort.Direction.DESC
        ));
    }

    @Test
    @DisplayName("Should map sort direction to asc by default")
    void shouldMapSortDirectionToAscByDefault() {
        var request = new GetMoviesForWebPort.MoviesWebRequest(
                0, 10, "id", null, null, null
        );
        var page = new PageImpl<MovieWithId>(List.of(), PageRequest.of(0, 10, Sort.Direction.ASC, "id"), 0);

        when(getMoviePort.executeAll(any())).thenReturn(page);
        when(converterDtoPort.toDTO(any())).thenReturn(new Object());

        handler.execute(request);

        verify(getMoviePort).executeAll(argThat(pageable ->
                pageable.getSort().getOrderFor("id").getDirection() == Sort.Direction.ASC
        ));
    }

    @Test
    @DisplayName("Should throw exception when request is null")
    void shouldThrowExceptionWhenRequestIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                handler.execute(null));

        assertEquals("Request cannot be null", exception.getMessage());
        verifyNoInteractions(getMoviePort);
        verifyNoInteractions(converterDtoPort);
    }

    @Test
    @DisplayName("Should calculate page numbers")
    void shouldCalculatePageNumbers() {
        var request = new GetMoviesForWebPort.MoviesWebRequest(
                0, 10, "id", "asc", null, null
        );
        var movies = List.of(
                new MovieWithId(1L, 2020, "Title", "Studio", "Producer", true),
                new MovieWithId(2L, 2021, "Title 2", "Studio", "Producer", false)
        );
        var page = new PageImpl<>(movies, PageRequest.of(0, 10), 2);

        when(getMoviePort.executeAll(any())).thenReturn(page);
        when(converterDtoPort.toDTO(any())).thenReturn(new Object());

        var result = handler.execute(request);

        assertNotNull(result.pageNumbers());
        verify(getMoviePort).executeAll(any());
    }
}

