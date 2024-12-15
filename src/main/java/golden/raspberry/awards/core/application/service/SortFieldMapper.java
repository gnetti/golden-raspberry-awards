package golden.raspberry.awards.core.application.service;

import java.util.Objects;

/**
 * Service for mapping sort field names.
 * Pure business logic without framework dependencies.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class SortFieldMapper {

    private static final String DEFAULT_SORT_FIELD = "id";
    private static final String SORT_FIELD_YEAR = "year";
    private static final String SORT_FIELD_TITLE = "title";
    private static final String SORT_FIELD_STUDIOS = "studios";
    private static final String SORT_FIELD_PRODUCERS = "producers";
    private static final String SORT_FIELD_WINNER = "winner";

    private SortFieldMapper() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    /**
     * Maps a sort field name to a valid database field name.
     *
     * @param sortBy Sort field name from request
     * @return Valid database field name, defaults to "id" if invalid
     * @throws NullPointerException if sortBy is null
     */
    public static String mapSortField(String sortBy) {
        Objects.requireNonNull(sortBy, "Sort field cannot be null");
        
        return switch (sortBy.toLowerCase()) {
            case SORT_FIELD_YEAR -> SORT_FIELD_YEAR;
            case SORT_FIELD_TITLE -> SORT_FIELD_TITLE;
            case SORT_FIELD_STUDIOS -> SORT_FIELD_STUDIOS;
            case SORT_FIELD_PRODUCERS -> SORT_FIELD_PRODUCERS;
            case SORT_FIELD_WINNER -> SORT_FIELD_WINNER;
            default -> DEFAULT_SORT_FIELD;
        };
    }
}

