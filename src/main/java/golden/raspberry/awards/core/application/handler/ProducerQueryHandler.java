package golden.raspberry.awards.core.application.handler;

import golden.raspberry.awards.core.application.port.in.CalculateIntervalsUseCase;
import golden.raspberry.awards.core.application.query.GetProducerIntervalsQuery;
import golden.raspberry.awards.core.domain.model.ProducerIntervalResponse;

import java.util.Objects;

/**
 * Query handler for producer operations.
 * Handles queries following CQRS pattern.
 *
 * <p>This handler is part of the Application layer and orchestrates
 * query execution following hexagonal architecture principles.
 *
 * <p><strong>CQRS Pattern:</strong>
 * <ul>
 *   <li>Separates query side from command side</li>
 *   <li>Handles read operations (queries)</li>
 *   <li>Delegates to use cases for execution</li>
 * </ul>
 *
 * <p>Uses Java 21 features: Objects.requireNonNull.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public class ProducerQueryHandler {

    private final CalculateIntervalsUseCase calculateIntervalsUseCase;

    /**
     * Constructor for dependency injection.
     *
     * @param calculateIntervalsUseCase Use case for calculating intervals
     */
    public ProducerQueryHandler(CalculateIntervalsUseCase calculateIntervalsUseCase) {
        this.calculateIntervalsUseCase = Objects.requireNonNull(calculateIntervalsUseCase, "CalculateIntervalsUseCase cannot be null");
    }

    /**
     * Handles GetProducerIntervalsQuery.
     *
     * @param query GetProducerIntervalsQuery
     * @return ProducerIntervalResponse
     */
    public ProducerIntervalResponse handle(GetProducerIntervalsQuery query) {
        Objects.requireNonNull(query, "GetProducerIntervalsQuery cannot be null");
        return calculateIntervalsUseCase.execute();
    }
}

