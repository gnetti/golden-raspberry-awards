package golden.raspberry.awards.infrastructure.adapter.driven.listener.recording;

import golden.raspberry.awards.core.application.port.out.DataArchivingPort;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Output Adapter for archiving data and maintaining historical records.
 * Implements DataArchivingPort.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class DataArchivistAdapter implements DataArchivingPort {

    /**
     * Archives data for historical record keeping.
     *
     * @param data Data to archive
     */
    @Override
    public void archive(Object data) {
        Objects.requireNonNull(data, "Data cannot be null");
    }

    /**
     * Preserves data for long-term storage.
     *
     * @param data Data to preserve
     */
    @Override
    public void preserve(Object data) {
        Objects.requireNonNull(data, "Data cannot be null");
    }
}

