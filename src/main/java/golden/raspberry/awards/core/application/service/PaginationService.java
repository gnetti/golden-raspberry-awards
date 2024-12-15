package golden.raspberry.awards.core.application.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service for pagination calculations.
 * Pure business logic without framework dependencies.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class PaginationService {

    private static final int MAX_VISIBLE_PAGES = 7;
    private static final int ELLIPSIS_MARKER = -1;

    private PaginationService() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    /**
     * Calculates page numbers to display with ellipsis for large page counts.
     * Shows max 7-page numbers: first, last, current, and 2-3 on each side.
     *
     * @param currentPage Current page number (0-based)
     * @param totalPages Total number of pages
     * @return List of page numbers to display, with -1 representing ellipsis
     * @throws IllegalArgumentException if currentPage < 0 or totalPages < 1
     */
    public static List<Integer> calculatePageNumbers(int currentPage, int totalPages) {
        Objects.requireNonNull(totalPages, "Total pages cannot be null");
        
        if (currentPage < 0) {
            throw new IllegalArgumentException("Current page must be >= 0, but was: " + currentPage);
        }
        if (totalPages < 1) {
            throw new IllegalArgumentException("Total pages must be >= 1, but was: " + totalPages);
        }
        if (currentPage >= totalPages) {
            throw new IllegalArgumentException(
                    "Current page (%d) must be < total pages (%d)".formatted(currentPage, totalPages)
            );
        }

        var pages = new ArrayList<Integer>();

        if (totalPages <= MAX_VISIBLE_PAGES) {
            for (int i = 0; i < totalPages; i++) {
                pages.add(i);
            }
            return pages;
        }

        pages.add(0);

        if (currentPage <= 3) {
            for (int i = 1; i <= 5; i++) {
                pages.add(i);
            }
            pages.add(ELLIPSIS_MARKER);
            pages.add(totalPages - 1);
        } else if (currentPage >= totalPages - 4) {
            pages.add(ELLIPSIS_MARKER);
            for (int i = totalPages - 5; i < totalPages; i++) {
                pages.add(i);
            }
        } else {
            pages.add(ELLIPSIS_MARKER);
            pages.add(currentPage - 1);
            pages.add(currentPage);
            pages.add(currentPage + 1);
            pages.add(ELLIPSIS_MARKER);
            pages.add(totalPages - 1);
        }

        return pages;
    }
}

