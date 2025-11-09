package golden.raspberry.awards.shared.kernel.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation for use cases.
 * Used for architectural validation and metadata.
 *
 * <p>This annotation marks classes that are use cases,
 * orchestrating business flows in the application layer.
 *
 * <p>Uses Java 21 features: Annotation metadata.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface UseCase {
}

