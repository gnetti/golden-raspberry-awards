package golden.raspberry.awards.adapter.driving.rest.controller;

import golden.raspberry.awards.adapter.driving.rest.dto.MovieDTO;
import golden.raspberry.awards.adapter.driving.rest.dto.ProducerIntervalResponseDTO;
import golden.raspberry.awards.core.application.port.in.CalculateIntervalsPort;
import golden.raspberry.awards.core.application.port.in.GetMoviePort;
import golden.raspberry.awards.core.application.port.in.GetMoviesForWebPort;
import golden.raspberry.awards.core.application.port.out.ConverterDtoPort;
import golden.raspberry.awards.core.domain.model.aggregate.MovieWithId;
import golden.raspberry.awards.core.domain.model.valueobject.ProducerIntervalResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ProducerWebController Tests")
class ProducerWebControllerTest {

    private ProducerWebController controller;
    private CalculateIntervalsPort calculateIntervalsPort;
    private ConverterDtoPort converterDtoPort;
    private GetMoviePort getMoviePort;
    private GetMoviesForWebPort getMoviesForWebPort;
    private Model model;

    @BeforeEach
    void setUp() {
        calculateIntervalsPort = mock(CalculateIntervalsPort.class);
        converterDtoPort = mock(ConverterDtoPort.class);
        getMoviePort = mock(GetMoviePort.class);
        getMoviesForWebPort = mock(GetMoviesForWebPort.class);
        model = mock(Model.class);
        controller = new ProducerWebController(
                calculateIntervalsPort,
                converterDtoPort,
                getMoviePort,
                getMoviesForWebPort
        );
    }

    @Test
    @DisplayName("Should redirect root to dashboard")
    void shouldRedirectRootToDashboard() {
        var result = controller.redirectToDashboard();

        assertEquals("redirect:/dashboard", result);
    }

    @Test
    @DisplayName("Should return dashboard page")
    void shouldReturnDashboardPage() {
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        var result = controller.dashboard(model);

        assertEquals("pages/dashboard", result);
        verify(model).addAttribute(eq("title"), eq("Dashboard"));
    }

    @Test
    @DisplayName("Should return intervals page")
    void shouldReturnIntervalsPage() {
        var intervalResponse = new ProducerIntervalResponse(List.of(), List.of());
        var dto = new ProducerIntervalResponseDTO(List.of(), List.of());

        when(calculateIntervalsPort.execute()).thenReturn(intervalResponse);
        when(converterDtoPort.toDTO(intervalResponse)).thenReturn(dto);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        var result = controller.intervals(model, null);

        assertEquals("pages/intervals", result);
        verify(calculateIntervalsPort).execute();
        verify(converterDtoPort).toDTO(intervalResponse);
    }

    @Test
    @DisplayName("Should return intervals modal when modal is true")
    void shouldReturnIntervalsModalWhenModalIsTrue() {
        var intervalResponse = new ProducerIntervalResponse(List.of(), List.of());
        var dto = new ProducerIntervalResponseDTO(List.of(), List.of());

        when(calculateIntervalsPort.execute()).thenReturn(intervalResponse);
        when(converterDtoPort.toDTO(intervalResponse)).thenReturn(dto);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        var result = controller.intervals(model, true);

        assertEquals("fragments/intervals-modal", result);
    }

    @Test
    @DisplayName("Should handle null response in intervals")
    void shouldHandleNullResponseInIntervals() {
        when(calculateIntervalsPort.execute()).thenReturn(null);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        var result = controller.intervals(model, null);

        assertEquals("pages/intervals", result);
        verify(calculateIntervalsPort).execute();
    }

    @Test
    @DisplayName("Should return new movie form page")
    void shouldReturnNewMovieFormPage() {
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        var result = controller.newMovie(model);

        assertEquals("pages/new-movie", result);
        verify(model).addAttribute(eq("title"), eq("New Movie"));
    }

    @Test
    @DisplayName("Should return edit movie form page")
    void shouldReturnEditMovieFormPage() {
        var movieWithId = new MovieWithId(1L, 2020, "Test Movie", "Test Studio", "Test Producer", true);
        var movieDTO = new MovieDTO(1L, 2020, "Test Movie", "Test Studio", "Test Producer", true);

        when(getMoviePort.execute(1L)).thenReturn(movieWithId);
        when(converterDtoPort.toDTO(movieWithId)).thenReturn(movieDTO);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        var result = controller.editMovie(1L, model);

        assertEquals("pages/edit-movie", result);
        verify(getMoviePort).execute(1L);
        verify(converterDtoPort).toDTO(movieWithId);
        verify(model).addAttribute(eq("title"), eq("Edit Movie"));
    }

    @Test
    @DisplayName("Should handle exception in intervals method")
    void shouldHandleExceptionInIntervalsMethod() {
        when(calculateIntervalsPort.execute()).thenThrow(new RuntimeException("Error"));
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        var result = controller.intervals(model, null);

        assertEquals("pages/intervals", result);
        verify(calculateIntervalsPort).execute();
    }

    @Test
    @DisplayName("Should return movies page")
    void shouldReturnMoviesPage() {
        var response = new GetMoviesForWebPort.MoviesWebResponse(
                List.of(new MovieDTO(1L, 2020, "Test", "Studio", "Producer", true)),
                0, 1, 1L, 10, "id", "asc", List.of(0), null, null
        );

        when(getMoviesForWebPort.execute(any())).thenReturn(response);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        var result = controller.movies(model, 0, 10, "id", "asc", null, null);

        assertEquals("pages/movies", result);
        verify(getMoviesForWebPort).execute(any());
    }

    @Test
    @DisplayName("Should return manual page")
    void shouldReturnManualPage() {
        var result = controller.manual();

        assertEquals("pages/manual", result);
    }

    @Test
    @DisplayName("Should handle null converterDtoPort in intervals")
    void shouldHandleNullConverterDtoPortInIntervals() {
        var intervalResponse = new ProducerIntervalResponse(List.of(), List.of());
        when(calculateIntervalsPort.execute()).thenReturn(intervalResponse);
        when(converterDtoPort.toDTO(intervalResponse)).thenReturn(null);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        var result = controller.intervals(model, null);

        assertEquals("pages/intervals", result);
        verify(calculateIntervalsPort).execute();
        verify(converterDtoPort).toDTO(intervalResponse);
    }

    @Test
    @DisplayName("Should handle null converterDtoPort in intervals with modal")
    void shouldHandleNullConverterDtoPortInIntervalsWithModal() {
        var intervalResponse = new ProducerIntervalResponse(List.of(), List.of());
        when(calculateIntervalsPort.execute()).thenReturn(intervalResponse);
        when(converterDtoPort.toDTO(intervalResponse)).thenReturn(null);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        var result = controller.intervals(model, true);

        assertEquals("fragments/intervals-modal", result);
    }

    @Test
    @DisplayName("Should return intervals modal when modal is false")
    void shouldReturnIntervalsModalWhenModalIsFalse() {
        var intervalResponse = new ProducerIntervalResponse(List.of(), List.of());
        var dto = new ProducerIntervalResponseDTO(List.of(), List.of());

        when(calculateIntervalsPort.execute()).thenReturn(intervalResponse);
        when(converterDtoPort.toDTO(intervalResponse)).thenReturn(dto);
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        var result = controller.intervals(model, false);

        assertEquals("pages/intervals", result);
    }


    @Test
    @DisplayName("Should handle dashboard correctly")
    void shouldHandleDashboardCorrectly() {
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        var result = controller.dashboard(model);

        assertEquals("pages/dashboard", result);
        verify(model, atLeastOnce()).addAttribute(anyString(), any());
    }


    @Test
    @DisplayName("Should handle intervals with exception and modal true")
    void shouldHandleIntervalsWithExceptionAndModalTrue() {
        when(calculateIntervalsPort.execute()).thenThrow(new RuntimeException("Error"));
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        var result = controller.intervals(model, true);

        assertEquals("fragments/intervals-modal", result);
    }

    @Test
    @DisplayName("Should handle intervals with exception and modal false")
    void shouldHandleIntervalsWithExceptionAndModalFalse() {
        when(calculateIntervalsPort.execute()).thenThrow(new RuntimeException("Error"));
        when(model.addAttribute(anyString(), any())).thenReturn(model);

        var result = controller.intervals(model, false);

        assertEquals("pages/intervals", result);
    }

    @Test
    @DisplayName("Should handle intervals with null calculateIntervalsPort using reflection")
    void shouldHandleIntervalsWithNullCalculateIntervalsPortUsingReflection() throws Exception {
        var calculateIntervalsPortField = ProducerWebController.class.getDeclaredField("calculateIntervalsPort");
        calculateIntervalsPortField.setAccessible(true);
        calculateIntervalsPortField.set(controller, null);
        
        when(model.addAttribute(anyString(), any())).thenReturn(model);
        
        var result = controller.intervals(model, false);
        
        assertEquals("pages/intervals", result);
    }

    @Test
    @DisplayName("Should handle intervals with null converterDtoPort using reflection")
    void shouldHandleIntervalsWithNullConverterDtoPortUsingReflection() throws Exception {
        var converterDtoPortField = ProducerWebController.class.getDeclaredField("converterDtoPort");
        converterDtoPortField.setAccessible(true);
        converterDtoPortField.set(controller, null);
        
        when(model.addAttribute(anyString(), any())).thenReturn(model);
        
        var result = controller.intervals(model, true);
        
        assertEquals("fragments/intervals-modal", result);
    }

}

