package golden.raspberry.awards.core.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("PaginationService Tests")
class PaginationServiceTest {

    @Test
    @DisplayName("Should return all pages when total pages is less than max visible")
    void shouldReturnAllPagesWhenTotalPagesIsLessThanMaxVisible() {
        var result = PaginationService.calculatePageNumbers(0, 5);

        assertEquals(List.of(0, 1, 2, 3, 4), result);
    }

    @Test
    @DisplayName("Should return all pages when total pages equals max visible")
    void shouldReturnAllPagesWhenTotalPagesEqualsMaxVisible() {
        var result = PaginationService.calculatePageNumbers(0, 7);

        assertEquals(List.of(0, 1, 2, 3, 4, 5, 6), result);
    }

    @Test
    @DisplayName("Should generate early sequence when current page is near beginning")
    void shouldGenerateEarlySequenceWhenCurrentPageIsNearBeginning() {
        var result = PaginationService.calculatePageNumbers(2, 20);

        assertEquals(0, result.get(0));
        assertTrue(result.contains(-1));
        assertEquals(19, result.get(result.size() - 1));
    }

    @Test
    @DisplayName("Should generate late sequence when current page is near end")
    void shouldGenerateLateSequenceWhenCurrentPageIsNearEnd() {
        var result = PaginationService.calculatePageNumbers(18, 20);

        assertEquals(0, result.get(0));
        assertTrue(result.contains(-1));
        assertEquals(19, result.get(result.size() - 1));
    }

    @Test
    @DisplayName("Should generate middle sequence when current page is in middle")
    void shouldGenerateMiddleSequenceWhenCurrentPageIsInMiddle() {
        var result = PaginationService.calculatePageNumbers(10, 20);

        assertEquals(0, result.get(0));
        assertTrue(result.contains(-1));
        assertTrue(result.contains(9));
        assertTrue(result.contains(10));
        assertTrue(result.contains(11));
        assertTrue(result.contains(-1));
        assertEquals(19, result.get(result.size() - 1));
    }

    @Test
    @DisplayName("Should throw exception when current page is negative")
    void shouldThrowExceptionWhenCurrentPageIsNegative() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                PaginationService.calculatePageNumbers(-1, 10));

        assertTrue(exception.getMessage().contains("Current page must be >= 0"));
    }

    @Test
    @DisplayName("Should return empty list when total pages is zero")
    void shouldReturnEmptyListWhenTotalPagesIsZero() {
        var result = PaginationService.calculatePageNumbers(0, 0);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should throw exception when current page is greater than or equal to total pages")
    void shouldThrowExceptionWhenCurrentPageIsGreaterThanOrEqualToTotalPages() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                PaginationService.calculatePageNumbers(10, 10));

        assertTrue(exception.getMessage().contains("Current page") && 
                   exception.getMessage().contains("must be < total pages"));
    }

    @Test
    @DisplayName("Should handle page zero correctly")
    void shouldHandlePageZeroCorrectly() {
        var result = PaginationService.calculatePageNumbers(0, 10);

        assertEquals(0, result.get(0));
    }

    @Test
    @DisplayName("Should handle last page correctly")
    void shouldHandleLastPageCorrectly() {
        var result = PaginationService.calculatePageNumbers(9, 10);

        assertTrue(result.contains(9));
    }

    @Test
    @DisplayName("Should throw exception when total pages is negative")
    void shouldThrowExceptionWhenTotalPagesIsNegative() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                PaginationService.calculatePageNumbers(0, -1));

        assertTrue(exception.getMessage().contains("Total pages must be >= 1"));
    }


    @Test
    @DisplayName("Should handle early sequence at threshold boundary")
    void shouldHandleEarlySequenceAtThresholdBoundary() {
        var result = PaginationService.calculatePageNumbers(3, 20);

        assertEquals(0, result.get(0));
        assertTrue(result.contains(-1));
        assertEquals(19, result.get(result.size() - 1));
    }

    @Test
    @DisplayName("Should handle late sequence at threshold boundary")
    void shouldHandleLateSequenceAtThresholdBoundary() {
        var result = PaginationService.calculatePageNumbers(16, 20);

        assertEquals(0, result.get(0));
        assertTrue(result.contains(-1));
        assertEquals(19, result.get(result.size() - 1));
    }

    @Test
    @DisplayName("Should throw AssertionError when trying to instantiate PaginationService")
    void shouldThrowAssertionErrorWhenTryingToInstantiatePaginationService() throws Exception {
        var constructor = PaginationService.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        var exception = assertThrows(Exception.class, constructor::newInstance);
        
        var cause = exception.getCause();
        assertNotNull(cause);
        assertInstanceOf(AssertionError.class, cause);
        assertEquals("Utility class cannot be instantiated", cause.getMessage());
    }
}

