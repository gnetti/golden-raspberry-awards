package golden.raspberry.awards.adapter.driving.rest.converter;

import golden.raspberry.awards.adapter.driving.rest.dto.MovieDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.ProducerIntervalDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.ProducerIntervalResponseDTO;
import golden.raspberry.awards.core.application.port.out.ConverterDtoPort;
import golden.raspberry.awards.core.domain.model.valueobject.ProducerIntervalResponse;
import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;
import golden.raspberry.awards.core.domain.model.valueobject.ProducerInterval;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * Adapter for converting Domain models to DTOs.
 * Implements ConverterDtoPort.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class ConverterDtoAdapter implements ConverterDtoPort {

    /**
     * Converts a domain model to DTO.
     *
     * @param domainModel Domain model to convert
     * @return Converted DTO
     * @throws NullPointerException if domainModel is null
     * @throws IllegalArgumentException if domain model type is not supported
     */
    @Override
    public Object toDTO(Object domainModel) {
        Objects.requireNonNull(domainModel, "Domain model cannot be null");
        
        return switch (domainModel) {
            case MovieWithId movieWithId -> convertMovieWithId(movieWithId);
            case ProducerIntervalResponse response -> convertProducerIntervalResponse(response);
            case ProducerInterval interval -> convertProducerInterval(interval);
            default -> throw new IllegalArgumentException(
                    "Unsupported domain model type: %s".formatted(domainModel.getClass().getName())
            );
        };
    }

    /**
     * Converts MovieWithId domain model to MovieDTO.
     *
     * @param movieWithId MovieWithId domain model
     * @return Converted MovieDTO
     */
    private MovieDTO convertMovieWithId(MovieWithId movieWithId) {
        return new MovieDTO(
                movieWithId.id(),
                movieWithId.year(),
                movieWithId.title(),
                movieWithId.studios(),
                movieWithId.producers(),
                movieWithId.winner()
        );
    }

    /**
     * Converts ProducerIntervalResponse domain model to ProducerIntervalResponseDTO.
     *
     * @param response ProducerIntervalResponse domain model
     * @return Converted ProducerIntervalResponseDTO
     */
    private ProducerIntervalResponseDTO convertProducerIntervalResponse(ProducerIntervalResponse response) {
        List<ProducerIntervalDTO> minDTOs = response.min().stream()
                .map(this::convertProducerInterval)
                .toList();

        List<ProducerIntervalDTO> maxDTOs = response.max().stream()
                .map(this::convertProducerInterval)
                .toList();

        return new ProducerIntervalResponseDTO(minDTOs, maxDTOs);
    }

    /**
     * Converts ProducerInterval value object to ProducerIntervalDTO.
     *
     * @param interval ProducerInterval value object
     * @return Converted ProducerIntervalDTO
     */
    private ProducerIntervalDTO convertProducerInterval(ProducerInterval interval) {
        return new ProducerIntervalDTO(
                interval.producer(),
                interval.interval(),
                interval.previousWin(),
                interval.followingWin()
        );
    }
}

