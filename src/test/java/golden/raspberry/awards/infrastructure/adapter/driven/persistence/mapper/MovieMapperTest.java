package golden.raspberry.awards.infrastructure.adapter.driven.persistence.mapper;

import golden.raspberry.awards.core.domain.model.aggregate.Movie;
import golden.raspberry.awards.infrastructure.adapter.driven.persistence.entity.MovieEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MovieMapper Tests")
class MovieMapperTest {

    @Test
    @DisplayName("Should convert MovieEntity to Domain Movie")
    void shouldConvertMovieEntityToDomainMovie() {
        var entity = new MovieEntity(2020, "Test Movie", "Test Studio", "Test Producer", true);

        var result = MovieMapper.toDomain(entity);

        assertNotNull(result);
        assertEquals(2020, result.year());
        assertEquals("Test Movie", result.title());
        assertEquals("Test Studio", result.studios());
        assertEquals("Test Producer", result.producers());
        assertTrue(result.winner());
    }

    @Test
    @DisplayName("Should return null when entity is null")
    void shouldReturnNullWhenEntityIsNull() {
        var result = MovieMapper.toDomain(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when entity has null year")
    void shouldReturnNullWhenEntityHasNullYear() {
        var entity = new MovieEntity(null, "Test Movie", "Test Studio", "Test Producer", true);

        var result = MovieMapper.toDomain(entity);

        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when entity has null title")
    void shouldReturnNullWhenEntityHasNullTitle() {
        var entity = new MovieEntity(2020, null, "Test Studio", "Test Producer", true);

        var result = MovieMapper.toDomain(entity);

        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when entity has null studios")
    void shouldReturnNullWhenEntityHasNullStudios() {
        var entity = new MovieEntity(2020, "Test Movie", null, "Test Producer", true);

        var result = MovieMapper.toDomain(entity);

        assertNull(result);
    }

    @Test
    @DisplayName("Should return null when entity has null producers")
    void shouldReturnNullWhenEntityHasNullProducers() {
        var entity = new MovieEntity(2020, "Test Movie", "Test Studio", null, true);

        var result = MovieMapper.toDomain(entity);

        assertNull(result);
    }

    @Test
    @DisplayName("Should default winner to false when null")
    void shouldDefaultWinnerToFalseWhenNull() {
        var entity = new MovieEntity(2020, "Test Movie", "Test Studio", "Test Producer", null);

        var result = MovieMapper.toDomain(entity);

        assertNotNull(result);
        assertFalse(result.winner());
    }

    @Test
    @DisplayName("Should convert Domain Movie to MovieEntity")
    void shouldConvertDomainMovieToMovieEntity() {
        var domain = new Movie(2020, "Test Movie", "Test Studio", "Test Producer", true);

        var result = MovieMapper.toEntity(domain);

        assertNotNull(result);
        assertEquals(2020, result.getYear());
        assertEquals("Test Movie", result.getTitle());
        assertEquals("Test Studio", result.getStudios());
        assertEquals("Test Producer", result.getProducers());
        assertTrue(result.getWinner());
    }

    @Test
    @DisplayName("Should return null when domain is null")
    void shouldReturnNullWhenDomainIsNull() {
        var result = MovieMapper.toEntity(null);

        assertNull(result);
    }

    @Test
    @DisplayName("Should convert list of MovieEntity to list of Domain Movie")
    void shouldConvertListOfMovieEntityToListOfDomainMovie() {
        var entities = List.of(
                new MovieEntity(2020, "Movie 1", "Studio 1", "Producer 1", true),
                new MovieEntity(2021, "Movie 2", "Studio 2", "Producer 2", false)
        );

        var result = MovieMapper.toDomainList(entities);

        assertEquals(2, result.size());
        assertEquals(2020, result.get(0).year());
        assertEquals(2021, result.get(1).year());
    }

    @Test
    @DisplayName("Should return empty list when entities is null")
    void shouldReturnEmptyListWhenEntitiesIsNull() {
        var result = MovieMapper.toDomainList(null);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should filter null entities from list")
    void shouldFilterNullEntitiesFromList() {
        var entities = new java.util.ArrayList<MovieEntity>();
        entities.add(new MovieEntity(2020, "Movie 1", "Studio 1", "Producer 1", true));
        entities.add(null);
        entities.add(new MovieEntity(2021, "Movie 2", "Studio 2", "Producer 2", false));

        var result = MovieMapper.toDomainList(entities);

        assertEquals(2, result.size());
    }

    @Test
    @DisplayName("Should include null in result when entity has null required fields")
    void shouldIncludeNullInResultWhenEntityHasNullRequiredFields() {
        var entities = new java.util.ArrayList<MovieEntity>();
        entities.add(new MovieEntity(2020, "Movie 1", "Studio 1", "Producer 1", true));
        entities.add(new MovieEntity(null, "Movie 2", "Studio 2", "Producer 2", false));
        entities.add(new MovieEntity(2021, "Movie 3", "Studio 3", "Producer 3", true));

        var result = MovieMapper.toDomainList(entities);

        assertEquals(3, result.size(), "Result should contain 3 elements: valid Movie, null (from invalid entity), and valid Movie");
        assertNotNull(result.get(0), "First element should be a valid Movie");
        assertNull(result.get(1), "Second element should be null because entity has null year");
        assertNotNull(result.get(2), "Third element should be a valid Movie");
        assertEquals(2020, result.get(0).year());
        assertEquals(2021, result.get(2).year());
    }
}

