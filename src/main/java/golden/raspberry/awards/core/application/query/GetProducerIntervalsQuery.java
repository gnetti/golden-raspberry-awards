package golden.raspberry.awards.core.application.query;

/**
 * Query for getting producer intervals.
 * Part of CQRS pattern for read operations.
 *
 * <p>This query is part of the Application layer and represents
 * a request to get producer intervals following hexagonal architecture principles.
 *
 * <p>Uses Java 21 features: Records.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record GetProducerIntervalsQuery() {
    /**
     * Factory method to create a GetProducerIntervalsQuery.
     *
     * @return GetProducerIntervalsQuery instance
     */
    public static GetProducerIntervalsQuery of() {
        return new GetProducerIntervalsQuery();
    }
}

