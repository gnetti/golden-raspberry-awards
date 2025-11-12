package golden.raspberry.awards.adapter.driving.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import golden.raspberry.awards.adapter.driving.rest.dto.Constants.MovieSchemaConstant;
import golden.raspberry.awards.adapter.driving.rest.controller.constants.ApiIllustrationSetConstants;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.util.Optional;

/**
 * DTO for updating an existing movie (request body). *
 * <p>Follows Richardson Level 2: structured request format.
 * Does not include ID (passed as path variable).
 * *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Schema(
        description = "Data Transfer Object for updating movies",
        example = ApiIllustrationSetConstants.ILLUSTRATION_SET_UPDATE_MOVIE_REQUEST
)
public record UpdateMovieDTO(
        @JsonProperty("year")
        @Schema(
                description = MovieSchemaConstant.YEAR_DESCRIPTION,
                example = MovieSchemaConstant.YEAR_ILLUSTRATION_SET,
                minimum = MovieSchemaConstant.YEAR_MINIMUM,
                maximum = MovieSchemaConstant.YEAR_MAXIMUM
        )
        @NotNull(message = "Field 'year' is required and cannot be null")
        @Min(value = 1900, message = "Field 'year' must be at least 1900")
        @Max(value = 2025, message = "Field 'year' cannot be in the future. Maximum allowed year is 2025")
        Integer year,

        @JsonProperty("title")
        @Schema(
                description = MovieSchemaConstant.TITLE_DESCRIPTION,
                example = MovieSchemaConstant.TITLE_ILLUSTRATION_SET
        )
        @NotNull(message = "Field 'title' is required and cannot be null")
        @NotBlank(message = "Field 'title' cannot be empty or contain only whitespace")
        @Size(min = 2, max = 255, message = "Field 'title' must be between 2 and 255 characters")
        String title,

        @JsonProperty("studios")
        @Schema(
                description = MovieSchemaConstant.STUDIOS_DESCRIPTION,
                example = MovieSchemaConstant.STUDIOS_ILLUSTRATION_SET
        )
        @NotNull(message = "Field 'studios' is required and cannot be null")
        @NotBlank(message = "Field 'studios' cannot be empty or contain only whitespace")
        @Size(min = 2, max = 255, message = "Field 'studios' must be between 2 and 255 characters")
        String studios,

        @JsonProperty("producers")
        @Schema(
                description = MovieSchemaConstant.PRODUCERS_DESCRIPTION,
                example = MovieSchemaConstant.PRODUCERS_ILLUSTRATION_SET
        )
        @NotNull(message = "Field 'producers' is required and cannot be null")
        @NotBlank(message = "Field 'producers' cannot be empty or contain only whitespace")
        @Size(min = 2, max = 255, message = "Field 'producers' must be between 2 and 255 characters")
        String producers,

        @JsonProperty("winner")
        @Schema(
                description = MovieSchemaConstant.WINNER_DESCRIPTION,
                example = MovieSchemaConstant.WINNER_ILLUSTRATION_SET
        )
        @NotNull(message = "Field 'winner' is required and cannot be null")
        Boolean winner
) {
    public UpdateMovieDTO {
        title = Optional.ofNullable(title).map(String::trim).orElse(title);
        studios = Optional.ofNullable(studios).map(String::trim).orElse(studios);
        producers = Optional.ofNullable(producers).map(String::trim).orElse(producers);
    }

}
