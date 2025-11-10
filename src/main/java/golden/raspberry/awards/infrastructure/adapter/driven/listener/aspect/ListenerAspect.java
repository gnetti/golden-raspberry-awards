package golden.raspberry.awards.infrastructure.adapter.driven.listener.aspect;

import golden.raspberry.awards.core.application.port.out.ListenerPort;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * AOP Aspect for intercepting REST API operations.
 *
 * @author Luiz Generoso
 * @since 1.0.0
 */
@Aspect
@Component
public class ListenerAspect {

    /**
     * Encapsulates operation data for listener.
     *
     * @param httpMethod HTTP method
     * @param endpoint Endpoint path
     * @param statusCode HTTP status code
     * @param entityType Entity type
     * @param entityId Entity identifier
     * @param dataBefore Data before operation
     * @param dataAfter Data after operation
     * @param requestData Request data
     * @param error Error message
     */
    private record OperationData(
            String httpMethod,
            String endpoint,
            Integer statusCode,
            String entityType,
            String entityId,
            Object dataBefore,
            Object dataAfter,
            Object requestData,
            String error
    ) {
        /**
         * Compact constructor with validation.
         */
        public OperationData {
            Objects.requireNonNull(httpMethod, "HTTP method cannot be null");
            Objects.requireNonNull(endpoint, "Endpoint cannot be null");
            Objects.requireNonNull(statusCode, "Status code cannot be null");
            Objects.requireNonNull(entityType, "Entity type cannot be null");
        }

        /**
         * Creates OperationData for GET operations.
         */
        public static OperationData forGet(String httpMethod, String endpoint, Integer statusCode,
                                           String entityType, String entityId, Object dataAfter, String error) {
            return new OperationData(httpMethod, endpoint, statusCode, entityType, entityId,
                    null, dataAfter, null, error);
        }

        /**
         * Creates OperationData for PUT operations.
         */
        public static OperationData forPut(String httpMethod, String endpoint, Integer statusCode,
                                           String entityType, String entityId,
                                           Object dataBefore, Object dataAfter, String error) {
            return new OperationData(httpMethod, endpoint, statusCode, entityType, entityId,
                    dataBefore, dataAfter, null, error);
        }

        /**
         * Creates OperationData for DELETE operations.
         */
        public static OperationData forDelete(String httpMethod, String endpoint, Integer statusCode,
                                              String entityType, String entityId, Object dataBefore, String error) {
            return new OperationData(httpMethod, endpoint, statusCode, entityType, entityId,
                    dataBefore, null, null, error);
        }

        /**
         * Creates OperationData for POST operations.
         */
        public static OperationData forPost(String httpMethod, String endpoint, Integer statusCode,
                                            String entityType, String entityId,
                                            Object requestData, Object dataAfter, String error) {
            return new OperationData(httpMethod, endpoint, statusCode, entityType, entityId,
                    null, dataAfter, requestData, error);
        }

        /**
         * Creates OperationData for error cases.
         */
        public static OperationData forError(String httpMethod, String endpoint, String entityType,
                                             String entityId, Object requestData, String error) {
            return new OperationData(httpMethod, endpoint, 500, entityType, entityId,
                    null, null, requestData, error);
        }

        /**
         * Creates OperationData with default values.
         */
        public static OperationData withDefaults(String httpMethod, String endpoint, String entityType,
                                                 String entityId, Object requestData) {
            return new OperationData(httpMethod, endpoint, 200, entityType, entityId,
                    null, null, requestData, null);
        }
    }

    private final ListenerPort listenerPort;

    /**
     * Constructor for dependency injection.
     *
     * @param listenerPort Listener port (output port)
     */
    public ListenerAspect(ListenerPort listenerPort) {
        this.listenerPort = Objects.requireNonNull(listenerPort, "ListenerPort cannot be null");
    }

    /**
     * Gets the current session ID from the HTTP request.
     *
     * @return Session identifier
     */
    public String getSessionId() {
        return extractSessionIdFromRequest()
                .orElseGet(() -> {
                    var requestAttributes = RequestContextHolder.getRequestAttributes();
                    if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
                        var request = servletRequestAttributes.getRequest();
                        var session = request.getSession(true);
                        return session.getId();
                    }
                    return UUID.randomUUID().toString();
                });
    }

    /**
     * Extracts session ID from HTTP request.
     *
     * @return Optional containing session ID if available, empty otherwise
     */
    private Optional<String> extractSessionIdFromRequest() {
        var requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletRequestAttributes) {
            var request = servletRequestAttributes.getRequest();
            var session = request.getSession(false);
            if (session != null) {
                return Optional.of(session.getId());
            }
        }
        return Optional.empty();
    }

    /**
     * Intercepts GET methods.
     *
     * @param joinPoint Join point
     * @return Result of method execution
     * @throws Throwable if method execution fails
     */
    @Around("@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public Object listenGetOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        return listenOperation(joinPoint, "GET");
    }

    /**
     * Intercepts PUT methods.
     *
     * @param joinPoint Join point
     * @return Result of method execution
     * @throws Throwable if method execution fails
     */
    @Around("@annotation(org.springframework.web.bind.annotation.PutMapping)")
    public Object listenPutOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        return listenOperation(joinPoint, "PUT");
    }

    /**
     * Intercepts DELETE methods.
     *
     * @param joinPoint Join point
     * @return Result of method execution
     * @throws Throwable if method execution fails
     */
    @Around("@annotation(org.springframework.web.bind.annotation.DeleteMapping)")
    public Object listenDeleteOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        return listenOperation(joinPoint, "DELETE");
    }

    /**
     * Intercepts POST methods.
     *
     * @param joinPoint Join point
     * @return Result of method execution
     * @throws Throwable if method execution fails
     */
    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public Object listenPostOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        return listenOperation(joinPoint, "POST");
    }

    /**
     * Listens to REST API operation.
     *
     * @param joinPoint Join point
     * @param httpMethod HTTP method
     * @return Result of method execution
     * @throws Throwable if method execution fails
     */
    private Object listenOperation(ProceedingJoinPoint joinPoint, String httpMethod) throws Throwable {
        var sessionId = getSessionId();
        var endpoint = extractEndpoint(joinPoint);
        var entityType = extractEntityType(joinPoint);
        var entityId = extractEntityId(joinPoint.getArgs());
        var requestData = extractRequestData(joinPoint.getArgs(), httpMethod);

        OperationData operationData = OperationData.withDefaults(
                httpMethod, endpoint, entityType, entityId, requestData
        );

        Object response;
        try {
            response = joinPoint.proceed();
            operationData = processResponse(response, httpMethod, endpoint, entityType, entityId, requestData);
        } catch (Throwable throwable) {
            var error = Optional.ofNullable(throwable.getMessage())
                    .filter(msg -> !msg.isBlank())
                    .orElseGet(() -> throwable.getClass().getSimpleName());

            operationData = OperationData.forError(
                    httpMethod, endpoint, entityType, entityId, requestData, error
            );
            throw throwable;
        } finally {
            listenOperationData(operationData, sessionId);
        }

        return response;
    }


    /**
     * Processes response.
     *
     * @param response Response object
     * @param httpMethod HTTP method
     * @param endpoint Endpoint path
     * @param entityType Entity type
     * @param entityId Entity identifier
     * @param requestData Request data
     * @return OperationData with processed information
     */
    private OperationData processResponse(Object response, String httpMethod, String endpoint,
                                          String entityType, String entityId, Object requestData) {
        if (response instanceof ResponseEntity<?> responseEntity) {
            return processResponseEntity(
                    responseEntity, httpMethod, endpoint, entityType, entityId, requestData
            );
        }
        return OperationData.forGet(
                httpMethod, endpoint, 200, entityType, entityId, response, null
        );
    }

    /**
     * Processes ResponseEntity.
     *
     * @param responseEntity ResponseEntity
     * @param httpMethod HTTP method
     * @param endpoint Endpoint path
     * @param entityType Entity type
     * @param entityId Entity identifier
     * @param requestData Request data
     * @return OperationData with processed information
     */
    private OperationData processResponseEntity(ResponseEntity<?> responseEntity, String httpMethod,
                                                String endpoint, String entityType, String entityId,
                                                Object requestData) {
        var statusCode = responseEntity.getStatusCode().value();
        var body = responseEntity.getBody();
        var error = statusCode >= 400 ? extractErrorFromResponse(body) : null;

        return switch (httpMethod) {
            case "GET" -> OperationData.forGet(
                    httpMethod, endpoint, statusCode, entityType, entityId, body, error
            );
            case "PUT" -> OperationData.forPut(
                    httpMethod, endpoint, statusCode, entityType, entityId, requestData, body, error
            );
            case "DELETE" -> OperationData.forDelete(
                    httpMethod, endpoint, statusCode, entityType, entityId, requestData, error
            );

            default -> OperationData.forPost(
                    httpMethod, endpoint, statusCode, entityType, entityId, requestData, body, error
            );
        };
    }

    /**
     * Listens to the operation using ListenerPort.
     *
     * @param operationData Operation data
     * @param sessionId Session ID
     */
    private void listenOperationData(OperationData operationData, String sessionId) {
        switch (operationData.httpMethod()) {
            case "GET" -> listenerPort.listenGet(
                    sessionId, operationData.httpMethod(), operationData.endpoint(), operationData.statusCode(),
                    operationData.entityType(), operationData.entityId(),
                    operationData.dataAfter(), operationData.error()
            );
            case "PUT" -> listenerPort.listenPut(
                    sessionId, operationData.httpMethod(), operationData.endpoint(), operationData.statusCode(),
                    operationData.entityType(), operationData.entityId(),
                    operationData.dataBefore(), operationData.dataAfter(), operationData.error()
            );
            case "DELETE" -> listenerPort.listenDelete(
                    sessionId, operationData.httpMethod(), operationData.endpoint(), operationData.statusCode(),
                    operationData.entityType(), operationData.entityId(),
                    operationData.dataBefore(), operationData.error()
            );
            case "POST" -> listenerPort.listenPost(
                    sessionId, operationData.httpMethod(), operationData.endpoint(), operationData.statusCode(),
                    operationData.entityType(), operationData.entityId(),
                    operationData.requestData(), operationData.dataAfter(), operationData.error()
            );
        }
    }

    /**
     * Extracts endpoint path from join point.
     *
     * @param joinPoint Join point
     * @return Endpoint path
     */
    private String extractEndpoint(ProceedingJoinPoint joinPoint) {
        var method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        var classMapping = joinPoint.getTarget().getClass().getAnnotation(RequestMapping.class);
        var methodPath = extractMethodPath(method);

        var basePath = Optional.ofNullable(classMapping)
                .map(RequestMapping::value)
                .filter(paths -> paths.length > 0)
                .map(paths -> paths[0])
                .orElse("");

        var fullPath = (basePath + methodPath).replaceAll("//+", "/");
        return fullPath.isEmpty() ? "/" : fullPath;
    }

    /**
     * Extracts method-level path from mapping annotations.
     *
     * @param method Method
     * @return Method path or empty string
     */
    private String extractMethodPath(Method method) {
        return Optional.<String>empty()
                .or(() -> Optional.ofNullable(method.getAnnotation(GetMapping.class))
                        .filter(m -> m.value().length > 0)
                        .map(m -> m.value()[0]))
                .or(() -> Optional.ofNullable(method.getAnnotation(PutMapping.class))
                        .filter(m -> m.value().length > 0)
                        .map(m -> m.value()[0]))
                .or(() -> Optional.ofNullable(method.getAnnotation(DeleteMapping.class))
                        .filter(m -> m.value().length > 0)
                        .map(m -> m.value()[0]))
                .or(() -> Optional.ofNullable(method.getAnnotation(PostMapping.class))
                        .filter(m -> m.value().length > 0)
                        .map(m -> m.value()[0]))
                .orElse("");
    }

    /**
     * Extracts entity type from join point.
     *
     * @param joinPoint Join point
     * @return Entity type
     */
    private String extractEntityType(ProceedingJoinPoint joinPoint) {
        var className = joinPoint.getTarget().getClass().getSimpleName();
        return className.endsWith("Controller")
                ? className.substring(0, className.length() - "Controller".length())
                : className;
    }

    /**
     * Extracts entity ID from method arguments.
     *
     * @param args Method arguments
     * @return Entity ID or null
     */
    private String extractEntityId(Object[] args) {
        if (args == null) {
            return null;
        }

        return java.util.Arrays.stream(args)
                .map(arg -> switch (arg) {
                    case String str when !str.isBlank() -> str;
                    case Long l -> String.valueOf(l);
                    case Integer i -> String.valueOf(i);
                    default -> null;
                })
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

    /**
     * Extracts request data from method arguments.
     *
     * @param args Method arguments
     * @param httpMethod HTTP method
     * @return Request data or null
     */
    private Object extractRequestData(Object[] args, String httpMethod) {
        if (args == null || args.length == 0) {
            return null;
        }

        return switch (httpMethod) {
            case "PUT", "POST" -> args[0];
            default -> null;
        };
    }

    /**
     * Extracts error message from response body.
     *
     * @param responseBody Response body
     * @return Error message or null
     */
    private String extractErrorFromResponse(Object responseBody) {
        return Optional.ofNullable(responseBody)
                .map(Object::toString)
                .orElse("No response body");
    }
}

