package golden.raspberry.awards.adapter.driving.rest.controller;

import golden.raspberry.awards.adapter.driving.rest.dto.ApiErrorDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.ProducerIntervalResponseDTO;
import golden.raspberry.awards.core.application.port.in.CalculateIntervalsPort;
import golden.raspberry.awards.core.application.port.out.ConverterDtoPort;
import golden.raspberry.awards.adapter.driving.rest.controller.constants.ApiIllustrationSetConstants;
import golden.raspberry.awards.adapter.driving.rest.controller.constants.AwardsControllerConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
                AwardsControllerConstants.ERROR_MESSAGE_CALCULATE_INTERVALS_PORT_CANNOT_BE_NULL
        );
        this.converterDtoPort = Objects.requireNonNull(
                converterDtoPort,
                AwardsControllerConstants.ERROR_MESSAGE_CONVERTER_DTO_PORT_CANNOT_BE_NULL
        );
    }

    /**
     * Gets producer intervals (minimum and maximum).
     *
     * @return ResponseEntity with ProducerIntervalResponseDTO
     */
    @Operation(
            summary = AwardsControllerConstants.OPERATION_SUMMARY_GET_PRODUCER_INTERVALS,
            description = AwardsControllerConstants.OPERATION_DESCRIPTION_GET_PRODUCER_INTERVALS
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = AwardsControllerConstants.HTTP_STATUS_CODE_OK_STRING,
                    description = AwardsControllerConstants.API_RESPONSE_DESCRIPTION_SUCCESSFULLY_RETRIEVED_INTERVALS,
                    content = @Content(
                            schema = @Schema(implementation = ProducerIntervalResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Success",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_PRODUCER_INTERVALS_SUCCESS
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = AwardsControllerConstants.HTTP_STATUS_CODE_INTERNAL_SERVER_ERROR_STRING,
                    description = AwardsControllerConstants.API_RESPONSE_DESCRIPTION_INTERNAL_SERVER_ERROR,
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Internal Server Error",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_ERROR_INTERNAL_SERVER
                            )
                    )
            )
    })
    @GetMapping("/producers/intervals")
    public ResponseEntity<ProducerIntervalResponseDTO> getIntervals() {
        var response = calculateIntervalsPort.execute();
        var dto = (ProducerIntervalResponseDTO) converterDtoPort.toDTO(response);
        return ResponseEntity.ok(dto);
    }
}

