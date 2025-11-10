package golden.raspberry.awards.adapter.driving.rest.controller;

import golden.raspberry.awards.adapter.driving.rest.dto.DocumentInfoDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.MovieDTO;
import golden.raspberry.awards.core.application.port.in.CalculateIntervalsPort;
import golden.raspberry.awards.core.application.port.in.GetMoviePort;
import golden.raspberry.awards.core.application.port.out.ConverterDtoPort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

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
    private final GetMoviePort getMoviePort;

    /**
     * Constructor for dependency injection.
     *
     * @param calculateIntervalsPort Use case for calculating intervals
     * @param converterDtoPort Port for converting domain models to DTOs
     * @param getMoviePort Port for getting movies
     */
    public ProducerWebController(
            CalculateIntervalsPort calculateIntervalsPort,
            ConverterDtoPort converterDtoPort,
            GetMoviePort getMoviePort) {
        this.calculateIntervalsPort = Objects.requireNonNull(
                calculateIntervalsPort,
                "CalculateIntervalsPort cannot be null"
        );
        this.converterDtoPort = Objects.requireNonNull(
                converterDtoPort,
                "ConverterDtoPort cannot be null"
        );
        this.getMoviePort = Objects.requireNonNull(
                getMoviePort,
                "GetMoviePort cannot be null"
        );
    }

    @GetMapping("/")
    public String index(Model model) {
        var docInfo = DocumentInfoDTO.createDefault();
        
        model.addAttribute("title", "Home");
        model.addAttribute("apiVersion", docInfo.apiVersion());
        model.addAttribute("baseUrl", docInfo.baseUrl());
        model.addAttribute("description", docInfo.description());
        model.addAttribute("architecture", docInfo.architecture());
        model.addAttribute("maturityLevel", docInfo.maturityLevel());
        model.addAttribute("author", docInfo.author());
        
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

    /**
     * Handles GET request for movies list page with pagination, sorting and page size control.
     *
     * @param model Model to add attributes for Thymeleaf
     * @param page Page number (0-based, default: 0)
     * @param size Page size (default: 10)
     * @param sortBy Field to sort by (default: "id")
     * @param direction Sort direction: "asc" or "desc" (default: "asc")
     * @return View name "pages/movies"
     */
    @GetMapping("/movies")
    public String movies(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction) {
        
        var allMovies = getMoviePort.executeAll();
        var allMovieDTOs = allMovies.stream()
                .map(movie -> (MovieDTO) converterDtoPort.toDTO(movie))
                .collect(Collectors.toList());

        var sortedMovies = sortMovies(allMovieDTOs, sortBy, direction);
        
        var totalItems = sortedMovies.size();
        var totalPages = (int) Math.ceil((double) totalItems / size);
        
        if (page < 0) page = 0;
        if (page >= totalPages && totalPages > 0) page = totalPages - 1;
        
        var start = page * size;
        var end = Math.min(start + size, totalItems);
        var paginatedMovies = sortedMovies.subList(start, end);
        
        var pageNumbers = java.util.stream.IntStream.range(0, totalPages)
                .boxed()
                .collect(Collectors.toList());
        
        model.addAttribute("movies", paginatedMovies);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("pageNumbers", pageNumbers);
        model.addAttribute("title", "Movies");
        
        return "pages/movies";
    }

    private List<MovieDTO> sortMovies(List<MovieDTO> movies, String sortBy, String direction) {
        Comparator<MovieDTO> comparator = switch (sortBy.toLowerCase()) {
            case "year" -> Comparator.comparing(MovieDTO::year, Comparator.nullsLast(Comparator.naturalOrder()));
            case "title" -> Comparator.comparing(MovieDTO::title, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case "studios" -> Comparator.comparing(MovieDTO::studios, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case "producers" -> Comparator.comparing(MovieDTO::producers, Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER));
            case "winner" -> Comparator.comparing(MovieDTO::winner, Comparator.nullsLast(Comparator.naturalOrder()));
            default -> Comparator.comparing(MovieDTO::id, Comparator.nullsLast(Comparator.naturalOrder()));
        };
        
        if ("desc".equalsIgnoreCase(direction)) {
            comparator = comparator.reversed();
        }
        
        return movies.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }
}

