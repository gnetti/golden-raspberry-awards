package golden.raspberry.awards.adapter.driving.rest.controller;

import golden.raspberry.awards.core.application.port.in.CalculateIntervalsPort;
import golden.raspberry.awards.core.application.port.out.ConverterDtoPort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

/**
 * Web Controller for Thymeleaf pages.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Controller
public class ProducerWebController {

    private final CalculateIntervalsPort calculateIntervalsPort;
    private final ConverterDtoPort converterDtoPort;

    /**
     * Constructor for dependency injection.
     *
     * @param calculateIntervalsPort Use case for calculating intervals
     * @param converterDtoPort Port for converting domain models to DTOs
     */
    public ProducerWebController(
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
     *
     * @param model Model to add attributes for Thymeleaf
     * @return View name "pages/intervals"
     */
    @GetMapping("/intervals")
    public String intervals(Model model) {
        var response = calculateIntervalsPort.execute();
        var dto = converterDtoPort.toDTO(response);
        model.addAttribute("intervals", dto);
        model.addAttribute("title", "Producer Intervals");
        return "pages/intervals";
    }
}

