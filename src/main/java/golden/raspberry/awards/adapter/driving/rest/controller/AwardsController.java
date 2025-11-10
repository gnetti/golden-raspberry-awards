package golden.raspberry.awards.adapter.driving.rest.controller;

import golden.raspberry.awards.adapter.driving.rest.dto.ApiErrorDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.ProducerIntervalResponseDTO;
import golden.raspberry.awards.core.application.port.in.CalculateIntervalsPort;
import golden.raspberry.awards.core.application.port.out.ConverterDtoPort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Awards", description = "Producer intervals operations")
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
    @Operation(
            summary = "Get producer intervals",
            description = "Returns producers with the minimum and maximum intervals between consecutive awards"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved producer intervals",
                    content = @Content(schema = @Schema(implementation = ProducerIntervalResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))
            )
    })
    @GetMapping("/producers/intervals")
    public ResponseEntity<ProducerIntervalResponseDTO> getIntervals() {
        var response = calculateIntervalsPort.execute();
        var dto = (ProducerIntervalResponseDTO) converterDtoPort.toDTO(response);
        return ResponseEntity.ok(dto);
    }
}

