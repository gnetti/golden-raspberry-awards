package golden.raspberry.awards.core.application.port.out;

/**
 * Output Port for emitting information reactively to various targets.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface InformationEmissionPort {

    /**
     * Emits information with session ID.
     *
     * @param information Information to emit
     * @param sessionId Session identifier
     */
    void withSession(Object information, String sessionId);
}

