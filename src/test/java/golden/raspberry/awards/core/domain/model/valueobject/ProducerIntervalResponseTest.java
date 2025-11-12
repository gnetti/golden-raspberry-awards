package golden.raspberry.awards.core.domain.model.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("ProducerIntervalResponse Value Object Tests")
class ProducerIntervalResponseTest {

    @Test
    @DisplayName("Should create response with valid intervals")
    void shouldCreateResponseWithValidIntervals() {
        var minInterval = ProducerInterval.of("Producer 1", 2010, 2015);
        var maxInterval = ProducerInterval.of("Producer 2", 2015, 2025);

        var response = new ProducerIntervalResponse(
                List.of(minInterval),
                List.of(maxInterval)
        );

        assertEquals(1, response.min().size());
        assertEquals(1, response.max().size());
        assertEquals("Producer 1", response.min().get(0).producer());
        assertEquals("Producer 2", response.max().get(0).producer());
    }

    @Test
    @DisplayName("Should create response with empty lists")
    void shouldCreateResponseWithEmptyLists() {
        var response = new ProducerIntervalResponse(List.of(), List.of());

        assertTrue(response.min().isEmpty());
        assertTrue(response.max().isEmpty());
    }

    @Test
    @DisplayName("Should throw exception when min list is null")
    void shouldThrowExceptionWhenMinListIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new ProducerIntervalResponse(null, List.of()));

        assertEquals("Min list cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when max list is null")
    void shouldThrowExceptionWhenMaxListIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new ProducerIntervalResponse(List.of(), null));

        assertEquals("Max list cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should create response with multiple intervals")
    void shouldCreateResponseWithMultipleIntervals() {
        var minIntervals = List.of(
                ProducerInterval.of("Producer 1", 2010, 2015),
                ProducerInterval.of("Producer 2", 2012, 2017)
        );
        var maxIntervals = List.of(
                ProducerInterval.of("Producer 3", 2015, 2025)
        );

        var response = new ProducerIntervalResponse(minIntervals, maxIntervals);

        assertEquals(2, response.min().size());
        assertEquals(1, response.max().size());
    }
}

