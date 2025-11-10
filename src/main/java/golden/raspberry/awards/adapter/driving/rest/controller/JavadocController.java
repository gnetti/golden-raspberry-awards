package golden.raspberry.awards.adapter.driving.rest.controller;

import golden.raspberry.awards.adapter.driving.rest.dto.ApiErrorDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Objects;

/**
 * REST Controller for serving JavaDoc documentation.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@RestController
@RequestMapping("/docs")
@Tag(name = "Documentation", description = "API documentation endpoints")
public class JavadocController {

    /**
     * Redirects to JavaDoc index page.
     *
     * @param response HTTP servlet response
     * @throws IOException if redirect fails
     */
    @Operation(
            summary = "Redirect to JavaDoc documentation",
            description = "Redirects to the JavaDoc index page at /docs/index.html"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "302",
                    description = "Redirect to JavaDoc index page",
                    headers = {
                            @io.swagger.v3.oas.annotations.headers.Header(
                                    name = "Location",
                                    description = "Redirect location: /docs/index.html",
                                    schema = @Schema(type = "string", example = "/docs/index.html")
                            )
                    }
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error - occurs if IOException is thrown during redirect",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Server Error",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T10:00:00",
                                                "status": 500,
                                                "error": "Internal Server Error",
                                                "message": "An unexpected error occurred while redirecting",
                                                "path": "/docs"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping
    public void redirectToJavadoc(HttpServletResponse response) throws IOException {
        Objects.requireNonNull(response, "HttpServletResponse cannot be null");
        response.sendRedirect("/docs/index.html");
    }

    /**
     * Provides information about available documentation.
     *
     * @return HTML page with documentation links
     */
    @Operation(
            summary = "Get documentation information",
            description = "Returns an HTML page with links to available API documentation"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Documentation information page",
                    content = @Content(
                            mediaType = "text/html",
                            schema = @Schema(type = "string", format = "html"),
                            examples = @ExampleObject(
                                    name = "Documentation Page",
                                    value = """
                                            <!DOCTYPE html>
                                            <html lang="en">
                                            <head>
                                                <meta charset="UTF-8">
                                                <title>API Documentation - Golden Raspberry Awards</title>
                                            </head>
                                            <body>
                                                <div class="container">
                                                    <h1>📚 API Documentation</h1>
                                                    <p>Welcome to the Golden Raspberry Awards API documentation center.</p>
                                                    <div class="link-card">
                                                        <a href="/docs/index.html">📖 JavaDoc Documentation</a>
                                                        <p class="description">Complete API reference documentation generated from JavaDoc comments.</p>
                                                    </div>
                                                    <div class="link-card">
                                                        <a href="/swagger-ui.html">🔧 Swagger UI</a>
                                                        <p class="description">Interactive API documentation and testing interface.</p>
                                                    </div>
                                                    <div class="link-card">
                                                        <a href="/api/movies">🎬 Movies API</a>
                                                        <p class="description">RestFul endpoints for movie operations.</p>
                                                    </div>
                                                </div>
                                            </body>
                                            </html>
                                            """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error - occurs if an unhandled exception is thrown",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiErrorDTO.class),
                            examples = @ExampleObject(
                                    name = "Server Error",
                                    value = """
                                            {
                                                "timestamp": "2025-11-10T10:00:00",
                                                "status": 500,
                                                "error": "Internal Server Error",
                                                "message": "An unexpected error occurred while generating documentation page",
                                                "path": "/docs/info"
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping(value = "/info", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getDocumentationInfo() {
        String htmlResponse = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>API Documentation - Golden Raspberry Awards</title>
                <style>
                    * {
                        margin: 0;
                        padding: 0;
                        box-sizing: border-box;
                    }
                    body {
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, sans-serif;
                        line-height: 1.6;
                        background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                        min-height: 100vh;
                        padding: 20px;
                    }
                    .container {
                        max-width: 900px;
                        margin: 0 auto;
                        background: white;
                        padding: 40px;
                        border-radius: 12px;
                        box-shadow: 0 10px 40px rgba(0,0,0,0.2);
                    }
                    .header {
                        text-align: center;
                        margin-bottom: 40px;
                        padding-bottom: 20px;
                        border-bottom: 3px solid #667eea;
                    }
                    .header h1 {
                        color: #333;
                        font-size: 2.5em;
                        margin-bottom: 10px;
                    }
                    .header p {
                        color: #666;
                        font-size: 1.1em;
                    }
                    .info-section {
                        background: #f8f9fa;
                        padding: 20px;
                        border-radius: 8px;
                        margin-bottom: 30px;
                        border-left: 4px solid #667eea;
                    }
                    .info-section h2 {
                        color: #333;
                        margin-bottom: 10px;
                        font-size: 1.3em;
                    }
                    .info-section p {
                        color: #666;
                        margin-bottom: 5px;
                    }
                    .links-grid {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
                        gap: 20px;
                        margin-top: 30px;
                    }
                    .link-card {
                        background: linear-gradient(135deg, #f8f9fa 0%, #ffffff 100%);
                        padding: 25px;
                        border-radius: 10px;
                        border: 2px solid #e9ecef;
                        transition: all 0.3s ease;
                        text-decoration: none;
                        display: block;
                        color: inherit;
                    }
                    .link-card:hover {
                        transform: translateY(-5px);
                        box-shadow: 0 8px 20px rgba(102, 126, 234, 0.3);
                        border-color: #667eea;
                    }
                    .link-card .icon {
                        font-size: 2.5em;
                        margin-bottom: 15px;
                        display: block;
                    }
                    .link-card .title {
                        color: #333;
                        font-weight: bold;
                        font-size: 1.2em;
                        margin-bottom: 10px;
                        text-decoration: none;
                    }
                    .link-card .description {
                        color: #666;
                        font-size: 0.95em;
                        line-height: 1.5;
                    }
                    .footer {
                        text-align: center;
                        margin-top: 40px;
                        padding-top: 20px;
                        border-top: 2px solid #e9ecef;
                        color: #999;
                        font-size: 0.9em;
                    }
                    @media (max-width: 768px) {
                        .container {
                            padding: 20px;
                        }
                        .header h1 {
                            font-size: 2em;
                        }
                        .links-grid {
                            grid-template-columns: 1fr;
                        }
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>📚 API Documentation</h1>
                        <p>Golden Raspberry Awards - Complete API Reference</p>
                    </div>
                    
                    <div class="info-section">
                        <h2>ℹ️ About This API</h2>
                        <p><strong>Version:</strong> 1.0.0</p>
                        <p><strong>Description:</strong> RESTful API for managing Golden Raspberry Awards movie data</p>
                        <p><strong>Base URL:</strong> <code>http://localhost:8080</code></p>
                        <p><strong>Architecture:</strong> Hexagonal Architecture (Ports & Adapters)</p>
                        <p><strong>Maturity Level:</strong> Richardson Level 2</p>
                    </div>
                    
                    <div class="links-grid">
                        <a href="/swagger-ui.html" class="link-card">
                            <span class="icon">🔧</span>
                            <div class="title">Swagger UI</div>
                            <div class="description">Interactive API documentation with live testing capabilities. Try out all endpoints directly from your browser.</div>
                        </a>
                        
                        <a href="/docs/index.html" class="link-card">
                            <span class="icon">📖</span>
                            <div class="title">JavaDoc Documentation</div>
                            <div class="description">Complete API reference documentation generated from JavaDoc comments. Includes all classes, methods, and parameters.</div>
                        </a>
                        
                        <a href="/api-docs" class="link-card">
                            <span class="icon">📄</span>
                            <div class="title">OpenAPI Specification</div>
                            <div class="description">Raw OpenAPI 3.0 JSON specification. Use this to import into API clients like Postman or generate SDKs.</div>
                        </a>
                        
                        <a href="/api/movies" class="link-card">
                            <span class="icon">🎬</span>
                            <div class="title">Movies API</div>
                            <div class="description">RESTful endpoints for movie CRUD operations. Create, read, update, and delete movie records.</div>
                        </a>
                        
                        <a href="/api/movies/producers/intervals" class="link-card">
                            <span class="icon">📊</span>
                            <div class="title">Producer Intervals</div>
                            <div class="description">Get producers with the greatest and smallest interval between two consecutive awards.</div>
                        </a>
                        
                        <a href="/h2-console" class="link-card">
                            <span class="icon">💾</span>
                            <div class="title">H2 Database Console</div>
                            <div class="description">Access the in-memory H2 database console. JDBC URL: jdbc:h2:~/test</div>
                        </a>
                    </div>
                    
                    <div class="footer">
                        <p>Golden Raspberry Awards API © 2025 | Built with Spring Boot 3.4.11 & Java 21</p>
                    </div>
                </div>
            </body>
            </html>
            """;

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(htmlResponse);
    }
}

