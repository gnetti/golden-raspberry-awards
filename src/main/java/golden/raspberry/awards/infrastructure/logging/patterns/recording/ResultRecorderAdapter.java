package golden.raspberry.awards.infrastructure.logging.patterns.recording;

import golden.raspberry.awards.core.application.port.out.ResultRecordingPort;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Output Adapter for recording results and outcomes of operations.
 * Implements ResultRecordingPort following hexagonal architecture principles.
 *
 * <p>This adapter is part of the 5 orchestration patterns selected for Golden Raspberry Awards project.
 * Focused on logging CREATE, UPDATE, DELETE operations.
 *
 * <p><strong>Hexagonal Architecture:</strong>
 * <ul>
 *   <li>Implements Port defined by Application layer</li>
 *   <li>Handles result recording (Infrastructure concern)</li>
 *   <li>No business logic - pure adapter</li>
 *   <li>Will use ListenerOperationUseCase when implementation is added</li>
 * </ul>
 *
 * <p><strong>Note:</strong> Implementation will be added when specific result recording logic is needed.
 * Currently, listening is handled automatically by ListenerAspect.
 *
 * <p>Uses Java 21 features elegantly and robustly.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class ResultRecorderAdapter implements ResultRecordingPort {

    /**
     * Default constructor.
     * ListenerOperationUseCase will be injected when implementation is added.
     */
    public ResultRecorderAdapter() {
    }

    /**
     * Records a result for logging.
     *
     * @param result Result to record
     */
    @Override
    public void record(Object result) {
        Objects.requireNonNull(result, "Result cannot be null");
    }

    /**
     * Logs an operation result.
     *
     * @param operation Operation name
     * @param result    Result to log
     */
    @Override
    public void log(String operation, Object result) {
        Objects.requireNonNull(operation, "Operation cannot be null");
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
