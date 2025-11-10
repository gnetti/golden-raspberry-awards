package golden.raspberry.awards.core.application.port.out;

/**
 * Output Port for observing process execution and state changes.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface ProcessObservationPort {

    /**
     * Observes a process execution.
     *
     * @param process Process to observe
     */
    void observe(Object process);
}

