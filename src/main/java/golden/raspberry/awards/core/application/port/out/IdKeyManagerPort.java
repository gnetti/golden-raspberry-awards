package golden.raspberry.awards.core.application.port.out;

import java.util.Optional;

/**
 * Output Port for managing ID keys from XML file.
 *
 * <p>This port is part of the Application layer and defines the contract
 * for managing the last used ID, ensuring data integrity by preventing
 * ID reuse.
 *
 * <p><strong>Key Rules:</strong>
 * <ul>
 *   <li>IDs are NEVER reused (even after DELETE)</li>
 *   <li>lastId is always the maximum ID ever used</li>
 *   <li>On CREATE, lastId is incremented and XML is updated</li>
 *   <li>On DELETE, XML is NOT modified</li>
 *   <li>On startup, lastId is synchronized with MAX(id) from database</li>
 * </ul>
 *
 * <p>Uses Java 21 features: Optional, Records (in implementations).
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
     * If XML doesn't exist, creates it with the provided value.
     *
     * @param lastId The new lastId value to set
     * @throws IllegalStateException if XML file cannot be written
     */
    void updateLastId(Long lastId);

    /**
     * Synchronizes lastId with the maximum ID from database.
     * Compares MAX(id) from database with lastId from XML:
     * - If MAX(id) > lastId → uses MAX(id)
     * - If MAX(id) < lastId → uses lastId
     * - Updates XML with the greater value
     *
     * <p>This method is called on application startup after database is populated.
     *
     * @param maxIdFromDatabase Maximum ID found in database
     * @return The synchronized lastId value (the greater between XML and database)
     */
    Long synchronizeWithDatabase(Long maxIdFromDatabase);

    /**
     * Gets the next available ID for CREATE operations.
     * Increments lastId by 1 and updates XML.
     *
     * @return The next available ID
     * @throws IllegalStateException if lastId cannot be read or updated
     */
    Long getNextId();
}

