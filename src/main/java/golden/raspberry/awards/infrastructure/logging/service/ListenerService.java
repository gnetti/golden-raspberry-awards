package golden.raspberry.awards.infrastructure.logging.service;

import golden.raspberry.awards.core.application.port.out.ChangeDetectionPort;
import golden.raspberry.awards.core.application.port.out.DataArchivingPort;
import golden.raspberry.awards.core.application.port.out.InformationEmissionPort;
import golden.raspberry.awards.core.application.port.out.ProcessObservationPort;
import golden.raspberry.awards.core.application.port.out.ResultRecordingPort;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Main logging service based on LISTENER_ORCHESTRATION_PATTERNS.md.
 * Provides access to 5 selected orchestration patterns focused on the project needs.
 *
 * <p><strong>Important:</strong> Custom service - NO external logging dependencies.
 *
 * <p><strong>Hexagonal Architecture:</strong>
 * <ul>
 *   <li>Service in Infrastructure layer</li>
 *   <li>Uses Ports defined by Application layer (dependency inversion)</li>
 *   <li>Adapters implement Ports and are injected via Spring</li>
 *   <li>No direct access to Adapters - only through Ports</li>
 * </ul>
 *
 * <p><strong>Selected Patterns (5 total):</strong>
 * <ul>
 *   <li><strong>ResultRecordingPort</strong> - Records results and outcomes of operations</li>
 *   <li><strong>DataArchivingPort</strong> - Archives data and maintains historical records</li>
 *   <li><strong>ChangeDetectionPort</strong> - Detects changes and variations (for UPDATE operations)</li>
 *   <li><strong>ProcessObservationPort</strong> - Observes process execution and state changes</li>
 *   <li><strong>InformationEmissionPort</strong> - Emits information reactively with session correlation</li>
 * </ul>
 *
 * <p>These 5 patterns cover all needs for logging CREATE, UPDATE, DELETE operations
 * with before/after data and session-specific tracking.
 *
 * <p>Uses Java 21 features: Records, Pattern Matching, clean code.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Service
public class ListenerService {

   //todo These methods will be implemented in the near future.
    private final ResultRecordingPort resultRecordingPort;
    private final DataArchivingPort dataArchivingPort;
    private final ChangeDetectionPort changeDetectionPort;
    private final ProcessObservationPort processObservationPort;
    private final InformationEmissionPort informationEmissionPort;

    /**
     * Constructor for dependency injection.
     * All Ports are automatically injected by Spring.
     *
     * @param resultRecordingPort Port for recording results
     * @param dataArchivingPort Port for archiving data
     * @param changeDetectionPort Port for detecting changes
     * @param processObservationPort Port for observing processes
     * @param informationEmissionPort Port for emitting information
     */
    public ListenerService(
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

}

