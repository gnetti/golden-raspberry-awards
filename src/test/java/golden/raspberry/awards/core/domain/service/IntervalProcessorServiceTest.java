package golden.raspberry.awards.core.domain.service;

import golden.raspberry.awards.core.domain.model.aggregate.Movie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("IntervalProcessorService Domain Service Tests")
class IntervalProcessorServiceTest {

    private IntervalProcessorService service;

    @BeforeEach
    void setUp() {
        service = new IntervalProcessorService();
    }

    @Test
    @DisplayName("Should group wins by producer")
    void shouldGroupWinsByProducer() {
        var winningMovies = List.of(
                new Movie(2010, "Movie 1", "Studio", "John Doe", true),
                new Movie(2015, "Movie 2", "Studio", "John Doe", true),
                new Movie(2020, "Movie 3", "Studio", "Jane Smith", true)
        );

        var result = service.groupWinsByProducer(winningMovies);

        assertEquals(2, result.size());
        assertTrue(result.containsKey("John Doe"));
        assertTrue(result.containsKey("Jane Smith"));
        assertEquals(List.of(2010, 2015), result.get("John Doe"));
        assertEquals(List.of(2020), result.get("Jane Smith"));
    }

    @Test
    @DisplayName("Should handle multiple producers in same movie")
    void shouldHandleMultipleProducersInSameMovie() {
        var winningMovies = List.of(
                new Movie(2010, "Movie 1", "Studio", "John Doe, Jane Smith", true),
                new Movie(2015, "Movie 2", "Studio", "John Doe", true)
        );

        var result = service.groupWinsByProducer(winningMovies);

        assertEquals(2, result.size());
        assertEquals(List.of(2010, 2015), result.get("John Doe"));
        assertEquals(List.of(2010), result.get("Jane Smith"));
    }

    @Test
    @DisplayName("Should handle producers separated by and")
    void shouldHandleProducersSeparatedByAnd() {
        var winningMovies = List.of(
                new Movie(2010, "Movie 1", "Studio", "John Doe and Jane Smith", true)
        );

        var result = service.groupWinsByProducer(winningMovies);

        assertEquals(2, result.size());
        assertTrue(result.containsKey("John Doe"));
        assertTrue(result.containsKey("Jane Smith"));
    }

    @Test
    @DisplayName("Should sort years for each producer")
    void shouldSortYearsForEachProducer() {
        var winningMovies = List.of(
                new Movie(2020, "Movie 1", "Studio", "John Doe", true),
                new Movie(2010, "Movie 2", "Studio", "John Doe", true),
                new Movie(2015, "Movie 3", "Studio", "John Doe", true)
        );

        var result = service.groupWinsByProducer(winningMovies);

        assertEquals(List.of(2010, 2015, 2020), result.get("John Doe"));
    }

    @Test
    @DisplayName("Should remove duplicate years for same producer")
    void shouldRemoveDuplicateYearsForSameProducer() {
        var winningMovies = List.of(
                new Movie(2010, "Movie 1", "Studio", "John Doe", true),
                new Movie(2010, "Movie 2", "Studio", "John Doe", true),
                new Movie(2015, "Movie 3", "Studio", "John Doe", true)
        );

        var result = service.groupWinsByProducer(winningMovies);

        assertEquals(List.of(2010, 2015), result.get("John Doe"));
    }

    @Test
    @DisplayName("Should return empty map when winning movies is null")
    void shouldReturnEmptyMapWhenWinningMoviesIsNull() {
        var result = service.groupWinsByProducer(null);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty map when no winning movies")
    void shouldReturnEmptyMapWhenNoWinningMovies() {
        var result = service.groupWinsByProducer(List.of());

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should calculate intervals for producers with multiple wins")
    void shouldCalculateIntervalsForProducersWithMultipleWins() {
        var producerWins = Map.of(
                "John Doe", List.of(2010, 2015, 2020)
        );

        var result = service.calculateIntervals(producerWins);

        assertEquals(2, result.size());
        assertEquals(5, result.get(0).interval());
        assertEquals(5, result.get(1).interval());
    }

    @Test
    @DisplayName("Should filter out producers with less than two wins")
    void shouldFilterOutProducersWithLessThanTwoWins() {
        var producerWins = Map.of(
                "John Doe", List.of(2010, 2015),
                "Jane Smith", List.of(2020)
        );

        var result = service.calculateIntervals(producerWins);

        assertEquals(1, result.size());
        assertEquals("John Doe", result.getFirst().producer());
    }

    @Test
    @DisplayName("Should calculate correct intervals")
    void shouldCalculateCorrectIntervals() {
        var producerWins = Map.of(
                "John Doe", List.of(2010, 2015, 2025)
        );

        var result = service.calculateIntervals(producerWins);

        assertEquals(2, result.size());
        assertEquals(5, result.get(0).interval());
        assertEquals(10, result.get(1).interval());
    }

    @Test
    @DisplayName("Should return empty list when producer wins is null")
    void shouldReturnEmptyListWhenProducerWinsIsNull() {
        var result = service.calculateIntervals(null);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return empty list when no producers with multiple wins")
    void shouldReturnEmptyListWhenNoProducersWithMultipleWins() {
        var producerWins = Map.of(
                "John Doe", List.of(2010),
                "Jane Smith", List.of(2015)
        );

        var result = service.calculateIntervals(producerWins);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle multiple producers with intervals")
    void shouldHandleMultipleProducersWithIntervals() {
        var producerWins = Map.of(
                "John Doe", List.of(2010, 2015),
                "Jane Smith", List.of(2020, 2025)
        );

        var result = service.calculateIntervals(producerWins);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(i -> i.producer().equals("John Doe")));
        assertTrue(result.stream().anyMatch(i -> i.producer().equals("Jane Smith")));
    }

    @Test
    @DisplayName("Should filter null movies")
    void shouldFilterNullMovies() {
        var winningMovies = new java.util.ArrayList<Movie>();
        winningMovies.add(null);
        winningMovies.add(new Movie(2010, "Movie 1", "Studio", "John Doe", true));
        winningMovies.add(null);

        var result = service.groupWinsByProducer(winningMovies);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("John Doe"));
    }

    @Test
    @DisplayName("Should handle empty producer string")
    void shouldHandleEmptyProducerString() {
        var winningMovies = List.of(
                new Movie(2010, "Movie 1", "Studio", "", true),
                new Movie(2015, "Movie 2", "Studio", "John Doe", true)
        );

        var result = service.groupWinsByProducer(winningMovies);

        assertEquals(1, result.size());
        assertTrue(result.containsKey("John Doe"));
    }

    @Test
    @DisplayName("Should handle exception during Producer.parseMultiple with invalid format")
    void shouldHandleExceptionDuringProducerParseMultiple() {
        var winningMovies = List.of(
                new Movie(2010, "Movie 1", "Studio", "###INVALID###", true)
        );

        var result = service.groupWinsByProducer(winningMovies);

        assertNotNull(result);
    }

    @Test
    @DisplayName("Should filter null years in calculateIntervals")
    void shouldFilterNullYearsInCalculateIntervals() {
        var years = new java.util.ArrayList<Integer>();
        years.add(2010);
        years.add(null);
        years.add(2015);
        var producerWins = new java.util.HashMap<String, List<Integer>>();
        producerWins.put("John Doe", years);

        var result = service.calculateIntervals(producerWins);

        assertTrue(result.isEmpty(), "When null is between years, no valid intervals can be created because consecutive non-null years are required");
    }

    @Test
    @DisplayName("Should handle null entry value in calculateIntervals")
    void shouldHandleNullEntryValueInCalculateIntervals() {
        var producerWins = new java.util.HashMap<String, List<Integer>>();
        producerWins.put("John Doe", null);

        var result = service.calculateIntervals(producerWins);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle exception during ProducerInterval.of")
    void shouldHandleExceptionDuringProducerIntervalOf() {
        var producerWins = Map.of(
                "John Doe", List.of(2010, 2010)
        );

        var result = service.calculateIntervals(producerWins);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should handle exception during flatMap in calculateIntervals")
    void shouldHandleExceptionDuringFlatMapInCalculateIntervals() {
        var years = new java.util.ArrayList<Integer>();
        years.add(2010);
        years.add(2015);
        var producerWins = new java.util.HashMap<String, List<Integer>>();
        producerWins.put("John Doe", years);
        years.add(null);

        var result = service.calculateIntervals(producerWins);

        assertNotNull(result);
    }
}

