package golden.raspberry.awards.infrastructure.adapter.driven.listener.aspect;

import golden.raspberry.awards.core.application.port.out.ListenerPort;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.aspectj.lang.reflect.SourceLocation;
import org.aspectj.runtime.internal.AroundClosure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ListenerAspect Tests")
class ListenerAspectTest {

    private ListenerPort listenerPort;
    private ListenerAspect aspect;
    private TestController mockTarget;

    @BeforeEach
    void setUp() {
        listenerPort = mock(ListenerPort.class);
        aspect = new ListenerAspect(listenerPort);
        mockTarget = new TestController();
    }

    /**
     * Test controller class with RequestMapping annotation for testing.
     */
    @RequestMapping("/api/movies")
    private static class TestController {
        @GetMapping
        public String getMovies() {
            return "movies";
        }

        @PostMapping
        public ResponseEntity<?> createMovie() {
            return ResponseEntity.ok().build();
        }

        @PutMapping("/{id}")
        public ResponseEntity<?> updateMovie() {
            return ResponseEntity.ok().build();
        }

        @DeleteMapping("/{id}")
        public ResponseEntity<?> deleteMovie() {
            return ResponseEntity.noContent().build();
        }
    }

    /**
     * Stub implementation of MethodSignature for testing.
     * Mockito cannot mock MethodSignature with inline mocks in Java 21.
     */
    private static class StubMethodSignature implements MethodSignature {
        private final Method method;
        private final Class<?> returnType;

        StubMethodSignature(Method method, Class<?> returnType) {
            this.method = method;
            this.returnType = returnType;
        }

        @Override
        public Method getMethod() {
            return method;
        }

        @Override
        public Class<?> getReturnType() {
            return returnType;
        }

        @Override
        public String getName() {
            return method != null ? method.getName() : "test";
        }

        @Override
        public Class<?> getDeclaringType() {
            return method != null ? method.getDeclaringClass() : Object.class;
        }

        @Override
        public String getDeclaringTypeName() {
            return method != null ? method.getDeclaringClass().getName() : Object.class.getName();
        }

        @Override
        public Class<?>[] getParameterTypes() {
            return method != null ? method.getParameterTypes() : new Class[0];
        }

        @Override
        public String[] getParameterNames() {
            return new String[0];
        }

        @Override
        public Class<?>[] getExceptionTypes() {
            return method != null ? method.getExceptionTypes() : new Class[0];
        }

        @Override
        public String toShortString() {
            return "test()";
        }

        @Override
        public String toLongString() {
            return "public void test()";
        }

        @Override
        public String toString() {
            return "test()";
        }

        @Override
        public int getModifiers() {
            return method != null ? method.getModifiers() : 0;
        }
    }

    /**
     * Stub implementation of ProceedingJoinPoint for testing.
     * Mockito cannot mock ProceedingJoinPoint with inline mocks in Java 21.
     */
    private static class StubProceedingJoinPoint implements ProceedingJoinPoint {
        private final Object target;
        private final Object[] args;
        private final MethodSignature methodSignature;
        private final Object proceedResult;
        private final Throwable proceedException;

        StubProceedingJoinPoint(Object target, Object[] args, MethodSignature methodSignature, 
                                Object proceedResult, Throwable proceedException) {
            this.target = target;
            this.args = args;
            this.methodSignature = methodSignature;
            this.proceedResult = proceedResult;
            this.proceedException = proceedException;
        }

        @Override
        public Object proceed() throws Throwable {
            if (proceedException != null) {
                throw proceedException;
            }
            return proceedResult;
        }

        @Override
        public Object proceed(Object[] args) throws Throwable {
            if (proceedException != null) {
                throw proceedException;
            }
            return proceedResult;
        }

        @Override
        public String toShortString() {
            return "execution(test())";
        }

        @Override
        public String toLongString() {
            return "execution(public void test())";
        }

        @Override
        public Object getThis() {
            return target;
        }

        @Override
        public Object getTarget() {
            return target;
        }

        @Override
        public Object[] getArgs() {
            return args != null ? args : new Object[0];
        }

        @Override
        public Signature getSignature() {
            return methodSignature;
        }

        @Override
        public SourceLocation getSourceLocation() {
            return null;
        }

        @Override
        public String getKind() {
            return JoinPoint.METHOD_EXECUTION;
        }

        @Override
        public StaticPart getStaticPart() {
            return null;
        }

        @Override
        public void set$AroundClosure(AroundClosure aroundClosure) {
            // No-op for testing
        }
    }

    @Test
    @DisplayName("Should throw exception when ListenerPort is null")
    void shouldThrowExceptionWhenListenerPortIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new ListenerAspect(null));

        assertEquals("ListenerPort cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should get session ID from request when session exists")
    void shouldGetSessionIdFromRequestWhenSessionExists() {
        var request = new MockHttpServletRequest();
        var session = new MockHttpSession();
        request.setSession(session);
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        var sessionId = aspect.getSessionId();

        assertNotNull(sessionId);
        assertEquals(session.getId(), sessionId);
        
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should generate UUID when no request context")
    void shouldGenerateUuidWhenNoRequestContext() {
        RequestContextHolder.resetRequestAttributes();

        var sessionId = aspect.getSessionId();

        assertNotNull(sessionId);
        assertFalse(sessionId.isEmpty());
    }

    @Test
    @DisplayName("Should create new session when no existing session")
    void shouldCreateNewSessionWhenNoExistingSession() {
        var request = new MockHttpServletRequest();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        var sessionId = aspect.getSessionId();

        assertNotNull(sessionId);
        assertFalse(sessionId.isEmpty());
        
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should intercept GET operation successfully")
    void shouldInterceptGetOperationSuccessfully() throws Throwable {
        var method = TestController.class.getMethod("getMovies");
        var methodSignature = new StubMethodSignature(method, String.class);
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/movies");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        var joinPoint = new StubProceedingJoinPoint(mockTarget, new Object[]{}, methodSignature, "Success", null);

        var result = aspect.listenGetOperation(joinPoint);

        assertEquals("Success", result);
        verify(listenerPort).listenGet(anyString(), eq("GET"), anyString(), anyInt(), anyString(), any(), any(), isNull());
        
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should intercept POST operation successfully")
    void shouldInterceptPostOperationSuccessfully() throws Throwable {
        var method = TestController.class.getMethod("createMovie");
        var methodSignature = new StubMethodSignature(method, ResponseEntity.class);
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/movies");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        var requestData = new Object();

        var joinPoint = new StubProceedingJoinPoint(mockTarget, new Object[]{requestData}, methodSignature, ResponseEntity.ok().build(), null);

        var result = aspect.listenPostOperation(joinPoint);

        assertNotNull(result);
        verify(listenerPort).listenPost(anyString(), eq("POST"), anyString(), anyInt(), anyString(), any(), any(), any(), isNull());
        
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should intercept PUT operation successfully")
    void shouldInterceptPutOperationSuccessfully() throws Throwable {
        var method = TestController.class.getMethod("updateMovie");
        var methodSignature = new StubMethodSignature(method, ResponseEntity.class);
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/movies/1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        var requestData = new Object();

        var joinPoint = new StubProceedingJoinPoint(mockTarget, new Object[]{1L, requestData}, methodSignature, ResponseEntity.ok().build(), null);

        var result = aspect.listenPutOperation(joinPoint);

        assertNotNull(result);
        verify(listenerPort).listenPut(anyString(), eq("PUT"), anyString(), anyInt(), anyString(), any(), any(), any(), isNull());
        
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should intercept DELETE operation successfully")
    void shouldInterceptDeleteOperationSuccessfully() throws Throwable {
        var method = TestController.class.getMethod("deleteMovie");
        var methodSignature = new StubMethodSignature(method, ResponseEntity.class);
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/movies/1");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        var joinPoint = new StubProceedingJoinPoint(mockTarget, new Object[]{1L}, methodSignature, ResponseEntity.noContent().build(), null);

        var result = aspect.listenDeleteOperation(joinPoint);

        assertNotNull(result);
        verify(listenerPort).listenDelete(anyString(), eq("DELETE"), anyString(), anyInt(), anyString(), any(), any(), isNull());
        
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should handle exception during operation")
    void shouldHandleExceptionDuringOperation() throws Throwable {
        var method = TestController.class.getMethod("getMovies");
        var methodSignature = new StubMethodSignature(method, String.class);
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/movies");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        var exception = new RuntimeException("Error occurred");

        var joinPoint = new StubProceedingJoinPoint(mockTarget, new Object[]{}, methodSignature, null, exception);

        var thrown = assertThrows(RuntimeException.class, () ->
                aspect.listenGetOperation(joinPoint));

        assertEquals("Error occurred", thrown.getMessage());
        verify(listenerPort).listenGet(anyString(), eq("GET"), anyString(), eq(500), anyString(), any(), isNull(), anyString());
        
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should handle exception with null message")
    void shouldHandleExceptionWithNullMessage() throws Throwable {
        var method = TestController.class.getMethod("getMovies");
        var methodSignature = new StubMethodSignature(method, String.class);
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/movies");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        var exception = new RuntimeException();

        var joinPoint = new StubProceedingJoinPoint(mockTarget, new Object[]{}, methodSignature, null, exception);

        assertThrows(RuntimeException.class, () ->
                aspect.listenGetOperation(joinPoint));

        verify(listenerPort).listenGet(anyString(), eq("GET"), anyString(), eq(500), anyString(), any(), isNull(), eq("RuntimeException"));
        
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should handle exception with blank message")
    void shouldHandleExceptionWithBlankMessage() throws Throwable {
        var method = TestController.class.getMethod("getMovies");
        var methodSignature = new StubMethodSignature(method, String.class);
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/movies");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        var exception = new RuntimeException("   ");

        var joinPoint = new StubProceedingJoinPoint(mockTarget, new Object[]{}, methodSignature, null, exception);

        assertThrows(RuntimeException.class, () ->
                aspect.listenGetOperation(joinPoint));

        verify(listenerPort).listenGet(anyString(), eq("GET"), anyString(), eq(500), anyString(), any(), isNull(), eq("RuntimeException"));
        
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should extract entity ID from Long argument")
    void shouldExtractEntityIdFromLongArgument() throws Throwable {
        var method = TestController.class.getMethod("getMovies");
        var methodSignature = new StubMethodSignature(method, String.class);
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/movies/123");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        var joinPoint = new StubProceedingJoinPoint(mockTarget, new Object[]{123L}, methodSignature, "Success", null);

        aspect.listenGetOperation(joinPoint);

        verify(listenerPort).listenGet(anyString(), eq("GET"), anyString(), anyInt(), anyString(), eq("123"), any(), isNull());
        
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should extract entity ID from String argument")
    void shouldExtractEntityIdFromStringArgument() throws Throwable {
        var method = TestController.class.getMethod("getMovies");
        var methodSignature = new StubMethodSignature(method, String.class);
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/movies/test-id");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        var joinPoint = new StubProceedingJoinPoint(mockTarget, new Object[]{"test-id"}, methodSignature, "Success", null);

        aspect.listenGetOperation(joinPoint);

        verify(listenerPort).listenGet(anyString(), eq("GET"), anyString(), anyInt(), anyString(), eq("test-id"), any(), isNull());
        
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should handle ResponseEntity with error status code")
    void shouldHandleResponseEntityWithErrorStatusCode() throws Throwable {
        var method = TestController.class.getMethod("getMovies");
        var methodSignature = new StubMethodSignature(method, ResponseEntity.class);
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/movies");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        var errorResponse = ResponseEntity.badRequest().body("Error message");

        var joinPoint = new StubProceedingJoinPoint(mockTarget, new Object[]{}, methodSignature, errorResponse, null);

        var result = aspect.listenGetOperation(joinPoint);

        assertNotNull(result);
        verify(listenerPort).listenGet(anyString(), eq("GET"), anyString(), eq(400), anyString(), any(), any(), anyString());
        
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should handle non-ResponseEntity response")
    void shouldHandleNonResponseEntityResponse() throws Throwable {
        var method = TestController.class.getMethod("getMovies");
        var methodSignature = new StubMethodSignature(method, String.class);
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/movies");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        var response = "Simple string response";

        var joinPoint = new StubProceedingJoinPoint(mockTarget, new Object[]{}, methodSignature, response, null);

        var result = aspect.listenGetOperation(joinPoint);

        assertEquals(response, result);
        verify(listenerPort).listenGet(anyString(), eq("GET"), anyString(), eq(200), anyString(), any(), eq(response), isNull());
        
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should handle ResponseEntity with null body and status 200")
    void shouldHandleResponseEntityWithNullBody() throws Throwable {
        var method = TestController.class.getMethod("getMovies");
        var methodSignature = new StubMethodSignature(method, ResponseEntity.class);
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/movies");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        var errorResponse = ResponseEntity.ok().build();

        var joinPoint = new StubProceedingJoinPoint(mockTarget, new Object[]{}, methodSignature, errorResponse, null);

        var result = aspect.listenGetOperation(joinPoint);

        assertNotNull(result);
        verify(listenerPort).listenGet(anyString(), eq("GET"), anyString(), eq(200), anyString(), any(), isNull(), isNull());
        
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should handle ResponseEntity with null body and error status")
    void shouldHandleResponseEntityWithNullBodyAndErrorStatus() throws Throwable {
        var method = TestController.class.getMethod("getMovies");
        var methodSignature = new StubMethodSignature(method, ResponseEntity.class);
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/movies");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        var errorResponse = ResponseEntity.badRequest().build();

        var joinPoint = new StubProceedingJoinPoint(mockTarget, new Object[]{}, methodSignature, errorResponse, null);

        var result = aspect.listenGetOperation(joinPoint);

        assertNotNull(result);
        verify(listenerPort).listenGet(anyString(), eq("GET"), anyString(), eq(400), anyString(), any(), isNull(), eq("No response body"));
        
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should handle extractRequestData with POST method")
    void shouldHandleExtractRequestDataWithPostMethod() throws Throwable {
        var method = TestController.class.getMethod("createMovie");
        var methodSignature = new StubMethodSignature(method, ResponseEntity.class);
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/movies");
        request.setMethod("POST");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        var requestBody = new Object();

        var joinPoint = new StubProceedingJoinPoint(mockTarget, new Object[]{requestBody}, methodSignature, ResponseEntity.ok().build(), null);

        aspect.listenPostOperation(joinPoint);

        verify(listenerPort).listenPost(anyString(), eq("POST"), anyString(), anyInt(), anyString(), any(), eq(requestBody), isNull(), isNull());
        
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should handle extractRequestData with PUT method")
    void shouldHandleExtractRequestDataWithPutMethod() throws Throwable {
        var method = TestController.class.getMethod("updateMovie");
        var methodSignature = new StubMethodSignature(method, ResponseEntity.class);
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/movies/1");
        request.setMethod("PUT");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        var requestBody = new Object();

        var joinPoint = new StubProceedingJoinPoint(mockTarget, new Object[]{requestBody}, methodSignature, ResponseEntity.ok().build(), null);

        aspect.listenPutOperation(joinPoint);

        verify(listenerPort).listenPut(anyString(), eq("PUT"), anyString(), anyInt(), anyString(), isNull(), eq(requestBody), isNull(), isNull());
        
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should handle extractRequestData with PUT method and entity ID")
    void shouldHandleExtractRequestDataWithPutMethodAndEntityId() throws Throwable {
        var method = TestController.class.getMethod("updateMovie");
        var methodSignature = new StubMethodSignature(method, ResponseEntity.class);
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/movies/1");
        request.setMethod("PUT");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
        var requestBody = new Object();

        var joinPoint = new StubProceedingJoinPoint(mockTarget, new Object[]{1L, requestBody}, methodSignature, ResponseEntity.ok().build(), null);

        aspect.listenPutOperation(joinPoint);

        verify(listenerPort).listenPut(anyString(), eq("PUT"), anyString(), anyInt(), anyString(), eq("1"), eq(1L), isNull(), isNull());
        
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should handle extractRequestData with DELETE method")
    void shouldHandleExtractRequestDataWithDeleteMethod() throws Throwable {
        var method = TestController.class.getMethod("deleteMovie");
        var methodSignature = new StubMethodSignature(method, ResponseEntity.class);
        var request = new MockHttpServletRequest();
        request.setRequestURI("/api/movies/1");
        request.setMethod("DELETE");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

        var joinPoint = new StubProceedingJoinPoint(mockTarget, new Object[]{1L}, methodSignature, ResponseEntity.ok().build(), null);

        aspect.listenDeleteOperation(joinPoint);

        verify(listenerPort).listenDelete(anyString(), eq("DELETE"), anyString(), anyInt(), anyString(), eq("1"), isNull(), isNull());
        
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    @DisplayName("Should call extractErrorFromResponse method directly via reflection")
    void shouldCallExtractErrorFromResponseMethodDirectly() throws Exception {
        var method = ListenerAspect.class.getDeclaredMethod("extractErrorFromResponse", Object.class);
        method.setAccessible(true);
        
        var resultWithBody = method.invoke(aspect, "Error message");
        assertEquals("Error message", resultWithBody);
        
        var resultWithNull = method.invoke(aspect, (Object) null);
        assertEquals("No response body", resultWithNull);
    }
}

