package golden.raspberry.awards.adapter.driving.rest.dto.Constants;

/**
 * Constants for Movie DTO Schema annotations to avoid duplication.
 * These constants are used directly in @Schema annotations for Swagger/OpenAPI documentation.
 * <p>
 * Java annotations can use String constants, making this approach both elegant and maintainable.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class MovieSchemaConstant {

    private MovieSchemaConstant() {
    }

    public static final String YEAR_DESCRIPTION = "Release year of the movie";
    public static final String YEAR_ILLUSTRATION_SET = "2024";
    public static final String YEAR_MINIMUM = "1900";
    public static final String YEAR_MAXIMUM = "2025";

    public static final String TITLE_DESCRIPTION = "Title of the movie";
    public static final String TITLE_ILLUSTRATION_SET = "The Matrix Resurrections";

    public static final String STUDIOS_DESCRIPTION = "Production studios";
    public static final String STUDIOS_ILLUSTRATION_SET = "Warner Bros. Pictures";

    public static final String PRODUCERS_DESCRIPTION = "Movie producers";
    public static final String PRODUCERS_ILLUSTRATION_SET = "Lana Wachowski, Grant Hill";

    public static final String WINNER_DESCRIPTION = "Indicates if the movie won the award";
    public static final String WINNER_ILLUSTRATION_SET = "true";

    public static final String ID_DESCRIPTION = "Movie unique identifier";
    public static final String ID_ILLUSTRATION_SET = "1";
}

