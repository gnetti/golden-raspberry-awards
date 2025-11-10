package golden.raspberry.awards.adapter.driving.rest.controller;

import golden.raspberry.awards.adapter.driving.rest.dto.ProducerIntervalResponseDTO;
import golden.raspberry.awards.core.application.port.in.CalculateIntervalsPort;
import golden.raspberry.awards.core.application.port.out.ConverterDtoPort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * REST Controller for Golden Raspberry Awards.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/movies")
public class AwardsController {

    private final CalculateIntervalsPort calculateIntervalsPort;
    private final ConverterDtoPort converterDtoPort;

    /**
     * Constructor for dependency injection.
     *
     * @param calculateIntervalsPort Use case for calculating intervals
     * @param converterDtoPort Port for converting domain models to DTOs
     */
    public AwardsController(
            CalculateIntervalsPort calculateIntervalsPort,
            ConverterDtoPort converterDtoPort) {
        this.calculateIntervalsPort = Objects.requireNonNull(
                calculateIntervalsPort,
                "CalculateIntervalsPort cannot be null"
        );
        this.converterDtoPort = Objects.requireNonNull(
                converterDtoPort,
                "ConverterDtoPort cannot be null"
        );
    }

    /**
     * Gets producer intervals (minimum and maximum).
     *
     * @return ResponseEntity with ProducerIntervalResponseDTO
     */
    @GetMapping("/producers/intervals")
    public ResponseEntity<ProducerIntervalResponseDTO> getIntervals() {
        var response = calculateIntervalsPort.execute();
        var dto = (ProducerIntervalResponseDTO) converterDtoPort.toDTO(response);
        return ResponseEntity.ok(dto);
    }
}

