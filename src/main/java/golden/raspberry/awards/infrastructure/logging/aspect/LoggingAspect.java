package golden.raspberry.awards.infrastructure.logging.aspect;

import golden.raspberry.awards.core.application.usecase.LogOperationUseCase;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * AOP Aspect for automatically intercepting CREATE, UPDATE, DELETE operations.
 *
 * <p><strong>Important:</strong> Uses custom logging use case - NO external logging dependencies.
 *
 * <p>This aspect intercepts repository operations and logs them automatically
 * using the LogOperationUseCase. Follows hexagonal architecture principles:
 * - Aspect (Infrastructure) calls Use Case (Application)
 * - Use Case orchestrates through Port (LoggingPort)
 * - Adapter (FileLoggingAdapter) implements Port and writes to file
 *
 * <p>Uses Java 21 features: var, Pattern Matching (future enhancement).
 *
 * @author Golden Raspberry Awards Team
 * @since 1.0.0
 */
@Aspect
@Component
public class LoggingAspect {
    
    private final LogOperationUseCase logOperationUseCase;
    
    /**
     * Constructor for dependency injection.
     *
     * @param logOperationUseCase Logging use case (Application layer)
     */
    public LoggingAspect(LogOperationUseCase logOperationUseCase) {
        this.logOperationUseCase = Objects.requireNonNull(logOperationUseCase, "LogOperationUseCase cannot be null");
    }
    
    /**
     * Gets the current session ID from the use case.
     *
     * @return Session identifier
     */
    public String getSessionId() {
        return logOperationUseCase.getSessionId();
    }
    
    /**
     * Intercepts methods annotated with @Loggable.
     * For now, this is a placeholder for future AOP interception.
     *
     * @param joinPoint Join point for intercepted method
     * @return Result of method execution
     * @throws Throwable if method execution fails
     */
    @Around("@annotation(Loggable)")
    public Object logOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        // TODO: Implement automatic interception of CREATE, UPDATE, DELETE operations

        return joinPoint.proceed();
    }
}
