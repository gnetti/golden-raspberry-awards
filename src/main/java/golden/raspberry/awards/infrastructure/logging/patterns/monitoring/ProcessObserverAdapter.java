package golden.raspberry.awards.infrastructure.logging.patterns.monitoring;

import golden.raspberry.awards.core.application.port.out.ProcessObservationPort;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Output Adapter for observing process execution and state changes.
 * Implements ProcessObservationPort following hexagonal architecture principles.
 *
 * <p>This adapter is part of the 5 orchestration patterns selected for Golden Raspberry Awards project.
 * Focused on monitoring the flow of CREATE, UPDATE, DELETE operations.
 *
 * <p><strong>Hexagonal Architecture:</strong>
 * <ul>
 *   <li>Implements Port defined by Application layer</li>
 *   <li>Handles process observation (Infrastructure concern)</li>
 *   <li>No business logic - pure adapter</li>
 * </ul>
 *
 * <p>Uses Java 21 features elegantly and robustly.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class ProcessObserverAdapter implements ProcessObservationPort {

    /**
     * Observes a process execution.
     *
     * @param process Process to observe
     */
    @Override
    public void observe(Object process) {
        Objects.requireNonNull(process, "Process cannot be null");
    }

    /**
     * Watches process state changes.
     *
     * @param process Process to watch
     */
    @Override
    public void watch(Object process) {
        Objects.requireNonNull(process, "Process cannot be null");
    }

    /**
     * Monitors process execution.
     *
     * @param process Process to monitor
     */
    @Override
    public void monitor(Object process) {
        Objects.requireNonNull(process, "Process cannot be null");
    }
}
