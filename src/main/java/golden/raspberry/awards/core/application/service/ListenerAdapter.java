package golden.raspberry.awards.core.application.service;

import golden.raspberry.awards.core.application.port.out.ChangeDetectionPort;
import golden.raspberry.awards.core.application.port.out.DataArchivingPort;
import golden.raspberry.awards.core.application.port.out.InformationEmissionPort;
import golden.raspberry.awards.core.application.port.out.ProcessObservationPort;
import golden.raspberry.awards.core.application.port.out.ResultRecordingPort;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * Main listener service providing access to orchestration patterns.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class ListenerAdapter {

    private final ResultRecordingPort resultRecordingPort;
    private final DataArchivingPort dataArchivingPort;
    private final ChangeDetectionPort changeDetectionPort;
    private final ProcessObservationPort processObservationPort;
    private final InformationEmissionPort informationEmissionPort;

    /**
     * Constructor for dependency injection.
     *
     * @param resultRecordingPort Port for recording results
     * @param dataArchivingPort Port for archiving data
     * @param changeDetectionPort Port for detecting changes
     * @param processObservationPort Port for observing processes
     * @param informationEmissionPort Port for emitting information
     */
    public ListenerAdapter(
            ResultRecordingPort resultRecordingPort,
            DataArchivingPort dataArchivingPort,
            ChangeDetectionPort changeDetectionPort,
            ProcessObservationPort processObservationPort,
            InformationEmissionPort informationEmissionPort) {
        this.resultRecordingPort = Objects.requireNonNull(resultRecordingPort, "ResultRecordingPort cannot be null");
        this.dataArchivingPort = Objects.requireNonNull(dataArchivingPort, "DataArchivingPort cannot be null");
        this.changeDetectionPort = Objects.requireNonNull(changeDetectionPort, "ChangeDetectionPort cannot be null");
        this.processObservationPort = Objects.requireNonNull(processObservationPort, "ProcessObservationPort cannot be null");
        this.informationEmissionPort = Objects.requireNonNull(informationEmissionPort, "InformationEmissionPort cannot be null");
    }

    /**
     * Records a result.
     *
     * @param result Result to record
     */
    public void recordResult(Object result) {
        if (result != null) {
            resultRecordingPort.record(result);
        }
    }

    /**
     * Archives data.
     *
     * @param data Data to archive
     */
    public void archiveData(Object data) {
        if (data != null) {
            dataArchivingPort.archive(data);
        }
    }

    /**
     * Preserves data.
     *
     * @param data Data to preserve
     */
    public void preserveData(Object data) {
        if (data != null) {
            dataArchivingPort.preserve(data);
        }
    }

    /**
     * Detects changes between before and after states.
     *
     * @param before State before change
     * @param after State after change
     * @return Detected changes
     */
    public Object detectChanges(Object before, Object after) {
        if (before != null && after != null) {
            return changeDetectionPort.detect(before, after);
        }
        return null;
    }

    /**
     * Observes a process execution.
     *
     * @param process Process to observe
     */
    public void observeProcess(Object process) {
        if (process != null) {
            processObservationPort.observe(process);
        }
    }

    /**
     * Emits information with session ID.
     *
     * @param information Information to emit
     * @param sessionId Session identifier
     */
    public void emitWithSession(Object information, String sessionId) {
        if (information != null && sessionId != null && !sessionId.isBlank()) {
            informationEmissionPort.withSession(information, sessionId);
        }
    }

    /**
     * Stores data.
     *
     * @param data Data to store
     */
    public void storeData(Object data) {
        if (data != null) {
            resultRecordingPort.store(data);
        }
    }
}

