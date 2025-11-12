package golden.raspberry.awards.adapter.driving.rest.controller;

import golden.raspberry.awards.adapter.driving.rest.dto.CreateMovieDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.MovieDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.UpdateMovieDTO;
import golden.raspberry.awards.core.application.port.in.CreateMoviePort;
import golden.raspberry.awards.core.application.port.in.DeleteMoviePort;
import golden.raspberry.awards.core.application.port.in.GetMoviePort;
import golden.raspberry.awards.core.application.port.in.UpdateMoviePort;
import golden.raspberry.awards.core.application.port.out.ConverterDtoPort;
import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("MovieController Tests")
class MovieControllerTest {

    private MovieController controller;
    private CreateMoviePort createMoviePort;
    private GetMoviePort getMoviePort;
    private UpdateMoviePort updateMoviePort;
    private DeleteMoviePort deleteMoviePort;
    private ConverterDtoPort converterDtoPort;

    @BeforeEach
    void setUp() {
        createMoviePort = mock(CreateMoviePort.class);
        getMoviePort = mock(GetMoviePort.class);
        updateMoviePort = mock(UpdateMoviePort.class);
        deleteMoviePort = mock(DeleteMoviePort.class);
        converterDtoPort = mock(ConverterDtoPort.class);
        controller = new MovieController(
                createMoviePort,
                getMoviePort,
                updateMoviePort,
                deleteMoviePort,
                converterDtoPort
        );
    }

    @Test
    @DisplayName("Should create movie successfully")
    void shouldCreateMovieSuccessfully() {
        var createDTO = new CreateMovieDTO(2020, "Test Movie", "Test Studio", "Test Producer", true);
        var movieWithId = new MovieWithId(1L, 2020, "Test Movie", "Test Studio", "Test Producer", true);
        var movieDTO = new MovieDTO(1L, 2020, "Test Movie", "Test Studio", "Test Producer", true);

        when(createMoviePort.execute(2020, "Test Movie", "Test Studio", "Test Producer", true))
                .thenReturn(movieWithId);
        when(converterDtoPort.toDTO(movieWithId)).thenReturn(movieDTO);

        var result = controller.create(createDTO);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(movieDTO, result.getBody());
        verify(createMoviePort).execute(2020, "Test Movie", "Test Studio", "Test Producer", true);
    }

    @Test
    @DisplayName("Should get all movies with pagination")
    void shouldGetAllMoviesWithPagination() {
        var movieWithId = new MovieWithId(1L, 2020, "Test Movie", "Test Studio", "Test Producer", true);
        var movieDTO = new MovieDTO(1L, 2020, "Test Movie", "Test Studio", "Test Producer", true);
        var pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));
        var moviePage = new PageImpl<>(List.of(movieWithId), pageable, 1);

        when(getMoviePort.executeAll(pageable)).thenReturn(moviePage);
        when(converterDtoPort.toDTO(movieWithId)).thenReturn(movieDTO);

        var result = controller.getAll(0, 10, "id", "asc");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(1, result.getBody().getContent().size());
        verify(getMoviePort).executeAll(pageable);
    }

    @Test
    @DisplayName("Should get movie by ID")
    void shouldGetMovieById() {
        var movieWithId = new MovieWithId(1L, 2020, "Test Movie", "Test Studio", "Test Producer", true);
        var movieDTO = new MovieDTO(1L, 2020, "Test Movie", "Test Studio", "Test Producer", true);

        when(getMoviePort.execute(1L)).thenReturn(movieWithId);
        when(converterDtoPort.toDTO(movieWithId)).thenReturn(movieDTO);

        var result = controller.getById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(movieDTO, result.getBody());
        verify(getMoviePort).execute(1L);
    }

    @Test
    @DisplayName("Should update movie successfully")
    void shouldUpdateMovieSuccessfully() {
        var updateDTO = new UpdateMovieDTO(2021, "Updated Movie", "Updated Studio", "Updated Producer", false);
        var movieWithId = new MovieWithId(1L, 2021, "Updated Movie", "Updated Studio", "Updated Producer", false);
        var movieDTO = new MovieDTO(1L, 2021, "Updated Movie", "Updated Studio", "Updated Producer", false);

        when(updateMoviePort.execute(1L, 2021, "Updated Movie", "Updated Studio", "Updated Producer", false))
                .thenReturn(movieWithId);
        when(converterDtoPort.toDTO(movieWithId)).thenReturn(movieDTO);

        var result = controller.update(1L, updateDTO);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertNotNull(result.getBody());
        assertEquals(movieDTO, result.getBody());
        verify(updateMoviePort).execute(1L, 2021, "Updated Movie", "Updated Studio", "Updated Producer", false);
    }

    @Test
    @DisplayName("Should delete movie successfully")
    void shouldDeleteMovieSuccessfully() {
        doNothing().when(deleteMoviePort).execute(1L);

        var result = controller.delete(1L);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        assertNull(result.getBody());
        verify(deleteMoviePort).execute(1L);
    }

    @Test
    @DisplayName("Should handle desc sort direction")
    void shouldHandleDescSortDirection() {
        var movieWithId = new MovieWithId(1L, 2020, "Test Movie", "Test Studio", "Test Producer", true);
        var movieDTO = new MovieDTO(1L, 2020, "Test Movie", "Test Studio", "Test Producer", true);
        var pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "id"));
        var moviePage = new PageImpl<>(List.of(movieWithId), pageable, 1);

        when(getMoviePort.executeAll(pageable)).thenReturn(moviePage);
        when(converterDtoPort.toDTO(movieWithId)).thenReturn(movieDTO);

        var result = controller.getAll(0, 10, "id", "desc");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(getMoviePort).executeAll(pageable);
    }

    @Test
    @DisplayName("Should call toMovieDTO method through create")
    void shouldCallToMovieDTOMethodThroughCreate() {
        var createDTO = new CreateMovieDTO(2020, "Test Movie", "Test Studio", "Test Producer", true);
        var movieWithId = new MovieWithId(1L, 2020, "Test Movie", "Test Studio", "Test Producer", true);
        var movieDTO = new MovieDTO(1L, 2020, "Test Movie", "Test Studio", "Test Producer", true);

        when(createMoviePort.execute(2020, "Test Movie", "Test Studio", "Test Producer", true))
                .thenReturn(movieWithId);
        when(converterDtoPort.toDTO(movieWithId)).thenReturn(movieDTO);

        var result = controller.create(createDTO);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(movieDTO, result.getBody());
        verify(converterDtoPort).toDTO(movieWithId);
    }

    @Test
    @DisplayName("Should call toMovieDTO method through getById")
    void shouldCallToMovieDTOMethodThroughGetById() {
        var movieWithId = new MovieWithId(1L, 2020, "Test Movie", "Test Studio", "Test Producer", true);
        var movieDTO = new MovieDTO(1L, 2020, "Test Movie", "Test Studio", "Test Producer", true);

        when(getMoviePort.execute(1L)).thenReturn(movieWithId);
        when(converterDtoPort.toDTO(movieWithId)).thenReturn(movieDTO);

        var result = controller.getById(1L);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(movieDTO, result.getBody());
        verify(converterDtoPort).toDTO(movieWithId);
    }

    @Test
    @DisplayName("Should call toMovieDTO method through update")
    void shouldCallToMovieDTOMethodThroughUpdate() {
        var updateDTO = new UpdateMovieDTO(2021, "Updated Movie", "Updated Studio", "Updated Producer", false);
        var movieWithId = new MovieWithId(1L, 2021, "Updated Movie", "Updated Studio", "Updated Producer", false);
        var movieDTO = new MovieDTO(1L, 2021, "Updated Movie", "Updated Studio", "Updated Producer", false);

        when(updateMoviePort.execute(1L, 2021, "Updated Movie", "Updated Studio", "Updated Producer", false))
                .thenReturn(movieWithId);
        when(converterDtoPort.toDTO(movieWithId)).thenReturn(movieDTO);

        var result = controller.update(1L, updateDTO);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(movieDTO, result.getBody());
        verify(converterDtoPort).toDTO(movieWithId);
    }
}

