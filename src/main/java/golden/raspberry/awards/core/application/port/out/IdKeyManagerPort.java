package golden.raspberry.awards.core.application.port.out;

import java.util.Optional;

/**
 * Output Port for managing ID keys from XML file.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface IdKeyManagerPort {

    /**
     * Gets the current lastId from XML file.
     *
     * @return Optional containing the lastId, or empty if XML doesn't exist or is invalid
     */
    Optional<Long> getLastId();

    /**
     * Updates the lastId in XML file.
     *
     * @param lastId The new lastId value to set
     * @throws IllegalStateException if XML file cannot be written
     */
    void updateLastId(Long lastId);

    /**
     * Synchronizes lastId with the maximum ID from database.
     *
     * @param maxIdFromDatabase Maximum ID found in database
     */
    void synchronizeWithDatabase(Long maxIdFromDatabase);

    /**
     * Resets lastId to a specific value.
     *
     * @param resetValue The value to reset lastId to
     * @throws NullPointerException     if resetValue is null
     * @throws IllegalArgumentException if resetValue is negative
     * @throws IllegalStateException    if XML cannot be updated
     */
    void resetLastId(Long resetValue);

    /**
     * Gets the next available ID for CREATE operations.
     *
     * @return The next available ID
     * @throws IllegalStateException if lastId cannot be read or updated
     */
    Long getNextId();
}

