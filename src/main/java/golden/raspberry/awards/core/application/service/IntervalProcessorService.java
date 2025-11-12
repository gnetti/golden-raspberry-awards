package golden.raspberry.awards.core.application.service;

import golden.raspberry.awards.core.domain.model.aggregate.Movie;
import golden.raspberry.awards.core.domain.model.valueobject.ProducerInterval;
import golden.raspberry.awards.core.domain.model.valueobject.ProducerIntervalResponse;
import golden.raspberry.awards.core.application.port.out.MovieRepositoryPort;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Application service for orchestrating producer interval calculations.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record IntervalProcessorService(MovieRepositoryPort repository,
                                       golden.raspberry.awards.core.domain.service.IntervalProcessorService calculator) {

    /**
     * Constructor for dependency injection.
     *
     * @param repository Movie repository port
     * @param calculator Domain service for interval calculations
     */
    public IntervalProcessorService(
            MovieRepositoryPort repository,
            golden.raspberry.awards.core.domain.service.IntervalProcessorService calculator) {
        this.repository = Objects.requireNonNull(repository, "MovieRepositoryPort cannot be null");
        this.calculator = Objects.requireNonNull(calculator, "IntervalProcessorService cannot be null");
    }

    /**
     * Orchestrates the calculation of producer intervals.
     *
     * @return ProducerIntervalResponse with min and max intervals
     */
    public ProducerIntervalResponse calculate() {
        try {
            if (repository == null || calculator == null) {
                return new ProducerIntervalResponse(List.of(), List.of());
            }
            
            List<Movie> winningMovies = repository.findByWinnerTrue();
            
            if (winningMovies == null || winningMovies.isEmpty()) {
                return new ProducerIntervalResponse(List.of(), List.of());
            }

            Map<String, List<Integer>> producerWins = calculator.groupWinsByProducer(winningMovies);

            List<ProducerInterval> allIntervals = calculator.calculateIntervals(producerWins);

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
        } catch (Exception e) {
            return new ProducerIntervalResponse(List.of(), List.of());
        }
    }
}

