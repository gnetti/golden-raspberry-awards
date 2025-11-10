package golden.raspberry.awards.adapter.driving.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import golden.raspberry.awards.adapter.driving.rest.dto.Constant.MovieSchemaConstant;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * DTO for Movie in REST API. *
 * <p>Follows Richardson Level 2: structured response format.
 * Includes ID for update/delete operations.
 * *
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Schema(description = "Data Transfer Object for movie responses")
public record MovieDTO(
        @JsonProperty("id")
        @Schema(description = MovieSchemaConstant.ID_DESCRIPTION, example = MovieSchemaConstant.ID_EXAMPLE)
        Long id,

        @JsonProperty("year")
        @Schema(
                description = MovieSchemaConstant.YEAR_DESCRIPTION,
                example = MovieSchemaConstant.YEAR_EXAMPLE,
                minimum = MovieSchemaConstant.YEAR_MINIMUM,
                maximum = MovieSchemaConstant.YEAR_MAXIMUM
        )
        Integer year,

        @JsonProperty("title")
        @Schema(description = MovieSchemaConstant.TITLE_DESCRIPTION, example = MovieSchemaConstant.TITLE_EXAMPLE)
        String title,

        @JsonProperty("studios")
        @Schema(description = MovieSchemaConstant.STUDIOS_DESCRIPTION, example = MovieSchemaConstant.STUDIOS_EXAMPLE)
        String studios,

        @JsonProperty("producers")
        @Schema(description = MovieSchemaConstant.PRODUCERS_DESCRIPTION, example = MovieSchemaConstant.PRODUCERS_EXAMPLE)
        String producers,

        @JsonProperty("winner")
        @Schema(description = MovieSchemaConstant.WINNER_DESCRIPTION, example = MovieSchemaConstant.WINNER_EXAMPLE)
        Boolean winner
) {

}
