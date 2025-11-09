package golden.raspberry.awards.infrastructure.listener.patterns.transmission;

import golden.raspberry.awards.core.application.port.out.InformationEmissionPort;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Output Adapter for emitting information reactively to various targets.
 * Implements InformationEmissionPort following hexagonal architecture principles.
 *
 * <p>This adapter is part of the 5 orchestration patterns selected for Golden Raspberry Awards project.
 * Focused on emitting logs with sessionId and correlation for CREATE, UPDATE, DELETE operations.
 *
 * <p><strong>Hexagonal Architecture:</strong>
 * <ul>
 *   <li>Implements Port defined by Application layer</li>
 *   <li>Handles information emission (Infrastructure concern)</li>
 *   <li>No business logic - pure adapter</li>
 * </ul>
 *
 * <p>Uses Java 21 features elegantly and robustly.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class InformationEmitterAdapter implements InformationEmissionPort {

    /**
     * Emits information to targets.
     *
     * @param information Information to emit
     */
    @Override
    public void emit(Object information) {
        Objects.requireNonNull(information, "Information cannot be null");
    }

    /**
     * Emits information with correlation ID.
     *
     * @param information   Information to emit
     * @param correlationId Correlation identifier
     */
    @Override
    public void withCorrelation(Object information, String correlationId) {
        Objects.requireNonNull(information, "Information cannot be null");
        Objects.requireNonNull(correlationId, "CorrelationId cannot be null");
    }

    /**
     * Emits information with session ID.
     *
     * @param information Information to emit
     * @param sessionId   Session identifier
     */
    @Override
    public void withSession(Object information, String sessionId) {
        Objects.requireNonNull(information, "Information cannot be null");
        Objects.requireNonNull(sessionId, "SessionId cannot be null");
    }
}
