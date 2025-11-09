package golden.raspberry.awards.shared.constant;

/**
 * Validation constraints constants.
 * Centralizes validation constraint values.
 *
 * <p>This class contains all validation constraint constants used across the application,
 * ensuring consistency in validation rules.
 *
 * <p>Uses Java 21 features: Static final fields.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class ValidationConstraints {

    private ValidationConstraints() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final int YEAR_MIN = 1900;
    public static final int YEAR_MAX = 2100;
    public static final int YEAR_LENGTH = 4;
    public static final int TITLE_MIN_LENGTH = 2;
    public static final int TITLE_MAX_LENGTH = 255;
    public static final int STUDIOS_MIN_LENGTH = 2;
    public static final int STUDIOS_MAX_LENGTH = 255;
    public static final int PRODUCERS_MIN_LENGTH = 2;
    public static final int PRODUCERS_MAX_LENGTH = 255;
}

