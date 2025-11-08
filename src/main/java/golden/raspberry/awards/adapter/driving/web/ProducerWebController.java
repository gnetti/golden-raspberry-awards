package golden.raspberry.awards.adapter.driving.web;

import golden.raspberry.awards.core.application.port.in.CalculateIntervalsUseCase;
import golden.raspberry.awards.core.domain.model.ProducerIntervalResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

/**
 * Web Controller for Thymeleaf pages.
 * Input Adapter (Primary) - handles web UI requests.
 *
 * <p><strong>Hexagonal Architecture:</strong>
 * <ul>
 *   <li>Input Adapter (Primary) - receives web requests</li>
 *   <li>Calls Use Case (Application layer)</li>
 *   <li>Adds data to Model for Thymeleaf rendering</li>
 *   <li>Returns view names for Thymeleaf templates</li>
 * </ul>
 *
 * <p>Uses Java 21 features: Records, var, Objects.requireNonNull.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Controller
public class ProducerWebController {

    private final CalculateIntervalsUseCase calculateIntervalsUseCase;

    /**
     * Constructor for dependency injection.
     *
     * @param calculateIntervalsUseCase Use case for calculating intervals
     */
    public ProducerWebController(CalculateIntervalsUseCase calculateIntervalsUseCase) {
        this.calculateIntervalsUseCase = Objects.requireNonNull(
                calculateIntervalsUseCase,
                "CalculateIntervalsUseCase cannot be null"
        );
    }

    /**
     * Handles GET request for home page.
     *
     * @param model Model to add attributes for Thymeleaf
     * @return View name "pages/index"
     */
    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("title", "Home");
        return "pages/index";
    }

    /**
     * Handles GET request for intervals page.
     * Calls Use Case to get producer intervals and adds to model.
     *
     * @param model Model to add attributes for Thymeleaf
     * @return View name "pages/intervals"
     */
    @GetMapping("/intervals")
    public String intervals(Model model) {
        ProducerIntervalResponse response = calculateIntervalsUseCase.execute();
        model.addAttribute("intervals", response);
        model.addAttribute("title", "Producer Intervals");
        return "pages/intervals";
    }
}

