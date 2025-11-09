package golden.raspberry.awards.core.application.port.out;

/**
 * Output Port for generating unique IDs.
 * Part of CQRS pattern - separates ID generation from ID management.
 *
 * <p>This port is part of the Application layer and defines the contract
 * for generating unique identifiers. It is separate from IdKeyManagerPort
 * which manages ID persistence in XML files.
 *
 * <p><strong>Hexagonal Architecture:</strong>
 * <ul>
 *   <li>Output Port (Secondary) - defined by Application</li>
 *   <li>Implemented by Output Adapters</li>
 *   <li>Used by Use Cases (Application layer)</li>
 * </ul>
 *
 * <p><strong>Difference from IdKeyManagerPort:</strong>
 * <ul>
 *   <li>IdGeneratorPort: Generates IDs (UUID, sequential, etc.)</li>
 *   <li>IdKeyManagerPort: Manages ID persistence in XML file</li>
 * </ul>
 *
 * <p>Uses Java 21 features: Records (in implementations).
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface IdGeneratorPort {

    /**
     * Generates a new unique ID.
     *
     * @return A new unique identifier
     */
    Long generateId();

    /**
     * Generates a new unique ID as a string.
     *
     * @return A new unique identifier as string
     */
    String generateIdAsString();
}

