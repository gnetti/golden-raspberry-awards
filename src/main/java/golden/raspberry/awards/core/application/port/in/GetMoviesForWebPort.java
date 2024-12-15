package golden.raspberry.awards.core.application.port.in;

import java.util.List;

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

    record MoviesWebRequest(
            int page,
            int size,
            String sortBy,
            String direction,
            String filterType,
            String filterValue
    ) {
        public MoviesWebRequest {
            if (page < 0) {
                throw new IllegalArgumentException("Page number must be >= 0, but was: " + page);
            }
            if (size < 1) {
                throw new IllegalArgumentException("Page size must be >= 1, but was: " + size);
            }
            if (size > 100) {
                throw new IllegalArgumentException("Page size must be <= 100, but was: " + size);
            }
        }

        public static MoviesWebRequest normalize(int page, int size, String sortBy, String direction, String filterType, String filterValue) {
            var normalizedPage = Math.max(0, page);
            var normalizedSize = Math.max(1, Math.min(100, size));
            var normalizedSortBy = sortBy != null ? sortBy : "id";
            var normalizedDirection = direction != null ? direction : "asc";
            return new MoviesWebRequest(normalizedPage, normalizedSize, normalizedSortBy, normalizedDirection, filterType, filterValue);
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

