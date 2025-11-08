package golden.raspberry.awards.infrastructure.logging.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark methods that should be automatically logged.
 *
 * <p>Methods annotated with @Loggable will be intercepted by LoggingAspect
 * and logged automatically using the custom logging service.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Loggable {
    /**
     * Action type (CREATE, UPDATE, DELETE).
     * If not specified, will be inferred from method name.
     */
    String action() default "";
    
    /**
     * Entity type (e.g., "Movie", "Producer").
     * If not specified, will be inferred from method parameters.
     */
    String entityType() default "";
}

