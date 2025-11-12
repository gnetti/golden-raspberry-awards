package golden.raspberry.awards.integration.helper;

import org.springframework.dao.InvalidDataAccessResourceUsageException;

import java.util.function.Supplier;

public final class DatabaseSchemaHelper {

    private static final int MAX_ATTEMPTS = 20;
    private static final long INITIAL_DELAY_MS = 200L;
    private static final long MAX_DELAY_MS = 5000L;

    private DatabaseSchemaHelper() {
        throw new AssertionError("Utility class cannot be instantiated");
    }

    public static void ensureSchemaReady(Runnable databaseOperation) {
        ensureSchemaReady(() -> {
            databaseOperation.run();
            return null;
        });
    }

    public static <T> T ensureSchemaReady(Supplier<T> databaseOperation) {
        long delayMs = INITIAL_DELAY_MS;
        
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                return databaseOperation.get();
            } catch (InvalidDataAccessResourceUsageException e) {
                if (isTableNotFoundError(e)) {
                    if (attempt < MAX_ATTEMPTS) {
                        try {
                            Thread.sleep(delayMs);
                            delayMs = Math.min(delayMs * 2, MAX_DELAY_MS);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new IllegalStateException(
                                    "Schema readiness check interrupted after %d attempts".formatted(attempt), ie
                            );
                        }
                    } else {
                        throw new IllegalStateException(
                                "Schema not created after %d attempts. Last error: %s"
                                        .formatted(MAX_ATTEMPTS, e.getMessage()), e
                        );
                    }
                } else {
                    throw e;
                }
            }
        }
        
        throw new IllegalStateException("Schema readiness check failed after " + MAX_ATTEMPTS + " attempts");
    }

    private static boolean isTableNotFoundError(InvalidDataAccessResourceUsageException e) {
        if (e.getMessage() == null) {
            return false;
        }
        String message = e.getMessage().toLowerCase();
        return message.contains("table") && message.contains("not found");
    }
}

