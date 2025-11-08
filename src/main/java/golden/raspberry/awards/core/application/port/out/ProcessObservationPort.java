package golden.raspberry.awards.core.application.port.out;

/**
 * Output Port for observing process execution and state changes.
 * Defined by Application layer, implemented by Output Adapter.
 *
 * <p>This port defines the contract for monitoring the flow of CREATE, UPDATE, DELETE operations
 * following hexagonal architecture principles.
 *
 * <p>Uses Java 21 features: clean interfaces.
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

    /**
     * Watches process state changes.
     *
     * @param process Process to watch
     */
    void watch(Object process);

    /**
     * Monitors process execution.
     *
     * @param process Process to monitor
     */
    void monitor(Object process);
}

