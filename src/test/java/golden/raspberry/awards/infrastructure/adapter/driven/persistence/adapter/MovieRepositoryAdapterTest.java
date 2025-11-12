package golden.raspberry.awards.infrastructure.adapter.driven.persistence.adapter;

import golden.raspberry.awards.core.domain.model.aggregate.Movie;
import golden.raspberry.awards.infrastructure.adapter.driven.persistence.entity.MovieEntity;
import golden.raspberry.awards.infrastructure.adapter.driven.persistence.repository.MovieJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("MovieRepositoryAdapter Tests")
class MovieRepositoryAdapterTest {

    private MovieJpaRepository jpaRepository;
    private MovieRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        jpaRepository = mock(MovieJpaRepository.class);
        adapter = new MovieRepositoryAdapter(jpaRepository);
    }

    @Test
    @DisplayName("Should throw exception when JpaRepository is null")
    void shouldThrowExceptionWhenJpaRepositoryIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new MovieRepositoryAdapter(null));

        assertEquals("JpaRepository cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should find all movies where winner is true")
    void shouldFindAllMoviesWhereWinnerIsTrue() {
        var entities = List.of(
                new MovieEntity(1L, 2020, "Movie 1", "Studio 1", "Producer 1", true),
                new MovieEntity(2L, 2021, "Movie 2", "Studio 2", "Producer 2", true)
        );
        when(jpaRepository.findByWinnerTrue()).thenReturn(entities);

        var result = adapter.findByWinnerTrue();

        assertEquals(2, result.size());
        verify(jpaRepository).findByWinnerTrue();
    }

    @Test
    @DisplayName("Should return empty list when no winners found")
    void shouldReturnEmptyListWhenNoWinnersFound() {
        when(jpaRepository.findByWinnerTrue()).thenReturn(List.of());

        var result = adapter.findByWinnerTrue();

        assertTrue(result.isEmpty());
        verify(jpaRepository).findByWinnerTrue();
    }

    @Test
    @DisplayName("Should save movie with ID")
    void shouldSaveMovieWithId() {
        var movie = new Movie(2020, "Movie", "Studio", "Producer", true);
        var id = 1L;
        var entity = new MovieEntity(id, 2020, "Movie", "Studio", "Producer", true);
        when(jpaRepository.save(any(MovieEntity.class))).thenReturn(entity);

        var result = adapter.saveWithId(movie, id);

        assertNotNull(result);
        assertEquals(id, result.id());
        verify(jpaRepository).save(any(MovieEntity.class));
    }

    @Test
    @DisplayName("Should throw exception when movie is null")
    void shouldThrowExceptionWhenMovieIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                adapter.saveWithId(null, 1L));

        assertEquals("Movie cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when ID is null")
    void shouldThrowExceptionWhenIdIsNull() {
        var movie = new Movie(2020, "Movie", "Studio", "Producer", true);
        var exception = assertThrows(NullPointerException.class, () ->
                adapter.saveWithId(movie, null));

        assertEquals("ID cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should find movie by ID")
    void shouldFindMovieById() {
        var id = 1L;
        var entity = new MovieEntity(id, 2020, "Movie", "Studio", "Producer", true);
        when(jpaRepository.findById(id)).thenReturn(Optional.of(entity));

        var result = adapter.findByIdWithId(id);

        assertTrue(result.isPresent());
        assertEquals(id, result.get().id());
        verify(jpaRepository).findById(id);
    }

    @Test
    @DisplayName("Should return empty when movie not found")
    void shouldReturnEmptyWhenMovieNotFound() {
        var id = 1L;
        when(jpaRepository.findById(id)).thenReturn(Optional.empty());

        var result = adapter.findByIdWithId(id);

        assertTrue(result.isEmpty());
        verify(jpaRepository).findById(id);
    }

    @Test
    @DisplayName("Should throw exception when findById ID is null")
    void shouldThrowExceptionWhenFindByIdIdIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                adapter.findByIdWithId(null));

        assertEquals("ID cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should delete movie by ID")
    void shouldDeleteMovieById() {
        var id = 1L;
        when(jpaRepository.existsById(id)).thenReturn(true);

        var result = adapter.deleteById(id);

        assertTrue(result);
        verify(jpaRepository).existsById(id);
        verify(jpaRepository).deleteById(id);
    }

    @Test
    @DisplayName("Should return false when movie does not exist for deletion")
    void shouldReturnFalseWhenMovieDoesNotExistForDeletion() {
        var id = 1L;
        when(jpaRepository.existsById(id)).thenReturn(false);

        var result = adapter.deleteById(id);

        assertFalse(result);
        verify(jpaRepository).existsById(id);
        verify(jpaRepository, never()).deleteById(id);
    }

    @Test
    @DisplayName("Should throw exception when deleteById ID is null")
    void shouldThrowExceptionWhenDeleteByIdIdIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                adapter.deleteById(null));

        assertEquals("ID cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should find all movies with pagination")
    void shouldFindAllMoviesWithPagination() {
        var pageable = PageRequest.of(0, 10);
        var entities = List.of(
                new MovieEntity(1L, 2020, "Movie 1", "Studio 1", "Producer 1", true)
        );
        var entityPage = new PageImpl<>(entities, pageable, 1);
        when(jpaRepository.findAll(pageable)).thenReturn(entityPage);

        var result = adapter.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        verify(jpaRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should throw exception when pageable is null")
    void shouldThrowExceptionWhenPageableIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                adapter.findAll(null));

        assertEquals("Pageable cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should find all movies with filter by title")
    void shouldFindAllMoviesWithFilterByTitle() {
        var pageable = PageRequest.of(0, 10);
        var entities = List.of(
                new MovieEntity(1L, 2020, "Movie 1", "Studio 1", "Producer 1", true)
        );
        var entityPage = new PageImpl<>(entities, pageable, 1);
        when(jpaRepository.findByTitleContainingIgnoreCase("Movie", pageable)).thenReturn(entityPage);

        var result = adapter.findAllWithFilter("title", "Movie", pageable);

        assertEquals(1, result.getTotalElements());
        verify(jpaRepository).findByTitleContainingIgnoreCase("Movie", pageable);
    }

    @Test
    @DisplayName("Should find all movies with filter by year")
    void shouldFindAllMoviesWithFilterByYear() {
        var pageable = PageRequest.of(0, 10);
        var entities = List.of(
                new MovieEntity(1L, 2020, "Movie 1", "Studio 1", "Producer 1", true)
        );
        var entityPage = new PageImpl<>(entities, pageable, 1);
        when(jpaRepository.findByYearContaining("2020", pageable)).thenReturn(entityPage);

        var result = adapter.findAllWithFilter("year", "2020", pageable);

        assertEquals(1, result.getTotalElements());
        verify(jpaRepository).findByYearContaining("2020", pageable);
    }

    @Test
    @DisplayName("Should find all movies with filter by studios")
    void shouldFindAllMoviesWithFilterByStudios() {
        var pageable = PageRequest.of(0, 10);
        var entities = List.of(
                new MovieEntity(1L, 2020, "Movie 1", "Studio 1", "Producer 1", true)
        );
        var entityPage = new PageImpl<>(entities, pageable, 1);
        when(jpaRepository.findByStudiosContainingIgnoreCase("Studio", pageable)).thenReturn(entityPage);

        var result = adapter.findAllWithFilter("studios", "Studio", pageable);

        assertEquals(1, result.getTotalElements());
        verify(jpaRepository).findByStudiosContainingIgnoreCase("Studio", pageable);
    }

    @Test
    @DisplayName("Should find all movies with filter by producers")
    void shouldFindAllMoviesWithFilterByProducers() {
        var pageable = PageRequest.of(0, 10);
        var entities = List.of(
                new MovieEntity(1L, 2020, "Movie 1", "Studio 1", "Producer 1", true)
        );
        var entityPage = new PageImpl<>(entities, pageable, 1);
        when(jpaRepository.findByProducersContainingIgnoreCase("Producer", pageable)).thenReturn(entityPage);

        var result = adapter.findAllWithFilter("producers", "Producer", pageable);

        assertEquals(1, result.getTotalElements());
        verify(jpaRepository).findByProducersContainingIgnoreCase("Producer", pageable);
    }

    @Test
    @DisplayName("Should find all movies with filter by ID")
    void shouldFindAllMoviesWithFilterById() {
        var pageable = PageRequest.of(0, 10);
        var entities = List.of(
                new MovieEntity(1L, 2020, "Movie 1", "Studio 1", "Producer 1", true)
        );
        var entityPage = new PageImpl<>(entities, pageable, 1);
        when(jpaRepository.findByIdContaining("1", pageable)).thenReturn(entityPage);

        var result = adapter.findAllWithFilter("id", "1", pageable);

        assertEquals(1, result.getTotalElements());
        verify(jpaRepository).findByIdContaining("1", pageable);
    }

    @Test
    @DisplayName("Should find all movies with filter by all fields")
    void shouldFindAllMoviesWithFilterByAllFields() {
        var pageable = PageRequest.of(0, 10);
        var entities = List.of(
                new MovieEntity(1L, 2020, "Movie 1", "Studio 1", "Producer 1", true)
        );
        var entityPage = new PageImpl<>(entities, pageable, 1);
        when(jpaRepository.findByAllFieldsContaining("Movie", pageable)).thenReturn(entityPage);

        var result = adapter.findAllWithFilter("all", "Movie", pageable);

        assertEquals(1, result.getTotalElements());
        verify(jpaRepository).findByAllFieldsContaining("Movie", pageable);
    }

    @Test
    @DisplayName("Should find all movies when filter value is blank")
    void shouldFindAllMoviesWhenFilterValueIsBlank() {
        var pageable = PageRequest.of(0, 10);
        var entities = List.of(
                new MovieEntity(1L, 2020, "Movie 1", "Studio 1", "Producer 1", true)
        );
        var entityPage = new PageImpl<>(entities, pageable, 1);
        when(jpaRepository.findAll(pageable)).thenReturn(entityPage);

        var result = adapter.findAllWithFilter("title", "   ", pageable);

        assertEquals(1, result.getTotalElements());
        verify(jpaRepository).findAll(pageable);
        verify(jpaRepository, never()).findByTitleContainingIgnoreCase(anyString(), any());
    }

    @Test
    @DisplayName("Should throw exception when filter type is null")
    void shouldThrowExceptionWhenFilterTypeIsNull() {
        var pageable = PageRequest.of(0, 10);
        var exception = assertThrows(NullPointerException.class, () ->
                adapter.findAllWithFilter(null, "value", pageable));

        assertEquals("Filter type cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when filter value is null")
    void shouldThrowExceptionWhenFilterValueIsNull() {
        var pageable = PageRequest.of(0, 10);
        var exception = assertThrows(NullPointerException.class, () ->
                adapter.findAllWithFilter("title", null, pageable));

        assertEquals("Filter value cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when pageable is null in findAllWithFilter")
    void shouldThrowExceptionWhenPageableIsNullInFindAllWithFilter() {
        var exception = assertThrows(NullPointerException.class, () ->
                adapter.findAllWithFilter("title", "value", null));

        assertEquals("Pageable cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should handle case insensitive filter type")
    void shouldHandleCaseInsensitiveFilterType() {
        var pageable = PageRequest.of(0, 10);
        var entities = List.of(
                new MovieEntity(1L, 2020, "Movie 1", "Studio 1", "Producer 1", true)
        );
        var entityPage = new PageImpl<>(entities, pageable, 1);
        when(jpaRepository.findByTitleContainingIgnoreCase("Movie", pageable)).thenReturn(entityPage);

        var result = adapter.findAllWithFilter("TITLE", "Movie", pageable);

        assertEquals(1, result.getTotalElements());
        verify(jpaRepository).findByTitleContainingIgnoreCase("Movie", pageable);
    }
}

