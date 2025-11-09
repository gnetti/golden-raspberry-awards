package golden.raspberry.awards.core.domain.service;

import golden.raspberry.awards.core.domain.model.Movie;
import golden.raspberry.awards.core.domain.model.Producer;
import golden.raspberry.awards.core.domain.model.ProducerInterval;

import java.util.*;

/**
 * Domain service for calculating producer intervals.
 * Contains ALL business rules for interval calculations.
 *
 * <p>This service is part of the Domain layer and contains
 * pure business logic with zero external dependencies.
 *
 * <p><strong>Domain Service Rules:</strong>
 * <ul>
 *   <li>Contains ALL business rules for interval calculations</li>
 *   <li>Zero external dependencies</li>
 *   <li>Pure business logic only</li>
 *   <li>No infrastructure concerns</li>
 * </ul>
 *
 * <p>Uses Java 21 features: var, Streams, Collections, Pattern Matching.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public class ProducerIntervalCalculator {

    public ProducerIntervalCalculator() {
    }

    /**
     * Groups winning movies by producer.
     * Handles multiple producers per movie (comma and "and" separated).
     * Removes duplicate years for the same producer (when a producer wins multiple times in the same year).
     *
     * @param winningMovies List of winning movies
     * @return Map of producer names to their unique winning years (sorted)
     */
    public Map<String, List<Integer>> groupWinsByProducer(List<Movie> winningMovies) {
        Objects.requireNonNull(winningMovies, "Winning movies cannot be null");
        
        Map<String, List<Integer>> producerWins = new HashMap<>();

        for (Movie movie : winningMovies) {
            List<Producer> producers = Producer.parseMultiple(movie.producers());

            for (Producer producer : producers) {
                producerWins.computeIfAbsent(producer.name(), k -> new ArrayList<>())
                        .add(movie.year());
            }
        }

        producerWins.replaceAll((producer, years) -> {
            var uniqueYears = new LinkedHashSet<>(years);
            var sortedYears = new ArrayList<>(uniqueYears);
            Collections.sort(sortedYears);
            return sortedYears;
        });

        return producerWins;
    }

    /**
     * Calculates intervals between consecutive wins for each producer.
     *
     * @param producerWins Map of producer names to their winning years
     * @return List of intervals for all producers
     */
    public List<ProducerInterval> calculateIntervals(Map<String, List<Integer>> producerWins) {
        Objects.requireNonNull(producerWins, "Producer wins cannot be null");
        
        List<ProducerInterval> intervals = new ArrayList<>();

        for (Map.Entry<String, List<Integer>> entry : producerWins.entrySet()) {
            String producer = entry.getKey();
            List<Integer> years = entry.getValue();

            if (years.size() < 2) {
                continue;
            }

            for (int i = 0; i < years.size() - 1; i++) {
                Integer previousWin = years.get(i);
                Integer followingWin = years.get(i + 1);

                intervals.add(ProducerInterval.of(producer, previousWin, followingWin));
            }
        }

        return intervals;
    }
}

