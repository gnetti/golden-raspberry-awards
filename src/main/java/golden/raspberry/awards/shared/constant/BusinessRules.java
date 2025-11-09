package golden.raspberry.awards.shared.constant;

/**
 * Business rules constants.
 * Centralizes business rule values.
 *
 * <p>This class contains all business rule constants used across the application,
 * ensuring consistency and maintainability.
 *
 * <p>Uses Java 21 features: Static final fields.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class BusinessRules {

    private BusinessRules() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final int MIN_YEAR = 1900;
    public static final int MAX_YEAR = 2100;
    public static final int MIN_TITLE_LENGTH = 2;
    public static final int MAX_TITLE_LENGTH = 255;
    public static final int MIN_STUDIOS_LENGTH = 2;
    public static final int MAX_STUDIOS_LENGTH = 255;
    public static final int MIN_PRODUCERS_LENGTH = 2;
    public static final int MAX_PRODUCERS_LENGTH = 255;
}

