package golden.raspberry.awards.adapter.driving.rest.controller;

import golden.raspberry.awards.adapter.driving.rest.dto.ApiErrorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
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
            summary = "Redirect to JavaDoc documentation",
            description = "Redirects to the JavaDoc index page at /docs/index.html"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "302",
                    description = "Redirect to JavaDoc index page"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ApiErrorDTO.class))
            )
    })
    @GetMapping
    public void redirect(HttpServletResponse response) throws IOException {
        Objects.requireNonNull(response, "HttpServletResponse cannot be null");
        response.sendRedirect("/docs/index.html");
    }

}

