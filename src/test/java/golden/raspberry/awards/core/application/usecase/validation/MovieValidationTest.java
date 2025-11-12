package golden.raspberry.awards.core.application.usecase.validation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("MovieValidation Tests")
class MovieValidationTest {

    private static final int CURRENT_YEAR = Year.now().getValue();
    private static final int MIN_YEAR = 1900;

    @Test
    @DisplayName("Should validate valid year")
    void shouldValidateValidYear() {
        assertDoesNotThrow(() -> MovieValidation.validateYear(2020));
    }

    @Test
    @DisplayName("Should throw exception when year is null")
    void shouldThrowExceptionWhenYearIsNull() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                MovieValidation.validateYear(null));

        assertEquals("Year cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when year is below minimum")
    void shouldThrowExceptionWhenYearIsBelowMinimum() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                MovieValidation.validateYear(1899));

        assertTrue(exception.getMessage().contains("Year must be at least"));
    }

    @Test
    @DisplayName("Should throw exception when year is in future")
    void shouldThrowExceptionWhenYearIsInFuture() {
        var futureYear = CURRENT_YEAR + 1;
        var exception = assertThrows(IllegalArgumentException.class, () ->
                MovieValidation.validateYear(futureYear));

        assertTrue(exception.getMessage().contains("Year cannot be in the future"));
    }

    @Test
    @DisplayName("Should accept year at minimum boundary")
    void shouldAcceptYearAtMinimumBoundary() {
        assertDoesNotThrow(() -> MovieValidation.validateYear(MIN_YEAR));
    }

    @Test
    @DisplayName("Should accept current year")
    void shouldAcceptCurrentYear() {
        assertDoesNotThrow(() -> MovieValidation.validateYear(CURRENT_YEAR));
    }

    @Test
    @DisplayName("Should validate valid title")
    void shouldValidateValidTitle() {
        assertDoesNotThrow(() -> MovieValidation.validateTitle("Valid Title"));
    }

    @Test
    @DisplayName("Should throw exception when title is null")
    void shouldThrowExceptionWhenTitleIsNull() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                MovieValidation.validateTitle(null));

        assertEquals("Title cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when title is blank")
    void shouldThrowExceptionWhenTitleIsBlank() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                MovieValidation.validateTitle("   "));

        assertEquals("Title cannot be blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when title exceeds max length")
    void shouldThrowExceptionWhenTitleExceedsMaxLength() {
        var longTitle = "a".repeat(501);
        var exception = assertThrows(IllegalArgumentException.class, () ->
                MovieValidation.validateTitle(longTitle));

        assertTrue(exception.getMessage().contains("Title must be between"));
    }

    @Test
    @DisplayName("Should validate valid studios")
    void shouldValidateValidStudios() {
        assertDoesNotThrow(() -> MovieValidation.validateStudios("Valid Studios"));
    }

    @Test
    @DisplayName("Should throw exception when studios is null")
    void shouldThrowExceptionWhenStudiosIsNull() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                MovieValidation.validateStudios(null));

        assertEquals("Studios cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when studios is blank")
    void shouldThrowExceptionWhenStudiosIsBlank() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                MovieValidation.validateStudios("   "));

        assertEquals("Studios cannot be blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when studios exceeds max length")
    void shouldThrowExceptionWhenStudiosExceedsMaxLength() {
        var longStudios = "a".repeat(501);
        var exception = assertThrows(IllegalArgumentException.class, () ->
                MovieValidation.validateStudios(longStudios));

        assertTrue(exception.getMessage().contains("Studios must be between"));
    }

    @Test
    @DisplayName("Should validate valid producers")
    void shouldValidateValidProducers() {
        assertDoesNotThrow(() -> MovieValidation.validateProducers("Valid Producers"));
    }

    @Test
    @DisplayName("Should throw exception when producers is null")
    void shouldThrowExceptionWhenProducersIsNull() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                MovieValidation.validateProducers(null));

        assertEquals("Producers cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when producers is blank")
    void shouldThrowExceptionWhenProducersIsBlank() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                MovieValidation.validateProducers("   "));

        assertEquals("Producers cannot be blank", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when producers exceeds max length")
    void shouldThrowExceptionWhenProducersExceedsMaxLength() {
        var longProducers = "a".repeat(1001);
        var exception = assertThrows(IllegalArgumentException.class, () ->
                MovieValidation.validateProducers(longProducers));

        assertTrue(exception.getMessage().contains("Producers must be between"));
    }

    @Test
    @DisplayName("Should validate valid winner")
    void shouldValidateValidWinner() {
        assertDoesNotThrow(() -> MovieValidation.validateWinner(true));
        assertDoesNotThrow(() -> MovieValidation.validateWinner(false));
    }

    @Test
    @DisplayName("Should throw exception when winner is null")
    void shouldThrowExceptionWhenWinnerIsNull() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                MovieValidation.validateWinner(null));

        assertEquals("Winner cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should validate valid ID")
    void shouldValidateValidId() {
        assertDoesNotThrow(() -> MovieValidation.validateId(1L));
    }

    @Test
    @DisplayName("Should throw exception when ID is null")
    void shouldThrowExceptionWhenIdIsNull() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                MovieValidation.validateId(null));

        assertEquals("ID cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when ID is zero")
    void shouldThrowExceptionWhenIdIsZero() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                MovieValidation.validateId(0L));

        assertTrue(exception.getMessage().contains("ID must be positive"));
    }

    @Test
    @DisplayName("Should throw exception when ID is negative")
    void shouldThrowExceptionWhenIdIsNegative() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                MovieValidation.validateId(-1L));

        assertTrue(exception.getMessage().contains("ID must be positive"));
    }

    @Test
    @DisplayName("Should validate all movie data for creation")
    void shouldValidateAllMovieDataForCreation() {
        assertDoesNotThrow(() -> MovieValidation.validateMovieData(
                2020, "Title", "Studios", "Producers", true));
    }

    @Test
    @DisplayName("Should validate all movie data for update")
    void shouldValidateAllMovieDataForUpdate() {
        assertDoesNotThrow(() -> MovieValidation.validateMovieUpdateData(
                1L, 2020, "Title", "Studios", "Producers", true));
    }

    @Test
    @DisplayName("Should throw exception when validating update data with invalid ID")
    void shouldThrowExceptionWhenValidatingUpdateDataWithInvalidId() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                MovieValidation.validateMovieUpdateData(0L, 2020, "Title", "Studios", "Producers", true));

        assertTrue(exception.getMessage().contains("ID must be positive"));
    }

    @Test
    @DisplayName("Should throw exception when validating update data with null ID")
    void shouldThrowExceptionWhenValidatingUpdateDataWithNullId() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                MovieValidation.validateMovieUpdateData(null, 2020, "Title", "Studios", "Producers", true));

        assertEquals("ID cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when validating update data with invalid year")
    void shouldThrowExceptionWhenValidatingUpdateDataWithInvalidYear() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                MovieValidation.validateMovieUpdateData(1L, null, "Title", "Studios", "Producers", true));

        assertEquals("Year cannot be null", exception.getMessage());
    }
}

