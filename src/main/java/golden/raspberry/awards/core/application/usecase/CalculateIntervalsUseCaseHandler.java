package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.in.CalculateIntervalsUseCase;
import golden.raspberry.awards.core.domain.model.Movie;
import golden.raspberry.awards.core.domain.model.ProducerInterval;
import golden.raspberry.awards.core.domain.model.ProducerIntervalResponse;
import golden.raspberry.awards.core.domain.port.out.MovieRepositoryPort;
import golden.raspberry.awards.core.domain.service.ProducerIntervalCalculator;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Use Case for calculating producer intervals.
 * Orchestrates the calculation by delegating business logic to domain service.
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

        Map<String, List<Integer>> producerWins = ProducerIntervalCalculator.groupWinsByProducer(winningMovies);

        List<ProducerInterval> allIntervals = ProducerIntervalCalculator.calculateIntervals(producerWins);

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
}

