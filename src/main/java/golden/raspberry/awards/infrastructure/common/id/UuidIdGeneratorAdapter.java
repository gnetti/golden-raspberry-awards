package golden.raspberry.awards.infrastructure.common.id;

import golden.raspberry.awards.core.application.port.out.IdGeneratorPort;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adapter implementing IdGeneratorPort using UUID.
 * Generates unique identifiers using Java's UUID class.
 *
 * <p>This adapter is part of the Infrastructure layer and implements
 * the IdGeneratorPort to provide UUID-based ID generation.
 *
 * <p><strong>Hexagonal Architecture:</strong>
 * <ul>
 *   <li>Driven Adapter (Secondary) - implements Output Port</li>
 *   <li>Part of Infrastructure layer</li>
 *   <li>Used by Use Cases via IdGeneratorPort interface</li>
 * </ul>
 *
 * <p>Uses Java 21 features: Records (in port interface).
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class UuidIdGeneratorAdapter implements IdGeneratorPort {

    /**
     * Generates a new unique ID using UUID.
     * Converts UUID to a Long by taking the most significant bits.
     *
     * @return A new unique identifier as Long
     */
    @Override
    public Long generateId() {
        var uuid = UUID.randomUUID();
        return Math.abs(uuid.getMostSignificantBits());
    }

    /**
     * Generates a new unique ID as a string using UUID.
     *
     * @return A new unique identifier as string
     */
    @Override
    public String generateIdAsString() {
        return UUID.randomUUID().toString();
    }
}

