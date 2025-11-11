package golden.raspberry.awards.adapter.driving.rest.controller;

import golden.raspberry.awards.adapter.driving.rest.dto.DocumentInfoDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.MovieDTO;
import golden.raspberry.awards.core.application.port.in.CalculateIntervalsPort;
import golden.raspberry.awards.core.application.port.in.GetMoviePort;
import golden.raspberry.awards.core.application.port.out.ConverterDtoPort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
     * @param modal Whether to return only the content for modal (no layout)
     * @return View name "pages/intervals" or "fragments/intervals-modal" if modal
     */
    @GetMapping("/intervals")
    public String intervals(Model model, @RequestParam(required = false) Boolean modal) {
        var response = calculateIntervalsPort.execute();
        var dto = converterDtoPort.toDTO(response);
        model.addAttribute("intervals", dto);
        model.addAttribute("title", "Producer Intervals");
        
        if (Boolean.TRUE.equals(modal)) {
            return "fragments/intervals-modal";
        }
        
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
            @RequestParam(defaultValue = "asc") String direction,
            @RequestParam(required = false) String filterType,
            @RequestParam(required = false) String filterValue) {
        
        if (page < 0) page = 0;
        if (size < 1) size = 10;
        
        var sortDirection = "desc".equalsIgnoreCase(direction) 
                ? Sort.Direction.DESC 
                : Sort.Direction.ASC;
        
        var sort = Sort.by(sortDirection, mapSortField(sortBy));
        var pageable = PageRequest.of(page, size, sort);
        
        // Apply filter to ALL database records, not just current page
        var moviePage = (filterType != null && filterValue != null && !filterValue.isBlank())
                ? getMoviePort.executeAllWithFilter(filterType, filterValue, pageable)
                : getMoviePort.executeAll(pageable);
        
        var movieDTOs = moviePage.getContent().stream()
                .map(movie -> (MovieDTO) converterDtoPort.toDTO(movie))
                .collect(Collectors.toList());
        
        var currentPageNum = moviePage.getNumber();
        var totalPages = moviePage.getTotalPages();
        var pageNumbers = calculatePageNumbers(currentPageNum, totalPages);
        
        model.addAttribute("movies", movieDTOs);
        model.addAttribute("currentPage", currentPageNum);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", moviePage.getTotalElements());
        model.addAttribute("pageSize", size);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("direction", direction);
        model.addAttribute("pageNumbers", pageNumbers);
        // Preserve filterType from request or default to "id"
        String finalFilterType = (filterType != null && !filterType.isBlank()) ? filterType : "id";
        model.addAttribute("filterType", finalFilterType);
        model.addAttribute("filterValue", filterValue != null ? filterValue : "");
        model.addAttribute("title", "Movies");
        
        return "pages/movies";
    }

    private String mapSortField(String sortBy) {
        return switch (sortBy.toLowerCase()) {
            case "year" -> "year";
            case "title" -> "title";
            case "studios" -> "studios";
            case "producers" -> "producers";
            case "winner" -> "winner";
            default -> "id";
        };
    }

    /**
     * Calculates page numbers to display with ellipsis for large page counts.
     * Shows max 7 page numbers: first, last, current, and 2-3 on each side.
     *
     * @param currentPage Current page number (0-based)
     * @param totalPages Total number of pages
     * @return List of page numbers to display, with -1 representing ellipsis
     */
    private java.util.List<Integer> calculatePageNumbers(int currentPage, int totalPages) {
        var pages = new java.util.ArrayList<Integer>();
        
        if (totalPages <= 7) {
            // Show all pages if 7 or fewer
            for (int i = 0; i < totalPages; i++) {
                pages.add(i);
            }
        } else {
            // Always show first page
            pages.add(0);
            
            if (currentPage <= 3) {
                // Near the beginning: show 1,2,3,4,5
                for (int i = 1; i <= 5; i++) {
                    pages.add(i);
                }
                pages.add(-1); // ellipsis
                pages.add(totalPages - 1); // last page
            } else if (currentPage >= totalPages - 4) {
                // Near the end: show ... n-4, n-3, n-2, n-1, n
                pages.add(-1); // ellipsis
                for (int i = totalPages - 5; i < totalPages; i++) {
                    pages.add(i);
                }
            } else {
                // In the middle: show ... current-1, current, current+1 ...
                pages.add(-1); // ellipsis
                pages.add(currentPage - 1);
                pages.add(currentPage);
                pages.add(currentPage + 1);
                pages.add(-1); // ellipsis
                pages.add(totalPages - 1); // last page
            }
        }
        
        return pages;
    }
}

