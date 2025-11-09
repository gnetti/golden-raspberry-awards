package golden.raspberry.awards.shared.constant;

/**
 * Architecture constants.
 * Centralizes architectural configuration values.
 *
 * <p>This class contains all architectural constants used across the application,
 * such as package names, layer boundaries, and architectural rules.
 *
 * <p>Uses Java 21 features: Static final fields.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
public final class ArchitectureConstants {

    private ArchitectureConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static final String DOMAIN_PACKAGE = "golden.raspberry.awards.core.domain";
    public static final String APPLICATION_PACKAGE = "golden.raspberry.awards.core.application";
    public static final String INFRASTRUCTURE_PACKAGE = "golden.raspberry.awards.infrastructure";
    public static final String ADAPTER_DRIVING_PACKAGE = "golden.raspberry.awards.adapter.driving";
    public static final String ADAPTER_DRIVEN_PACKAGE = "golden.raspberry.awards.adapter.driven";
    public static final String SHARED_KERNEL_PACKAGE = "golden.raspberry.awards.shared";
}

