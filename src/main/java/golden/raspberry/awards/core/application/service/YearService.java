package golden.raspberry.awards.core.application.service;

import java.time.Year;

/**
 * Service for year-related operations.
 * Pure business logic without framework dependencies.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class YearService {

    private YearService() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    /**
     * Gets the current year.
     *
     * @return Current year
     */
    public static int getCurrentYear() {
        return Year.now().getValue();
    }
}

