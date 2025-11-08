package golden.raspberry.awards.infrastructure.logging.service;

import golden.raspberry.awards.infrastructure.logging.patterns.monitoring.ProcessObserver;
import golden.raspberry.awards.infrastructure.logging.patterns.observation.ChangeDetector;
import golden.raspberry.awards.infrastructure.logging.patterns.recording.DataArchivist;
import golden.raspberry.awards.infrastructure.logging.patterns.recording.ResultRecorder;
import golden.raspberry.awards.infrastructure.logging.patterns.transmission.InformationEmitter;

/**
 * Main logging service based on LISTENER_ORCHESTRATION_PATTERNS.md.
 * Provides access to 5 selected orchestration patterns focused on the project needs.
 *
 * <p><strong>Important:</strong> Custom service - NO external logging dependencies.
 *
 * <p><strong>Selected Patterns (5 total):</strong>
 * <ul>
 *   <li><strong>ResultRecorder</strong> - Records results and outcomes of operations</li>
 *   <li><strong>DataArchivist</strong> - Archives data and maintains historical records</li>
 *   <li><strong>ChangeDetector</strong> - Detects changes and variations (for UPDATE operations)</li>
 *   <li><strong>ProcessObserver</strong> - Observes process execution and state changes</li>
 *   <li><strong>InformationEmitter</strong> - Emits information reactively with session correlation</li>
 * </ul>
 *
 * <p>These 5 patterns cover all needs for logging CREATE, UPDATE, DELETE operations
 * with before/after data and session-specific tracking.
 *
 * <p>Uses Java 21 features: Records, Pattern Matching, clean code.
 *
 * @author Golden Raspberry Awards Team
 * @since 1.0.0
 */
public final class ListenerService {

    /**
     * Records results and outcomes of operations.
     * Used for logging CREATE, UPDATE, DELETE operation results.
     */
    public static final ResultRecorder resultRecorder = new ResultRecorder();

    /**
     * Archives data and maintains historical records.
     * Used for storing before/after data in UPDATE and DELETE operations.
     */
    public static final DataArchivist dataArchivist = new DataArchivist();

    /**
     * Detects changes and variations.
     * Used for detecting changes between before/after states in UPDATE operations.
     */
    public static final ChangeDetector changeDetector = new ChangeDetector();

    /**
     * Observes process execution and state changes.
     * Used for monitoring the flow of CREATE, UPDATE, DELETE operations.
     */
    public static final ProcessObserver processObserver = new ProcessObserver();

    /**
     * Emits information reactively to various targets.
     * Used for emitting logs with sessionId and correlation.
     */
    public static final InformationEmitter informationEmitter = new InformationEmitter();

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class providing static access to orchestration patterns.
     */
    private ListenerService() {
        throw new UnsupportedOperationException("ListenerService is a utility class and cannot be instantiated");
    }
}

