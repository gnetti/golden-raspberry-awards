package golden.raspberry.awards.core.application.service;

import java.util.Objects;

/**
 * Service for normalizing pagination parameters.
 * Pure business logic without framework dependencies.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class PaginationNormalizerService {

    private static final int MINIMUM_PAGE_NUMBER = 0;
    private static final int MINIMUM_PAGE_SIZE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAXIMUM_PAGE_SIZE = 100;

    private PaginationNormalizerService() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    /**
     * Normalizes page number to ensure it's >= 0.
     *
     * @param page Page number to normalize
     * @return Normalized page number (>= 0)
     */
    public static int normalizePage(int page) {
        return Math.max(MINIMUM_PAGE_NUMBER, page);
    }

    /**
     * Normalizes page size to ensure it's between MINIMUM_PAGE_SIZE and MAXIMUM_PAGE_SIZE.
     *
     * @param size Page size to normalize
     * @return Normalized page size (between 1 and 100)
     */
    public static int normalizeSize(int size) {
        if (size < MINIMUM_PAGE_SIZE) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(MAXIMUM_PAGE_SIZE, size);
    }

    /**
     * Normalizes sort direction string.
     *
     * @param direction Sort direction (asc or desc)
     * @return Normalized direction ("asc" or "desc"), defaults to "asc" if null or invalid
     */
    public static String normalizeDirection(String direction) {
        if (direction == null || direction.isBlank()) {
            return "asc";
        }
        return "desc".equalsIgnoreCase(direction) ? "desc" : "asc";
    }

    /**
     * Normalizes sort field using SortFieldMapper.
     *
     * @param sortBy Sort field name
     * @return Normalized sort field name
     * @throws NullPointerException if sortBy is null
     */
    public static String normalizeSortField(String sortBy) {
        Objects.requireNonNull(sortBy, "Sort field cannot be null");
        return SortFieldMapper.mapSortField(sortBy);
    }
}

