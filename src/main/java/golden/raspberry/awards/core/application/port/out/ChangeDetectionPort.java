package golden.raspberry.awards.core.application.port.out;

/**
 * Output Port for detecting changes and variations.
 * Defined by Application layer, implemented by Output Adapter.
 *
 * <p>This port defines the contract for detecting changes between before/after states
 * in UPDATE operations following hexagonal architecture principles.
 *
 * <p>Uses Java 21 features: clean interfaces.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface ChangeDetectionPort {

    /**
     * Detects changes between two states.
     *
     * @param before State before change
     * @param after State after change
     * @return Detected changes
     */
    Object detect(Object before, Object after);

    /**
     * Identifies variations in data.
     *
     * @param data Data to analyze
     * @return Identified variations
     */
    Object identify(Object data);

    /**
     * Monitors changes over time.
     *
     * @param data Data to monitor
     */
    void monitor(Object data);
}

