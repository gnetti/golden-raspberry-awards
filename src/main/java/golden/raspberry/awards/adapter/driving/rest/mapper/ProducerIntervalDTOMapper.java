package golden.raspberry.awards.adapter.driving.rest.mapper;

import golden.raspberry.awards.adapter.driving.rest.dto.ProducerIntervalDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.ProducerIntervalResponseDTO;
import golden.raspberry.awards.core.domain.model.ProducerInterval;
import golden.raspberry.awards.core.domain.model.ProducerIntervalResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper between ProducerIntervalResponse (Domain) and ProducerIntervalResponseDTO (Adapter).
 * Converts between domain layer and REST API layer.
 *
 * <p>This mapper is in the Adapter layer because it knows about DTOs.
 * It converts ProducerIntervalResponse to ProducerIntervalResponseDTO for REST API responses.
 *
 * <p>Uses Java 21 features: Streams, Method References.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class ProducerIntervalDTOMapper {

    private ProducerIntervalDTOMapper() {
    }

    /**
     * Converts ProducerIntervalResponse to ProducerIntervalResponseDTO.
     *
     * @param response Domain model response
     * @return DTO response
     */
    public static ProducerIntervalResponseDTO toDTO(ProducerIntervalResponse response) {
        List<ProducerIntervalDTO> minDTOs = response.min().stream()
                .map(ProducerIntervalDTOMapper::toDTO)
                .collect(Collectors.toList());

        List<ProducerIntervalDTO> maxDTOs = response.max().stream()
                .map(ProducerIntervalDTOMapper::toDTO)
                .collect(Collectors.toList());

        return new ProducerIntervalResponseDTO(minDTOs, maxDTOs);
    }

    /**
     * Converts ProducerInterval to ProducerIntervalDTO.
     *
     * @param interval Domain model interval
     * @return DTO interval
     */
    private static ProducerIntervalDTO toDTO(ProducerInterval interval) {
        return new ProducerIntervalDTO(
                interval.producer(),
                interval.interval(),
                interval.previousWin(),
                interval.followingWin()
        );
    }
}

