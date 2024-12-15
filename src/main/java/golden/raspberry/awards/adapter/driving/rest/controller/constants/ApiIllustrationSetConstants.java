package golden.raspberry.awards.adapter.driving.rest.controller.constants;

/**
 * Constants for Swagger API examples.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class SwaggerExamplesConstants {

    private SwaggerExamplesConstants() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    public static final String EXAMPLE_MOVIE_SUCCESS = """
            {
              "id": 1,
              "year": 1980,
              "title": "Can't Stop the Music",
              "studios": "Associated Film Distribution",
              "producers": "Allan Carr",
              "winner": true
            }
            """;

    public static final String EXAMPLE_MOVIE_LIST_SUCCESS = """
            {
              "content": [
                {
                  "id": 1,
                  "year": 1980,
                  "title": "Can't Stop the Music",
                  "studios": "Associated Film Distribution",
                  "producers": "Allan Carr",
                  "winner": true
                },
                {
                  "id": 2,
                  "year": 1981,
                  "title": "Mommie Dearest",
                  "studios": "Paramount Pictures",
                  "producers": "Frank Yablans",
                  "winner": true
                }
              ],
              "pageable": {
                "pageNumber": 0,
                "pageSize": 10
              },
              "totalElements": 213,
              "totalPages": 22
            }
            """;

    public static final String EXAMPLE_CREATE_MOVIE_REQUEST = """
            {
              "year": 2024,
              "title": "Example Movie Title",
              "studios": "Example Studios",
              "producers": "Producer One, Producer Two",
              "winner": false
            }
            """;

    public static final String EXAMPLE_UPDATE_MOVIE_REQUEST = """
            {
              "year": 2024,
              "title": "Updated Movie Title",
              "studios": "Updated Studios",
              "producers": "Updated Producer",
              "winner": true
            }
            """;

    public static final String EXAMPLE_PRODUCER_INTERVALS_SUCCESS = """
            {
              "min": [
                {
                  "producer": "Joel Silver",
                  "interval": 1,
                  "previousWin": 1990,
                  "followingWin": 1991
                }
              ],
              "max": [
                {
                  "producer": "Matthew Vaughn",
                  "interval": 13,
                  "previousWin": 2002,
                  "followingWin": 2015
                }
              ]
            }
            """;

    public static final String EXAMPLE_ERROR_BAD_REQUEST = """
            {
              "timestamp": "2025-01-21T12:00:00",
              "status": 400,
              "error": "Bad Request",
              "message": "Year must be at least 1900, but was: 1800",
              "path": "/api/movies"
            }
            """;

    public static final String EXAMPLE_ERROR_NOT_FOUND = """
            {
              "timestamp": "2025-01-21T12:00:00",
              "status": 404,
              "error": "Not Found",
              "message": "Movie with ID 999 not found",
              "path": "/api/movies/999"
            }
            """;

    public static final String EXAMPLE_ERROR_VALIDATION = """
            {
              "timestamp": "2025-01-21T12:00:00",
              "status": 400,
              "error": "Bad Request",
              "message": "Validation failed",
              "path": "/api/movies",
              "errors": [
                {
                  "field": "year",
                  "message": "Year must be at least 1900",
                  "rejectedValue": 1800,
                  "type": "integer",
                  "validValues": "a number between 1900 and 2025 (current year, no future years allowed)"
                }
              ]
            }
            """;

    public static final String EXAMPLE_ERROR_INTERNAL_SERVER = """
            {
              "timestamp": "2025-01-21T12:00:00",
              "status": 500,
              "error": "Internal Server Error",
              "message": "An unexpected error occurred",
              "path": "/api/movies/producers/intervals"
            }
            """;
}

