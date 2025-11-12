package golden.raspberry.awards.core.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("SortFieldMapper Tests")
class SortFieldMapperTest {

    @Test
    @DisplayName("Should map year field correctly")
    void shouldMapYearFieldCorrectly() {
        var result = SortFieldMapper.mapSortField("year");

        assertEquals("year", result);
    }

    @Test
    @DisplayName("Should map title field correctly")
    void shouldMapTitleFieldCorrectly() {
        var result = SortFieldMapper.mapSortField("title");

        assertEquals("title", result);
    }

    @Test
    @DisplayName("Should map studios field correctly")
    void shouldMapStudiosFieldCorrectly() {
        var result = SortFieldMapper.mapSortField("studios");

        assertEquals("studios", result);
    }

    @Test
    @DisplayName("Should map producers field correctly")
    void shouldMapProducersFieldCorrectly() {
        var result = SortFieldMapper.mapSortField("producers");

        assertEquals("producers", result);
    }

    @Test
    @DisplayName("Should map winner field correctly")
    void shouldMapWinnerFieldCorrectly() {
        var result = SortFieldMapper.mapSortField("winner");

        assertEquals("winner", result);
    }

    @Test
    @DisplayName("Should return default id field for invalid field")
    void shouldReturnDefaultIdFieldForInvalidField() {
        var result = SortFieldMapper.mapSortField("invalid");

        assertEquals("id", result);
    }

    @Test
    @DisplayName("Should handle case insensitive mapping")
    void shouldHandleCaseInsensitiveMapping() {
        assertEquals("year", SortFieldMapper.mapSortField("YEAR"));
        assertEquals("title", SortFieldMapper.mapSortField("Title"));
        assertEquals("studios", SortFieldMapper.mapSortField("STUDIOS"));
    }

    @Test
    @DisplayName("Should throw exception when sort field is null")
    void shouldThrowExceptionWhenSortFieldIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                SortFieldMapper.mapSortField(null));

        assertEquals("Sort field cannot be null", exception.getMessage());
    }
}

