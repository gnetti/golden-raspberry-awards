package golden.raspberry.awards.core.application.port.out;

/**
 * Output Port for detecting changes and variations.
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
}

