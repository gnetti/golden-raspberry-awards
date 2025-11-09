package golden.raspberry.awards.adapter.driving.rest;

import golden.raspberry.awards.adapter.driving.rest.dto.ProducerIntervalResponseDTO;
import golden.raspberry.awards.adapter.driving.rest.mapper.ProducerIntervalDTOMapper;
import golden.raspberry.awards.core.application.port.in.CalculateIntervalsUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

/**
 * REST Controller for Golden Raspberry Awards.
 *
 * <p>Implements Richardson Maturity Level 2:
 * <ul>
 *   <li><strong>Resources:</strong> /api/movies/producers/intervals</li>
 *   <li><strong>HTTP Verbs:</strong> GET</li>
 *   <li><strong>Status Codes:</strong> 200 OK, 400 Bad Request, 500 Internal Server Error</li>
 *   <li><strong>Structured Error Messages:</strong> via ApiExceptionHandler</li>
 * </ul>
 *
 * <p><strong>Hexagonal Architecture:</strong>
 * <ul>
 *   <li>Input Adapter (Primary) - receives HTTP requests</li>
 *   <li>Calls Use Case (Application layer)</li>
 *   <li>Converts Domain models to DTOs</li>
 *   <li>Returns JSON responses</li>
 * </ul>
 *
 * <p>Uses Java 21 features: Records, Streams, Method References.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/movies")
public class AwardsController {

    private final CalculateIntervalsUseCase calculateIntervalsUseCase;

    /**
     * Constructor for dependency injection.
     *
     * @param calculateIntervalsUseCase Use case for calculating intervals
     */
    public AwardsController(CalculateIntervalsUseCase calculateIntervalsUseCase) {
        this.calculateIntervalsUseCase = Objects.requireNonNull(
                calculateIntervalsUseCase,
                "CalculateIntervalsUseCase cannot be null"
        );
    }

    /**
     * Gets producer intervals (minimum and maximum).
     *
     * <p>Endpoint: GET /api/movies/producers/intervals
     *
     * <p>Response format (Richardson Level 2):
     * <pre>
     * {
     *   "min": [
     *     {
     *       "producer": "Producer 1",
     *       "interval": 1,
     *       "previousWin": 2008,
     *       "followingWin": 2009
     *     }
     *   ],
     *   "max": [
     *     {
     *       "producer": "Producer 2",
     *       "interval": 99,
     *       "previousWin": 1900,
     *       "followingWin": 1999
     *     }
     *   ]
     * }
     * </pre>
     *
     * @return ResponseEntity with ProducerIntervalResponseDTO
     * @apiNote Status Code: 200 OK (success) or 500 Internal Server Error (via exception handler)
     */
    @GetMapping("/producers/intervals")
    public ResponseEntity<ProducerIntervalResponseDTO> getIntervals() {
        var response = calculateIntervalsUseCase.execute();
        var dto = ProducerIntervalDTOMapper.toDTO(response);
        return ResponseEntity.ok(dto);
    }
}

