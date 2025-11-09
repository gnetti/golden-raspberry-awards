package golden.raspberry.awards.infrastructure.common.time;

import org.springframework.stereotype.Component;

import java.time.Year;

/**
 * Adapter for getting current year.
 * Provides current year information for business rules validation.
 *
 * <p>This adapter is part of the Infrastructure layer and provides
 * time-related information that may be needed by domain services
 * or use cases for validation purposes.
 *
 * <p><strong>Hexagonal Architecture:</strong>
 * <ul>
 *   <li>Driven Adapter (Secondary) - provides time information</li>
 *   <li>Part of Infrastructure layer</li>
 *   <li>Can be used via a port if needed by Application layer</li>
 * </ul>
 *
 * <p>Uses Java 21 features: Year API.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Component
public class LocalDateCurrentYearAdapter {

    /**
     * Gets the current year.
     *
     * @return Current year as Integer
     */
    public Integer getCurrentYear() {
        return Year.now().getValue();
    }
}

