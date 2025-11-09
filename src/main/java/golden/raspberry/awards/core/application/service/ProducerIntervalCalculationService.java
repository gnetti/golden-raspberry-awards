package golden.raspberry.awards.core.application.service;

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
 * Application service for orchestrating producer interval calculations.
 * Orchestrates the flow between repository, domain service, and response.
 *
 * <p>This service is part of the Application layer and orchestrates
 * the calculation of producer intervals following hexagonal architecture principles.
 *
 * <p><strong>Application Service Rules:</strong>
 * <ul>
 *   <li>Orchestrates flow between domain and ports</li>
 *   <li>NO business logic (delegates to domain service)</li>
 *   <li>Coordinates repository calls and domain service calls</li>
 *   <li>Depends only on ports and domain services</li>
 * </ul>
 *
 * <p><strong>Flow:</strong>
 * <pre>
 * Application Service (this)
 *     ↓
 * MovieRepositoryPort (Domain - Port OUT)
 *     ↓
 * ProducerIntervalCalculator (Domain - Service)
 *     ↓
 * ProducerIntervalResponse
 * </pre>
 *
 * <p>Uses Java 21 features: var, Objects.requireNonNull, Streams.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record ProducerIntervalCalculationService(MovieRepositoryPort repository,
                                                 ProducerIntervalCalculator calculator) {

    /**
     * Constructor for dependency injection.
     *
     * @param repository Movie repository port (output port)
     * @param calculator Domain service for interval calculations
     */
    public ProducerIntervalCalculationService(
            MovieRepositoryPort repository,
            ProducerIntervalCalculator calculator) {
        this.repository = Objects.requireNonNull(repository, "MovieRepositoryPort cannot be null");
        this.calculator = Objects.requireNonNull(calculator, "ProducerIntervalCalculator cannot be null");
    }

    /**
     * Orchestrates the calculation of producer intervals.
     * Coordinates repository calls and domain service calls.
     *
     * @return ProducerIntervalResponse with min and max intervals
     */
    public ProducerIntervalResponse calculate() {
        List<Movie> winningMovies = repository.findByWinnerTrue();

        if (winningMovies.isEmpty()) {
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
    }
}

