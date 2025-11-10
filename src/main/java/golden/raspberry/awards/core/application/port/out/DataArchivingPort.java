package golden.raspberry.awards.core.application.port.out;

/**
 * Output Port for archiving data and maintaining historical records.
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
     * Preserves data for long-term storage.
     *
     * @param data Data to preserve
     */
    void preserve(Object data);
}

