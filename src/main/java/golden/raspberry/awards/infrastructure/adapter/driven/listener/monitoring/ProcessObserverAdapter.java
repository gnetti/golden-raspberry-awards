package golden.raspberry.awards.infrastructure.adapter.driven.listener.monitoring;

import golden.raspberry.awards.core.application.port.out.ProcessObservationPort;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Output Adapter for observing process execution and state changes.
 * Implements ProcessObservationPort.
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
}

