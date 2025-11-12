package golden.raspberry.awards.core.application.service;

import golden.raspberry.awards.core.application.port.out.MovieRepositoryPort;
import golden.raspberry.awards.core.domain.model.aggregate.Movie;
import golden.raspberry.awards.core.domain.service.IntervalProcessorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("IntervalProcessorService Application Service Tests")
class IntervalProcessorServiceApplicationTest {

    private MovieRepositoryPort repository;
    private IntervalProcessorService domainCalculator;
    private golden.raspberry.awards.core.application.service.IntervalProcessorService applicationService;

    @BeforeEach
    void setUp() {
        repository = mock(MovieRepositoryPort.class);
        domainCalculator = new IntervalProcessorService();
        applicationService = new golden.raspberry.awards.core.application.service.IntervalProcessorService(repository, domainCalculator);
    }

    @Test
    @DisplayName("Should calculate intervals and return response with min and max")
    void shouldCalculateIntervalsAndReturnResponseWithMinAndMax() {
        var winningMovies = List.of(
                new Movie(2010, "Movie 1", "Studio", "John Doe", true),
                new Movie(2015, "Movie 2", "Studio", "John Doe", true),
                new Movie(2020, "Movie 3", "Studio", "Jane Smith", true),
                new Movie(2025, "Movie 4", "Studio", "Jane Smith", true)
        );

        when(repository.findByWinnerTrue()).thenReturn(winningMovies);

        var result = applicationService.calculate();

        assertNotNull(result);
        assertEquals(2, result.min().size());
        assertEquals(2, result.max().size());
        assertEquals(5, result.min().get(0).interval());
        assertEquals(5, result.max().get(0).interval());
        verify(repository).findByWinnerTrue();
    }

    @Test
    @DisplayName("Should return empty response when no winning movies")
    void shouldReturnEmptyResponseWhenNoWinningMovies() {
        when(repository.findByWinnerTrue()).thenReturn(List.of());

        var result = applicationService.calculate();

        assertTrue(result.min().isEmpty());
        assertTrue(result.max().isEmpty());
        verify(repository).findByWinnerTrue();
    }

    @Test
    @DisplayName("Should return empty response when no intervals calculated")
    void shouldReturnEmptyResponseWhenNoIntervalsCalculated() {
        var winningMovies = List.of(
                new Movie(2010, "Movie 1", "Studio", "John Doe", true)
        );

        when(repository.findByWinnerTrue()).thenReturn(winningMovies);

        var result = applicationService.calculate();

        assertTrue(result.min().isEmpty());
        assertTrue(result.max().isEmpty());
    }

    @Test
    @DisplayName("Should filter intervals by min and max values")
    void shouldFilterIntervalsByMinAndMaxValues() {
        var winningMovies = List.of(
                new Movie(2010, "Movie 1", "Studio", "John Doe", true),
                new Movie(2011, "Movie 2", "Studio", "John Doe", true),
                new Movie(2020, "Movie 3", "Studio", "Jane Smith", true),
                new Movie(2030, "Movie 4", "Studio", "Jane Smith", true)
        );

        when(repository.findByWinnerTrue()).thenReturn(winningMovies);

        var result = applicationService.calculate();

        assertEquals(1, result.min().size());
        assertEquals(1, result.max().size());
        assertEquals(1, result.min().get(0).interval());
        assertEquals(10, result.max().get(0).interval());
    }

    @Test
    @DisplayName("Should throw exception when repository is null")
    void shouldThrowExceptionWhenRepositoryIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new golden.raspberry.awards.core.application.service.IntervalProcessorService(null, domainCalculator));

        assertEquals("MovieRepositoryPort cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when calculator is null")
    void shouldThrowExceptionWhenCalculatorIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new golden.raspberry.awards.core.application.service.IntervalProcessorService(repository, null));

        assertEquals("IntervalProcessorService cannot be null", exception.getMessage());
    }


    @Test
    @DisplayName("Should return empty response when winning movies is null")
    void shouldReturnEmptyResponseWhenWinningMoviesIsNull() {
        when(repository.findByWinnerTrue()).thenReturn(null);

        var result = applicationService.calculate();

        assertTrue(result.min().isEmpty());
        assertTrue(result.max().isEmpty());
        verify(repository).findByWinnerTrue();
    }

    @Test
    @DisplayName("Should handle exception during calculation")
    void shouldHandleExceptionDuringCalculation() {
        when(repository.findByWinnerTrue()).thenThrow(new RuntimeException("Database error"));

        var result = applicationService.calculate();

        assertTrue(result.min().isEmpty());
        assertTrue(result.max().isEmpty());
    }

    @Test
    @DisplayName("Should handle exception during groupWinsByProducer")
    void shouldHandleExceptionDuringGroupWinsByProducer() {
        var winningMovies = List.of(
                new Movie(2010, "Movie 1", "Studio", "John Doe", true)
        );
        when(repository.findByWinnerTrue()).thenReturn(winningMovies);
        var mockCalculator = mock(IntervalProcessorService.class);
        when(mockCalculator.groupWinsByProducer(any())).thenThrow(new RuntimeException("Error"));
        var service = new golden.raspberry.awards.core.application.service.IntervalProcessorService(repository, mockCalculator);

        var result = service.calculate();

        assertTrue(result.min().isEmpty());
        assertTrue(result.max().isEmpty());
    }

    @Test
    @DisplayName("Should handle exception during calculateIntervals")
    void shouldHandleExceptionDuringCalculateIntervals() {
        var winningMovies = List.of(
                new Movie(2010, "Movie 1", "Studio", "John Doe", true),
                new Movie(2015, "Movie 2", "Studio", "John Doe", true)
        );
        when(repository.findByWinnerTrue()).thenReturn(winningMovies);
        var mockCalculator = mock(IntervalProcessorService.class);
        when(mockCalculator.groupWinsByProducer(any())).thenReturn(java.util.Map.of("John Doe", List.of(2010, 2015)));
        when(mockCalculator.calculateIntervals(any())).thenThrow(new RuntimeException("Error"));
        var service = new golden.raspberry.awards.core.application.service.IntervalProcessorService(repository, mockCalculator);

        var result = service.calculate();

        assertTrue(result.min().isEmpty());
        assertTrue(result.max().isEmpty());
    }

    @Test
    @DisplayName("Should handle multiple intervals with same min and max values")
    void shouldHandleMultipleIntervalsWithSameMinAndMaxValues() {
        var winningMovies = List.of(
                new Movie(2010, "Movie 1", "Studio", "Producer A", true),
                new Movie(2015, "Movie 2", "Studio", "Producer A", true),
                new Movie(2010, "Movie 3", "Studio", "Producer B", true),
                new Movie(2015, "Movie 4", "Studio", "Producer B", true)
        );

        when(repository.findByWinnerTrue()).thenReturn(winningMovies);

        var result = applicationService.calculate();

        assertNotNull(result);
        assertEquals(2, result.min().size());
        assertEquals(2, result.max().size());
        assertEquals(5, result.min().get(0).interval());
        assertEquals(5, result.max().get(0).interval());
    }

    @Test
    @DisplayName("Should handle case when allIntervals is empty after calculation")
    void shouldHandleCaseWhenAllIntervalsIsEmptyAfterCalculation() {
        var winningMovies = List.of(
                new Movie(2010, "Movie 1", "Studio", "Producer A", true)
        );
        when(repository.findByWinnerTrue()).thenReturn(winningMovies);
        var mockCalculator = mock(IntervalProcessorService.class);
        when(mockCalculator.groupWinsByProducer(any())).thenReturn(java.util.Map.of("Producer A", List.of(2010)));
        when(mockCalculator.calculateIntervals(any())).thenReturn(List.of());
        var service = new golden.raspberry.awards.core.application.service.IntervalProcessorService(repository, mockCalculator);

        var result = service.calculate();

        assertTrue(result.min().isEmpty());
        assertTrue(result.max().isEmpty());
    }

    @Test
    @DisplayName("Should handle case when producerWins is empty")
    void shouldHandleCaseWhenProducerWinsIsEmpty() {
        var winningMovies = List.of(
                new Movie(2010, "Movie 1", "Studio", "Producer A", true)
        );
        when(repository.findByWinnerTrue()).thenReturn(winningMovies);
        var mockCalculator = mock(IntervalProcessorService.class);
        when(mockCalculator.groupWinsByProducer(any())).thenReturn(java.util.Map.of());
        var service = new golden.raspberry.awards.core.application.service.IntervalProcessorService(repository, mockCalculator);

        var result = service.calculate();

        assertTrue(result.min().isEmpty());
        assertTrue(result.max().isEmpty());
    }

    @Test
    @DisplayName("Should handle case when min and max intervals are different")
    void shouldHandleCaseWhenMinAndMaxIntervalsAreDifferent() {
        var winningMovies = List.of(
                new Movie(2010, "Movie 1", "Studio", "Producer A", true),
                new Movie(2011, "Movie 2", "Studio", "Producer A", true),
                new Movie(2010, "Movie 3", "Studio", "Producer B", true),
                new Movie(2020, "Movie 4", "Studio", "Producer B", true)
        );

        when(repository.findByWinnerTrue()).thenReturn(winningMovies);

        var result = applicationService.calculate();

        assertNotNull(result);
        assertFalse(result.min().isEmpty());
        assertFalse(result.max().isEmpty());
        assertNotEquals(result.min().get(0).interval(), result.max().get(0).interval());
    }

    @Test
    @DisplayName("Should handle case when only one interval exists")
    void shouldHandleCaseWhenOnlyOneIntervalExists() {
        var winningMovies = List.of(
                new Movie(2010, "Movie 1", "Studio", "Producer A", true),
                new Movie(2015, "Movie 2", "Studio", "Producer A", true)
        );

        when(repository.findByWinnerTrue()).thenReturn(winningMovies);

        var result = applicationService.calculate();

        assertNotNull(result);
        assertEquals(1, result.min().size());
        assertEquals(1, result.max().size());
        assertEquals(result.min().get(0).interval(), result.max().get(0).interval());
    }

    @Test
    @DisplayName("Should handle case when minInterval equals maxInterval with multiple intervals")
    void shouldHandleCaseWhenMinIntervalEqualsMaxIntervalWithMultipleIntervals() {
        var winningMovies = List.of(
                new Movie(2010, "Movie 1", "Studio", "Producer A", true),
                new Movie(2015, "Movie 2", "Studio", "Producer A", true),
                new Movie(2010, "Movie 3", "Studio", "Producer B", true),
                new Movie(2015, "Movie 4", "Studio", "Producer B", true),
                new Movie(2010, "Movie 5", "Studio", "Producer C", true),
                new Movie(2015, "Movie 6", "Studio", "Producer C", true)
        );

        when(repository.findByWinnerTrue()).thenReturn(winningMovies);

        var result = applicationService.calculate();

        assertNotNull(result);
        assertFalse(result.min().isEmpty());
        assertFalse(result.max().isEmpty());
        assertEquals(result.min().get(0).interval(), result.max().get(0).interval());
    }

    @Test
    @DisplayName("Should handle case when producerWins map is null")
    void shouldHandleCaseWhenProducerWinsMapIsNull() {
        var winningMovies = List.of(
                new Movie(2010, "Movie 1", "Studio", "Producer A", true)
        );
        when(repository.findByWinnerTrue()).thenReturn(winningMovies);
        var mockCalculator = mock(IntervalProcessorService.class);
        when(mockCalculator.groupWinsByProducer(any())).thenReturn(null);
        var service = new golden.raspberry.awards.core.application.service.IntervalProcessorService(repository, mockCalculator);

        var result = service.calculate();

        assertTrue(result.min().isEmpty());
        assertTrue(result.max().isEmpty());
    }

    @Test
    @DisplayName("Should handle case when producerWins map is empty")
    void shouldHandleCaseWhenProducerWinsMapIsEmpty() {
        var winningMovies = List.of(
                new Movie(2010, "Movie 1", "Studio", "Producer A", true)
        );
        when(repository.findByWinnerTrue()).thenReturn(winningMovies);
        var mockCalculator = mock(IntervalProcessorService.class);
        when(mockCalculator.groupWinsByProducer(any())).thenReturn(java.util.Map.of());
        when(mockCalculator.calculateIntervals(any())).thenReturn(List.of());
        var service = new golden.raspberry.awards.core.application.service.IntervalProcessorService(repository, mockCalculator);

        var result = service.calculate();

        assertTrue(result.min().isEmpty());
        assertTrue(result.max().isEmpty());
    }

    @Test
    @DisplayName("Should handle case when allIntervals contains null values")
    void shouldHandleCaseWhenAllIntervalsContainsNullValues() {
        var winningMovies = List.of(
                new Movie(2010, "Movie 1", "Studio", "Producer A", true),
                new Movie(2015, "Movie 2", "Studio", "Producer A", true)
        );
        when(repository.findByWinnerTrue()).thenReturn(winningMovies);
        var mockCalculator = mock(IntervalProcessorService.class);
        when(mockCalculator.groupWinsByProducer(any())).thenReturn(java.util.Map.of("Producer A", List.of(2010, 2015)));
        var intervalsWithNull = new ArrayList<golden.raspberry.awards.core.domain.model.valueobject.ProducerInterval>();
        intervalsWithNull.add(golden.raspberry.awards.core.domain.model.valueobject.ProducerInterval.of("Producer A", 2010, 2015));
        intervalsWithNull.add(null);
        when(mockCalculator.calculateIntervals(any())).thenReturn(intervalsWithNull);
        var service = new golden.raspberry.awards.core.application.service.IntervalProcessorService(repository, mockCalculator);

        var result = service.calculate();

        assertNotNull(result);
        assertTrue(result.min().isEmpty());
        assertTrue(result.max().isEmpty());
    }

    @Test
    @DisplayName("Should access repository field")
    void shouldAccessRepositoryField() {
        assertNotNull(applicationService.repository());
        assertEquals(repository, applicationService.repository());
    }

    @Test
    @DisplayName("Should access calculator field")
    void shouldAccessCalculatorField() {
        assertNotNull(applicationService.calculator());
        assertEquals(domainCalculator, applicationService.calculator());
    }

    @Test
    @DisplayName("Should have equal instances")
    void shouldHaveEqualInstances() {
        var service1 = new golden.raspberry.awards.core.application.service.IntervalProcessorService(repository, domainCalculator);
        var service2 = new golden.raspberry.awards.core.application.service.IntervalProcessorService(repository, domainCalculator);

        assertEquals(service1, service2);
        assertEquals(service1.hashCode(), service2.hashCode());
    }

    @Test
    @DisplayName("Should have different instances when repository differs")
    void shouldHaveDifferentInstancesWhenRepositoryDiffers() {
        var repository2 = mock(MovieRepositoryPort.class);
        var service1 = new golden.raspberry.awards.core.application.service.IntervalProcessorService(repository, domainCalculator);
        var service2 = new golden.raspberry.awards.core.application.service.IntervalProcessorService(repository2, domainCalculator);

        assertNotEquals(service1, service2);
    }

    @Test
    @DisplayName("Should have different instances when calculator differs")
    void shouldHaveDifferentInstancesWhenCalculatorDiffers() {
        var calculator2 = new IntervalProcessorService();
        var service1 = new golden.raspberry.awards.core.application.service.IntervalProcessorService(repository, domainCalculator);
        var service2 = new golden.raspberry.awards.core.application.service.IntervalProcessorService(repository, calculator2);

        assertNotEquals(service1, service2);
    }

    @Test
    @DisplayName("Should have toString method")
    void shouldHaveToStringMethod() {
        var toString = applicationService.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("IntervalProcessorService"));
    }
}

