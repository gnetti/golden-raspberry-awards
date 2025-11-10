package golden.raspberry.awards.infrastructure.adapter.driven.listener.transmission;

import golden.raspberry.awards.core.application.port.out.InformationEmissionPort;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Output Adapter for emitting information reactively to various targets.
 * Implements InformationEmissionPort.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class InformationEmitterAdapter implements InformationEmissionPort {

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

