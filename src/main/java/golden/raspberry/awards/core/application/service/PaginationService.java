package golden.raspberry.awards.core.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Service for pagination calculations.
 * Pure business logic without framework dependencies.
 * <p>
 * Uses Java 21 features: pattern matching, streams, and functional programming.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class PaginationService {

    private static final int MAX_VISIBLE_PAGES = 7;
    private static final int ELLIPSIS_MARKER = -1;
    private static final int EARLY_PAGE_THRESHOLD = 3;
    private static final int LATE_PAGE_THRESHOLD = 4;
    private static final int PAGES_AROUND_CURRENT = 5;
    private static final int MIN_PAGE = 0;
    private static final int MIN_TOTAL_PAGES = 1;

    private PaginationService() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    /**
     * Calculates page numbers to display with ellipsis for large page counts.
     * Shows max 7-page numbers: first, last, current, and 2-3 on each side.
     *
     * @param currentPage Current page number (0-based)
     * @param totalPages  Total number of pages
     * @return List of page numbers to display, with -1 representing ellipsis
     * @throws IllegalArgumentException if currentPage < 0 or totalPages < 1
     */
    public static List<Integer> calculatePageNumbers(int currentPage, int totalPages) {
        validateInputs(currentPage, totalPages);

        return totalPages <= MAX_VISIBLE_PAGES
                ? generateAllPages(totalPages)
                : generatePaginationWithEllipsis(currentPage, totalPages);
    }

    private static void validateInputs(int currentPage, int totalPages) {
        if (currentPage < MIN_PAGE) {
            throw new IllegalArgumentException(
                    "Current page must be >= %d, but was: %d".formatted(MIN_PAGE, currentPage)
            );
        }
        if (totalPages < MIN_TOTAL_PAGES) {
            throw new IllegalArgumentException(
                    "Total pages must be >= %d, but was: %d".formatted(MIN_TOTAL_PAGES, totalPages)
            );
        }
        if (currentPage >= totalPages) {
            throw new IllegalArgumentException(
                    "Current page (%d) must be < total pages (%d)".formatted(currentPage, totalPages)
            );
        }
    }

    private static List<Integer> generateAllPages(int totalPages) {
        return IntStream.range(0, totalPages)
                .boxed()
                .toList();
    }

    private static List<Integer> generatePaginationWithEllipsis(int currentPage, int totalPages) {
        var pages = new ArrayList<Integer>();
        pages.add(MIN_PAGE);

        var paginationStrategy = determinePaginationStrategy(currentPage, totalPages);
        pages.addAll(buildPaginationSequence(paginationStrategy, currentPage, totalPages));

        return pages;
    }

    private static PaginationStrategy determinePaginationStrategy(int currentPage, int totalPages) {
        if (currentPage <= EARLY_PAGE_THRESHOLD) {
            return PaginationStrategy.EARLY;
        }
        if (currentPage >= totalPages - LATE_PAGE_THRESHOLD) {
            return PaginationStrategy.LATE;
        }
        return PaginationStrategy.MIDDLE;
    }

    private static List<Integer> buildPaginationSequence(
            PaginationStrategy strategy,
            int currentPage,
            int totalPages) {

        return switch (strategy) {
            case EARLY -> buildEarlySequence(totalPages);
            case LATE -> buildLateSequence(totalPages);
            case MIDDLE -> buildMiddleSequence(currentPage, totalPages);
        };
    }

    private static List<Integer> buildEarlySequence(int totalPages) {
        var pages = new ArrayList<>(generatePageRange(1, PAGES_AROUND_CURRENT + 1));
        pages.add(ELLIPSIS_MARKER);
        pages.add(totalPages - 1);
        return pages;
    }

    private static List<Integer> buildLateSequence(int totalPages) {
        var pages = new ArrayList<Integer>();
        pages.add(ELLIPSIS_MARKER);
        pages.addAll(generatePageRange(totalPages - PAGES_AROUND_CURRENT, totalPages));
        return pages;
    }

    private static List<Integer> buildMiddleSequence(int currentPage, int totalPages) {
        var pages = new ArrayList<Integer>();
        pages.add(ELLIPSIS_MARKER);
        pages.addAll(generatePageRange(currentPage - 1, currentPage + 2));
        pages.add(ELLIPSIS_MARKER);
        pages.add(totalPages - 1);
        return pages;
    }

    private static List<Integer> generatePageRange(int startInclusive, int endExclusive) {
        return IntStream.range(startInclusive, endExclusive)
                .boxed()
                .toList();
    }

    /**
     * Enum representing pagination strategies based on current page position.
     */
    private enum PaginationStrategy {
        /**
         * Current page is near the beginning (pages 0-3).
         */
        EARLY,

        /**
         * Current page is near the end (within last 4 pages).
         */
        LATE,

        /**
         * Current page is in the middle.
         */
        MIDDLE
    }
}
