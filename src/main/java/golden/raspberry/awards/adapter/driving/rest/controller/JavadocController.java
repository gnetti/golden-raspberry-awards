package golden.raspberry.awards.adapter.driving.rest.controller;

import golden.raspberry.awards.adapter.driving.rest.dto.ApiErrorDTO;
import golden.raspberry.awards.adapter.driving.rest.controller.constants.JavadocControllerConstants;
import golden.raspberry.awards.adapter.driving.rest.controller.constants.ApiIllustrationSetConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Objects;

/**
 * Controller for serving JavaDoc documentation.
 * Redirects requests to the JavaDoc index page.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Controller
@RequestMapping("/docs")
@Tag(name = "Documentation", description = "API documentation endpoints")
public class JavadocController {

    @Operation(
            summary = JavadocControllerConstants.OPERATION_SUMMARY_REDIRECT_TO_JAVADOC,
            description = JavadocControllerConstants.OPERATION_DESCRIPTION_REDIRECT_TO_JAVADOC
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = JavadocControllerConstants.HTTP_STATUS_CODE_FOUND_STRING,
                    description = JavadocControllerConstants.API_RESPONSE_DESCRIPTION_REDIRECT
            ),
            @ApiResponse(
                    responseCode = JavadocControllerConstants.HTTP_STATUS_CODE_INTERNAL_SERVER_ERROR_STRING,
                    description = JavadocControllerConstants.API_RESPONSE_DESCRIPTION_INTERNAL_SERVER_ERROR,
                    content = @Content(
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Internal Server Error",
                                    value = ApiIllustrationSetConstants.ILLUSTRATION_SET_ERROR_INTERNAL_SERVER
                            )
                    )
            )
    })
    @GetMapping
    public void redirect(HttpServletResponse response) throws IOException {
        Objects.requireNonNull(response, JavadocControllerConstants.ERROR_MESSAGE_HTTP_SERVLET_RESPONSE_CANNOT_BE_NULL);
        response.sendRedirect("/docs/index.html");
    }

}

