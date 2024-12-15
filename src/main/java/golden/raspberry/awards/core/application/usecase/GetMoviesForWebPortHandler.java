package golden.raspberry.awards.core.application.usecase;

import golden.raspberry.awards.core.application.port.in.GetMoviePort;
import golden.raspberry.awards.core.application.port.in.GetMoviesForWebPort;
import golden.raspberry.awards.core.application.port.out.ConverterDtoPort;
import golden.raspberry.awards.core.application.service.PaginationService;
import golden.raspberry.awards.core.application.service.SortFieldMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Use Case implementation for getting movies for web interface.
 * Encapsulates all pagination, sorting, filtering and presentation logic.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public record GetMoviesForWebPortHandler(
        GetMoviePort getMoviePort,
        ConverterDtoPort converterDtoPort) implements GetMoviesForWebPort {

    private static final String DEFAULT_FILTER_TYPE = "id";
    private static final String EMPTY_STRING = "";
    private static final String DESC_DIRECTION = "desc";

    public GetMoviesForWebPortHandler {
        Objects.requireNonNull(getMoviePort, "GetMoviePort cannot be null");
        Objects.requireNonNull(converterDtoPort, "ConverterDtoPort cannot be null");
    }

    @Override
    public MoviesWebResponse execute(MoviesWebRequest request) {
        Objects.requireNonNull(request, "Request cannot be null");

        var sortField = SortFieldMapper.mapSortField(request.sortBy());
        var sortDirection = mapSortDirection(request.direction());
        var sort = Sort.by(sortDirection, sortField);
        var pageable = PageRequest.of(request.page(), request.size(), sort);

        var moviePage = executeQuery(request, pageable);
        var movieDTOs = convertToDTOs(moviePage.getContent());
        var pageNumbers = PaginationService.calculatePageNumbers(
                moviePage.getNumber(),
                moviePage.getTotalPages()
        );

        return new MoviesWebResponse(
                movieDTOs,
                moviePage.getNumber(),
                moviePage.getTotalPages(),
                moviePage.getTotalElements(),
                request.size(),
                request.sortBy(),
                request.direction(),
                pageNumbers,
                determineFilterType(request.filterType()),
                extractFilterValue(request.filterValue())
        );
    }

    private org.springframework.data.domain.Page<?> executeQuery(
            MoviesWebRequest request,
            org.springframework.data.domain.Pageable pageable) {

        return hasActiveFilter(request)
                ? getMoviePort.executeAllWithFilter(
                        request.filterType(),
                        request.filterValue(),
                        pageable)
                : getMoviePort.executeAll(pageable);
    }

    private static boolean hasActiveFilter(MoviesWebRequest request) {
        return Optional.ofNullable(request.filterType())
                .filter(Predicate.not(String::isBlank))
                .isPresent()
                && Optional.ofNullable(request.filterValue())
                .filter(Predicate.not(String::isBlank))
                .isPresent();
    }

    private static Sort.Direction mapSortDirection(String direction) {
        var normalizedDirection = Optional.ofNullable(direction)
                .map(String::toLowerCase)
                .orElse("");
        
        return DESC_DIRECTION.equals(normalizedDirection)
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;
    }

    private static String determineFilterType(String filterType) {
        return Optional.ofNullable(filterType)
                .filter(Predicate.not(String::isBlank))
                .orElse(DEFAULT_FILTER_TYPE);
    }

    private static String extractFilterValue(String filterValue) {
        return Optional.ofNullable(filterValue)
                .orElse(EMPTY_STRING);
    }

    private java.util.List<Object> convertToDTOs(java.util.List<?> movies) {
        return movies.stream()
                .map(converterDtoPort::toDTO)
                .collect(Collectors.toList());
    }
}
