package golden.raspberry.awards.core.application.port.out;

/**
 * Output Port for converting Domain models to DTOs.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface ConverterDtoPort {

    /**
     * Converts a domain model to its DTO representation.
     *
     * @param domainModel Domain model object
     * @return DTO object representation
     */
    Object toDTO(Object domainModel);
}

