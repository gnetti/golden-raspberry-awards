package golden.raspberry.awards.core.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("YearService Tests")
class YearServiceTest {

    @Test
    @DisplayName("Should return current year")
    void shouldReturnCurrentYear() {
        var result = YearService.getCurrentYear();
        var expectedYear = Year.now().getValue();

        assertEquals(expectedYear, result);
    }

    @Test
    @DisplayName("Should return positive year")
    void shouldReturnPositiveYear() {
        var result = YearService.getCurrentYear();

        assertTrue(result > 0);
    }
}

