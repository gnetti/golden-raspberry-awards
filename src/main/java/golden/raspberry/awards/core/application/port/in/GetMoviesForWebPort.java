package golden.raspberry.awards.core.application.port.in;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Input Port for getting movies for web interface.
 * Encapsulates pagination, sorting, filtering and presentation logic.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public interface GetMoviesForWebPort {

    /**
     * Gets movies with pagination, sorting and filtering for web interface.
     *
     * @param request Request parameters for movies query
     * @return Response with movies and pagination metadata
     */
    MoviesWebResponse execute(MoviesWebRequest request);

    /**
     * Request for movies query with pagination, sorting and filtering.
     *
     * @param page Page number (0-based)
     * @param size Page size
     * @param sortBy Sort field name
     * @param direction Sort direction (asc/desc)
     * @param filterType Type of filter
     * @param filterValue Filter value
     */
    record MoviesWebRequest(
            int page,
            int size,
            String sortBy,
            String direction,
            String filterType,
            String filterValue
    ) {
        private static final int MIN_PAGE = 0;
        private static final int MIN_SIZE = 1;
        private static final int MAX_SIZE = 100;
        private static final String DEFAULT_SORT_BY = "id";
        private static final String DEFAULT_DIRECTION = "asc";

        public MoviesWebRequest {
            validatePage(page);
            validateSize(size);
        }

        private static void validatePage(int page) {
            if (page < MIN_PAGE) {
                throw new IllegalArgumentException(
                        "Page number must be >= %d, but was: %d".formatted(MIN_PAGE, page)
                );
            }
        }

        private static void validateSize(int size) {
            if (size < MIN_SIZE) {
                throw new IllegalArgumentException(
                        "Page size must be >= %d, but was: %d".formatted(MIN_SIZE, size)
                );
            }
            if (size > MAX_SIZE) {
                throw new IllegalArgumentException(
                        "Page size must be <= %d, but was: %d".formatted(MAX_SIZE, size)
                );
            }
        }

        /**
         * Normalizes request parameters with default values.
         *
         * @param page Page number
         * @param size Page size
         * @param sortBy Sort field
         * @param direction Sort direction
         * @param filterType Filter type
         * @param filterValue Filter value
         * @return Normalized request
         */
        public static MoviesWebRequest normalize(
                int page,
                int size,
                String sortBy,
                String direction,
                String filterType,
                String filterValue) {

            var normalizedPage = Math.max(MIN_PAGE, page);
            var normalizedSize = Math.max(MIN_SIZE, Math.min(MAX_SIZE, size));
            var normalizedSortBy = Optional.ofNullable(sortBy)
                    .filter(Predicate.not(String::isBlank))
                    .orElse(DEFAULT_SORT_BY);
            var normalizedDirection = Optional.ofNullable(direction)
                    .filter(Predicate.not(String::isBlank))
                    .orElse(DEFAULT_DIRECTION);

            return new MoviesWebRequest(
                    normalizedPage,
                    normalizedSize,
                    normalizedSortBy,
                    normalizedDirection,
                    filterType,
                    filterValue
            );
        }
    }

    /**
     * Response with movies and pagination metadata.
     *
     * @param movies List of movie DTOs
     * @param currentPage Current page number (0-based)
     * @param totalPages Total number of pages
     * @param totalItems Total number of items
     * @param pageSize Current page size
     * @param sortBy Current sort field
     * @param direction Current sort direction
     * @param pageNumbers List of page numbers to display (with -1 for ellipsis)
     * @param filterType Current filter type
     * @param filterValue Current filter value
     */
    record MoviesWebResponse(
            List<Object> movies,
            int currentPage,
            int totalPages,
            long totalItems,
            int pageSize,
            String sortBy,
            String direction,
            List<Integer> pageNumbers,
            String filterType,
            String filterValue
    ) {}
}
