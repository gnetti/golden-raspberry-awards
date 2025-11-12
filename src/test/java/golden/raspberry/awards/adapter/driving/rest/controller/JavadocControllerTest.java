package golden.raspberry.awards.adapter.driving.rest.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("JavadocController Tests")
class JavadocControllerTest {

    private JavadocController controller;
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        controller = new JavadocController();
        response = mock(HttpServletResponse.class);
    }

    @Test
    @DisplayName("Should redirect to javadoc index")
    void shouldRedirectToJavadocIndex() throws IOException {
        controller.redirect(response);

        verify(response).sendRedirect("/docs/index.html");
    }

    @Test
    @DisplayName("Should throw exception when response is null")
    void shouldThrowExceptionWhenResponseIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                controller.redirect(null));

        assertTrue(exception.getMessage().contains("HttpServletResponse cannot be null"));
    }

    @Test
    @DisplayName("Should handle IOException during redirect")
    void shouldHandleIOExceptionDuringRedirect() throws IOException {
        doThrow(new IOException("Redirect failed")).when(response).sendRedirect("/docs/index.html");

        assertThrows(IOException.class, () ->
                controller.redirect(response));
    }
}

