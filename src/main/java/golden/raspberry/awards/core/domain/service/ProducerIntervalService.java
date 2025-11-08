package golden.raspberry.awards.core.domain.service;

import golden.raspberry.awards.core.domain.model.Movie;
import golden.raspberry.awards.core.domain.model.Producer;
import golden.raspberry.awards.core.domain.model.ProducerInterval;
import golden.raspberry.awards.core.domain.model.ProducerIntervalResponse;
import golden.raspberry.awards.core.domain.port.out.MovieRepositoryPort;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Domain Service implementing the business logic for calculating producer intervals.
 * Pure Java - no Spring annotations, no framework dependencies.
 *
 * <p>This service contains pure business logic for:
 * <ul>
 *   <li>Grouping winning movies by producer</li>
 *   <li>Calculating intervals between consecutive wins</li>
 *   <li>Finding minimum and maximum intervals</li>
 * </ul>
 *
 * <p>Uses Java 21 features: Records, Pattern Matching, Streams.
 *
 * @author Golden Raspberry Awards Team
 * @since 1.0.0
 */
public record ProducerIntervalService(MovieRepositoryPort repository) {

    /**
     * Compact constructor for validation.
     *
     * @param repository Movie repository port (output port)
     */
    public ProducerIntervalService {
        Objects.requireNonNull(repository, "Repository cannot be null");
    }

    /**
     * Calculates producer intervals based on winning movies.
     * This is pure business logic - no orchestration concerns.
     *
     * @return ProducerIntervalResponse with min and max intervals
     */
    public ProducerIntervalResponse calculateIntervals() {
        List<Movie> winningMovies = repository.findByWinnerTrue();

        if (winningMovies.isEmpty()) {
            return new ProducerIntervalResponse(List.of(), List.of());
        }

        Map<String, List<Integer>> producerWins = groupWinsByProducer(winningMovies);

        List<ProducerInterval> allIntervals = calculateIntervals(producerWins);

        if (allIntervals.isEmpty()) {
            return new ProducerIntervalResponse(List.of(), List.of());
        }

        Integer minInterval = allIntervals.stream()
                .mapToInt(ProducerInterval::interval)
                .min()
                .orElse(0);

        Integer maxInterval = allIntervals.stream()
                .mapToInt(ProducerInterval::interval)
                .max()
                .orElse(0);

        List<ProducerInterval> minIntervals = allIntervals.stream()
                .filter(interval -> interval.interval().equals(minInterval))
                .collect(Collectors.toList());

        List<ProducerInterval> maxIntervals = allIntervals.stream()
                .filter(interval -> interval.interval().equals(maxInterval))
                .collect(Collectors.toList());

        return new ProducerIntervalResponse(minIntervals, maxIntervals);
    }

    /**
     * Groups winning movies by producer.
     * Handles multiple producers per movie (comma and "and" separated).
     *
     * @param winningMovies List of winning movies
     * @return Map of producer names to their winning years
     */
    private Map<String, List<Integer>> groupWinsByProducer(List<Movie> winningMovies) {
        Map<String, List<Integer>> producerWins = new HashMap<>();

        for (Movie movie : winningMovies) {
            List<Producer> producers = Producer.parseMultiple(movie.producers());

            for (Producer producer : producers) {
                producerWins.computeIfAbsent(producer.name(), k -> new ArrayList<>())
                        .add(movie.year());
            }
        }

        producerWins.replaceAll((producer, years) -> {
            List<Integer> sortedYears = new ArrayList<>(years);
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
    private List<ProducerInterval> calculateIntervals(Map<String, List<Integer>> producerWins) {
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
