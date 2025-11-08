package golden.raspberry.awards.core.application.port.out;

/**
 * Output Port for archiving data and maintaining historical records.
 * Defined by Application layer, implemented by Output Adapter.
 *
 * <p>This port defines the contract for archiving before/after data
 * for UPDATE and DELETE operations following hexagonal architecture principles.
 *
 * <p>Uses Java 21 features: clean interfaces.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface DataArchivingPort {

    /**
     * Archives data for historical record keeping.
     *
     * @param data Data to archive
     */
    void archive(Object data);

    /**
     * Stores data for persistence.
     *
     * @param data Data to store
     */
    void store(Object data);

    /**
     * Preserves data for long-term storage.
     *
     * @param data Data to preserve
     */
    void preserve(Object data);
}

