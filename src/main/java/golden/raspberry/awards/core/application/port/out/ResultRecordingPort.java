package golden.raspberry.awards.core.application.port.out;

/**
 * Output Port for recording results and outcomes of operations.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface ResultRecordingPort {

    /**
     * Records a result for listener.
     *
     * @param result Result to record
     */
    void record(Object result);

    /**
     * Stores a result for persistence.
     *
     * @param result Result to store
     */
    void store(Object result);
}

