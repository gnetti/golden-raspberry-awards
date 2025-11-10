package golden.raspberry.awards.infrastructure.adapter.driven.listener.observation;

import golden.raspberry.awards.core.application.port.out.ChangeDetectionPort;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Output Adapter for detecting changes and variations.
 * Implements ChangeDetectionPort.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class ChangeDetectorAdapter implements ChangeDetectionPort {

    /**
     * Detects changes between two states.
     *
     * @param before State before change
     * @param after  State after change
     * @return Detected changes
     */
    @Override
    public Object detect(Object before, Object after) {
        Objects.requireNonNull(before, "Before state cannot be null");
        Objects.requireNonNull(after, "After state cannot be null");
        return null;
    }
}

