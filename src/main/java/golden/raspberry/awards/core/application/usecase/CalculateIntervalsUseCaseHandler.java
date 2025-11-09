package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.in.CalculateIntervalsUseCase;
import golden.raspberry.awards.core.domain.model.Movie;
import golden.raspberry.awards.core.domain.model.Producer;
import golden.raspberry.awards.core.domain.model.ProducerInterval;
import golden.raspberry.awards.core.domain.model.ProducerIntervalResponse;
import golden.raspberry.awards.core.domain.port.out.MovieRepositoryPort;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Use Case for calculating producer intervals.
 * Implements the business logic for calculating intervals directly in the use case.
 *
 * <p>This use case is part of the Application layer and orchestrates
 * the calculation of producer intervals following hexagonal architecture principles.
 *
 * <p><strong>Flow:</strong>
 * <pre>
 * Adapter IN (Controller)
 *     ↓
 * CalculateIntervalsUseCase (this - Application)
 *     ↓
 * MovieRepositoryPort (Domain - Port OUT)
 * </pre>
 *
 * <p>Uses Java 21 features: var, Objects.requireNonNull, Streams, Pattern Matching.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record CalculateIntervalsUseCaseHandler(
        MovieRepositoryPort repository) implements CalculateIntervalsUseCase {

    /**
     * Constructor for dependency injection.
     *
     * @param repository Movie repository port (output port)
     */
    public CalculateIntervalsUseCaseHandler(MovieRepositoryPort repository) {
        this.repository = Objects.requireNonNull(repository, "MovieRepositoryPort cannot be null");
    }

    @Override
    public ProducerIntervalResponse execute() {
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
     * Removes duplicate years for the same producer (when a producer wins multiple times in the same year).
     *
     * @param winningMovies List of winning movies
     * @return Map of producer names to their unique winning years (sorted)
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

