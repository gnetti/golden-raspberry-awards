package golden.raspberry.awards.infrastructure.adapter.driven.listener.recording;

import golden.raspberry.awards.core.application.port.out.ResultRecordingPort;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Output Adapter for recording results and outcomes of operations.
 * Implements ResultRecordingPort.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class ResultRecorderAdapter implements ResultRecordingPort {

    /**
     * Records a result for listener.
     *
     * @param result Result to record
     */
    @Override
    public void record(Object result) {
        Objects.requireNonNull(result, "Result cannot be null");
    }

    /**
     * Stores a result for persistence.
     *
     * @param result Result to store
     */
    @Override
    public void store(Object result) {
        Objects.requireNonNull(result, "Result cannot be null");
    }
}

