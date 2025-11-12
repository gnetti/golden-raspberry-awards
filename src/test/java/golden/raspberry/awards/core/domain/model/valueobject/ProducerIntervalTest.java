package golden.raspberry.awards.core.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProducerInterval Value Object Tests")
class ProducerIntervalTest {

    @Test
    @DisplayName("Should create interval with valid data")
    void shouldCreateIntervalWithValidData() {
        var interval = new ProducerInterval("John Doe", 5, 2010, 2015);

        assertEquals("John Doe", interval.producer());
        assertEquals(5, interval.interval());
        assertEquals(2010, interval.previousWin());
        assertEquals(2015, interval.followingWin());
    }

    @Test
    @DisplayName("Should create interval using factory method")
    void shouldCreateIntervalUsingFactoryMethod() {
        var interval = ProducerInterval.of("John Doe", 2010, 2015);

        assertEquals("John Doe", interval.producer());
        assertEquals(5, interval.interval());
        assertEquals(2010, interval.previousWin());
        assertEquals(2015, interval.followingWin());
    }

    @Test
    @DisplayName("Should throw exception when producer is null")
    void shouldThrowExceptionWhenProducerIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new ProducerInterval(null, 5, 2010, 2015));

        assertEquals("Producer cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when producer is blank")
    void shouldThrowExceptionWhenProducerIsBlank() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new ProducerInterval("   ", 5, 2010, 2015));

        assertTrue(exception.getMessage().contains("Producer cannot be blank"));
    }

    @Test
    @DisplayName("Should throw exception when interval is null")
    void shouldThrowExceptionWhenIntervalIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new ProducerInterval("John Doe", null, 2010, 2015));

        assertEquals("Interval cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when interval is negative")
    void shouldThrowExceptionWhenIntervalIsNegative() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new ProducerInterval("John Doe", -1, 2010, 2015));

        assertTrue(exception.getMessage().contains("Interval must be non-negative"));
    }

    @Test
    @DisplayName("Should throw exception when interval is zero with same years")
    void shouldThrowExceptionWhenIntervalIsZeroWithSameYears() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new ProducerInterval("John Doe", 0, 2010, 2010));

        assertTrue(exception.getMessage().contains("FollowingWin") && 
                   exception.getMessage().contains("must be greater than PreviousWin"));
    }

    @Test
    @DisplayName("Should throw exception when previousWin is null")
    void shouldThrowExceptionWhenPreviousWinIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new ProducerInterval("John Doe", 5, null, 2015));

        assertEquals("PreviousWin cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when followingWin is null")
    void shouldThrowExceptionWhenFollowingWinIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new ProducerInterval("John Doe", 5, 2010, null));

        assertEquals("FollowingWin cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when followingWin is not greater than previousWin")
    void shouldThrowExceptionWhenFollowingWinIsNotGreaterThanPreviousWin() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new ProducerInterval("John Doe", 5, 2015, 2010));

        assertTrue(exception.getMessage().contains("FollowingWin") && 
                   exception.getMessage().contains("must be greater than PreviousWin"));
    }

    @Test
    @DisplayName("Should throw exception when followingWin equals previousWin")
    void shouldThrowExceptionWhenFollowingWinEqualsPreviousWin() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new ProducerInterval("John Doe", 0, 2010, 2010));

        assertTrue(exception.getMessage().contains("FollowingWin") && 
                   exception.getMessage().contains("must be greater than PreviousWin"));
    }

    @Test
    @DisplayName("Should throw exception when interval does not match calculation")
    void shouldThrowExceptionWhenIntervalDoesNotMatchCalculation() {
        var exception = assertThrows(IllegalArgumentException.class, () ->
                new ProducerInterval("John Doe", 10, 2010, 2015));

        assertTrue(exception.getMessage().contains("Interval") && 
                   exception.getMessage().contains("must equal the difference"));
    }

    @Test
    @DisplayName("Should throw exception in factory method when producer is null")
    void shouldThrowExceptionInFactoryMethodWhenProducerIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                ProducerInterval.of(null, 2010, 2015));

        assertEquals("Producer cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception in factory method when previousWin is null")
    void shouldThrowExceptionInFactoryMethodWhenPreviousWinIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                ProducerInterval.of("John Doe", null, 2015));

        assertEquals("PreviousWin cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception in factory method when followingWin is null")
    void shouldThrowExceptionInFactoryMethodWhenFollowingWinIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                ProducerInterval.of("John Doe", 2010, null));

        assertEquals("FollowingWin cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should return formatted string representation")
    void shouldReturnFormattedStringRepresentation() {
        var interval = new ProducerInterval("John Doe", 5, 2010, 2015);
        var result = interval.toString();

        assertTrue(result.contains("John Doe"));
        assertTrue(result.contains("5"));
        assertTrue(result.contains("2010"));
        assertTrue(result.contains("2015"));
    }
}

