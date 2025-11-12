package golden.raspberry.awards.core.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PaginationNormalizerService Tests")
class PaginationNormalizerServiceTest {

    @Test
    @DisplayName("Should normalize page number to minimum when negative")
    void shouldNormalizePageNumberToMinimumWhenNegative() {
        var result = PaginationNormalizerService.normalizePage(-1);

        assertEquals(0, result);
    }

    @Test
    @DisplayName("Should keep page number when positive")
    void shouldKeepPageNumberWhenPositive() {
        var result = PaginationNormalizerService.normalizePage(5);

        assertEquals(5, result);
    }

    @Test
    @DisplayName("Should normalize page size to default when less than minimum")
    void shouldNormalizePageSizeToDefaultWhenLessThanMinimum() {
        var result = PaginationNormalizerService.normalizeSize(0);

        assertEquals(10, result);
    }

    @Test
    @DisplayName("Should normalize page size to maximum when greater than maximum")
    void shouldNormalizePageSizeToMaximumWhenGreaterThanMaximum() {
        var result = PaginationNormalizerService.normalizeSize(200);

        assertEquals(100, result);
    }

    @Test
    @DisplayName("Should keep page size when within valid range")
    void shouldKeepPageSizeWhenWithinValidRange() {
        var result = PaginationNormalizerService.normalizeSize(20);

        assertEquals(20, result);
    }

    @Test
    @DisplayName("Should normalize direction to asc when null")
    void shouldNormalizeDirectionToAscWhenNull() {
        var result = PaginationNormalizerService.normalizeDirection(null);

        assertEquals("asc", result);
    }

    @Test
    @DisplayName("Should normalize direction to asc when blank")
    void shouldNormalizeDirectionToAscWhenBlank() {
        var result = PaginationNormalizerService.normalizeDirection("   ");

        assertEquals("asc", result);
    }

    @Test
    @DisplayName("Should normalize direction to desc when desc")
    void shouldNormalizeDirectionToDescWhenDesc() {
        var result = PaginationNormalizerService.normalizeDirection("desc");

        assertEquals("desc", result);
    }

    @Test
    @DisplayName("Should normalize direction to desc when DESC")
    void shouldNormalizeDirectionToDescWhenDESC() {
        var result = PaginationNormalizerService.normalizeDirection("DESC");

        assertEquals("desc", result);
    }

    @Test
    @DisplayName("Should normalize direction to asc when invalid")
    void shouldNormalizeDirectionToAscWhenInvalid() {
        var result = PaginationNormalizerService.normalizeDirection("invalid");

        assertEquals("asc", result);
    }

    @Test
    @DisplayName("Should normalize sort field")
    void shouldNormalizeSortField() {
        var result = PaginationNormalizerService.normalizeSortField("title");

        assertEquals("title", result);
    }

    @Test
    @DisplayName("Should throw exception when sort field is null")
    void shouldThrowExceptionWhenSortFieldIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                PaginationNormalizerService.normalizeSortField(null));

        assertEquals("Sort field cannot be null", exception.getMessage());
    }
}

