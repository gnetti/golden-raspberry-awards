package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.service.IntervalProcessorService;
import golden.raspberry.awards.core.domain.model.valueobject.ProducerIntervalResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CalculateIntervalsPortHandler Tests")
class CalculateIntervalsPortHandlerTest {

    private IntervalProcessorService calculationService;
    private CalculateIntervalsPortHandler handler;

    @BeforeEach
    void setUp() {
        calculationService = mock(IntervalProcessorService.class);
        handler = new CalculateIntervalsPortHandler(calculationService);
    }

    @Test
    @DisplayName("Should execute calculation and return response")
    void shouldExecuteCalculationAndReturnResponse() {
        var expectedResponse = new ProducerIntervalResponse(
                java.util.List.of(),
                java.util.List.of()
        );

        when(calculationService.calculate()).thenReturn(expectedResponse);

        var result = handler.execute();

        assertEquals(expectedResponse, result);
        verify(calculationService).calculate();
    }

    @Test
    @DisplayName("Should throw exception when calculation service is null")
    void shouldThrowExceptionWhenCalculationServiceIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new CalculateIntervalsPortHandler(null));

        assertEquals("IntervalProcessorService cannot be null", exception.getMessage());
    }


    @Test
    @DisplayName("Should return empty response when result is null")
    void shouldReturnEmptyResponseWhenResultIsNull() {
        when(calculationService.calculate()).thenReturn(null);

        var result = handler.execute();

        assertTrue(result.min().isEmpty());
        assertTrue(result.max().isEmpty());
        verify(calculationService).calculate();
    }

    @Test
    @DisplayName("Should handle exception during calculation")
    void shouldHandleExceptionDuringCalculation() {
        when(calculationService.calculate()).thenThrow(new RuntimeException("Error"));

        var result = handler.execute();

        assertTrue(result.min().isEmpty());
        assertTrue(result.max().isEmpty());
        verify(calculationService).calculate();
    }

    @Test
    @DisplayName("Should access calculationService field")
    void shouldAccessCalculationServiceField() {
        assertNotNull(handler.calculationService());
        assertEquals(calculationService, handler.calculationService());
    }

    @Test
    @DisplayName("Should have equal instances")
    void shouldHaveEqualInstances() {
        var handler1 = new CalculateIntervalsPortHandler(calculationService);
        var handler2 = new CalculateIntervalsPortHandler(calculationService);

        assertEquals(handler1, handler2);
        assertEquals(handler1.hashCode(), handler2.hashCode());
    }

    @Test
    @DisplayName("Should have different instances when calculationService differs")
    void shouldHaveDifferentInstancesWhenCalculationServiceDiffers() {
        var calculationService2 = mock(IntervalProcessorService.class);
        var handler1 = new CalculateIntervalsPortHandler(calculationService);
        var handler2 = new CalculateIntervalsPortHandler(calculationService2);

        assertNotEquals(handler1, handler2);
    }

    @Test
    @DisplayName("Should have toString method")
    void shouldHaveToStringMethod() {
        var toString = handler.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("CalculateIntervalsPortHandler"));
    }
}

