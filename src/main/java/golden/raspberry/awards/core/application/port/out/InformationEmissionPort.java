package golden.raspberry.awards.core.application.port.out;

/**
 * Output Port for emitting information reactively to various targets.
 * Defined by Application layer, implemented by Output Adapter.
 *
 * <p>This port defines the contract for emitting logs with sessionId and correlation
 * for CREATE, UPDATE, DELETE operations following hexagonal architecture principles.
 *
 * <p>Uses Java 21 features: clean interfaces.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface InformationEmissionPort {

    /**
     * Emits information to targets.
     *
     * @param information Information to emit
     */
    void emit(Object information);

    /**
     * Emits information with correlation ID.
     *
     * @param information Information to emit
     * @param correlationId Correlation identifier
     */
    void withCorrelation(Object information, String correlationId);

    /**
     * Emits information with session ID.
     *
     * @param information Information to emit
     * @param sessionId Session identifier
     */
    void withSession(Object information, String sessionId);
}

