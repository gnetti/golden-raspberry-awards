package golden.raspberry.awards.core.application.port.out;

/**
 * Output Port for recording results and outcomes of operations.
 * Defined by Application layer, implemented by Output Adapter.
 *
 * <p>This port defines the contract for recording operation results
 * following hexagonal architecture principles.
 *
 * <p>Uses Java 21 features: clean interfaces.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface ResultRecordingPort {

    /**
     * Records a result for logging.
     *
     * @param result Result to record
     */
    void record(Object result);

    /**
     * Logs an operation result.
     *
     * @param operation Operation name
     * @param result Result to log
     */
    void log(String operation, Object result);

    /**
     * Stores a result for persistence.
     *
     * @param result Result to store
     */
    void store(Object result);
}

