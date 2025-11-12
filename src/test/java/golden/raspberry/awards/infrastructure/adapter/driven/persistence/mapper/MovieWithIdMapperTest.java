package golden.raspberry.awards.infrastructure.adapter.driven.persistence.mapper;

import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;
import golden.raspberry.awards.infrastructure.adapter.driven.persistence.entity.MovieEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MovieWithIdMapper Tests")
class MovieWithIdMapperTest {

    @Test
    @DisplayName("Should convert MovieEntity to MovieWithId")
    void shouldConvertMovieEntityToMovieWithId() {
        var entity = new MovieEntity(1L, 2020, "Test Movie", "Test Studio", "Test Producer", true);

        var result = MovieWithIdMapper.toDomain(entity);

        assertTrue(result.isPresent());
        var movieWithId = result.get();
        assertEquals(1L, movieWithId.id());
        assertEquals(2020, movieWithId.year());
        assertEquals("Test Movie", movieWithId.title());
        assertEquals("Test Studio", movieWithId.studios());
        assertEquals("Test Producer", movieWithId.producers());
        assertTrue(movieWithId.winner());
    }

    @Test
    @DisplayName("Should return empty Optional when entity is null")
    void shouldReturnEmptyOptionalWhenEntityIsNull() {
        var result = MovieWithIdMapper.toDomain(null);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should convert list of MovieEntity to list of MovieWithId")
    void shouldConvertListOfMovieEntityToListOfMovieWithId() {
        var entities = List.of(
                new MovieEntity(1L, 2020, "Movie 1", "Studio 1", "Producer 1", true),
                new MovieEntity(2L, 2021, "Movie 2", "Studio 2", "Producer 2", false)
        );

        var result = MovieWithIdMapper.toDomainList(entities);

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals(2L, result.get(1).id());
    }

    @Test
    @DisplayName("Should return empty list when entities list is empty")
    void shouldReturnEmptyListWhenEntitiesListIsEmpty() {
        var result = MovieWithIdMapper.toDomainList(List.of());

        assertTrue(result.isEmpty());
    }
}

