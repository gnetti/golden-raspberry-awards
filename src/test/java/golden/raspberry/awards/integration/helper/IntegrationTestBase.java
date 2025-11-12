package golden.raspberry.awards.integration.helper;

import golden.raspberry.awards.core.application.port.out.IdKeyManagerPort;
import golden.raspberry.awards.infrastructure.adapter.driven.persistence.repository.MovieJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public abstract class IntegrationTestBase {

    @Autowired
    protected MovieJpaRepository jpaRepository;

    @Autowired(required = false)
    protected DataSource dataSource;

    @Autowired(required = false)
    protected IdKeyManagerPort idKeyManagerPort;

    private static final int MAX_RETRY_ATTEMPTS = 10;
    private static final long INITIAL_DELAY_MS = 50L;
    private static final long MAX_DELAY_MS = 500L;

    @BeforeEach
    protected void ensureSchemaReady() {
        if (jpaRepository == null || dataSource == null) {
            return;
        }

        try {
            jpaRepository.count();
            return;
        } catch (InvalidDataAccessResourceUsageException e) {
            if (!isTableNotFoundError(e)) {
                throw new IllegalStateException("Unexpected database error", e);
            }
        } catch (Exception e) {
        }

        long delayMs = INITIAL_DELAY_MS;
        for (int attempt = 1; attempt <= MAX_RETRY_ATTEMPTS; attempt++) {
            try {
                createSchemaManually();
                jpaRepository.count();
                return;
            } catch (InvalidDataAccessResourceUsageException e) {
                if (isTableNotFoundError(e)) {
                    if (attempt < MAX_RETRY_ATTEMPTS) {
                        try {
                            Thread.sleep(delayMs);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            return;
                        }
                        delayMs = Math.min(delayMs * 2, MAX_DELAY_MS);
                    } else {
                        throw new IllegalStateException(
                                "Failed to create schema after " + MAX_RETRY_ATTEMPTS + " attempts", e);
                    }
                } else {
                    throw new IllegalStateException("Unexpected database error", e);
                }
            } catch (Exception e) {
                if (attempt == MAX_RETRY_ATTEMPTS) {
                    throw new IllegalStateException("Failed to ensure schema readiness", e);
                }
                try {
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    return;
                }
                delayMs = Math.min(delayMs * 2, MAX_DELAY_MS);
            }
        }
    }

    private boolean isTableNotFoundError(InvalidDataAccessResourceUsageException e) {
        if (e.getMessage() == null) {
            return false;
        }
        var message = e.getMessage().toLowerCase();
        return message.contains("table") && message.contains("not found");
    }

    private void createSchemaManually() {
        if (dataSource == null) {
            throw new IllegalStateException(
                    "Cannot create schema manually: DataSource not available. " +
                    "Hibernate should create schema automatically with ddl-auto=create"
            );
        }
        try {
            var jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS "movies" (
                    "id" BIGINT NOT NULL,
                    "year" INTEGER NOT NULL,
                    "title" VARCHAR(255) NOT NULL,
                    "studios" VARCHAR(255) NOT NULL,
                    "producers" VARCHAR(255) NOT NULL,
                    "winner" BOOLEAN NOT NULL,
                    PRIMARY KEY ("id")
                )
                """);
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to create schema manually", ex);
        }
    }

    protected void safeCleanup() {
        if (jpaRepository == null) {
            return;
        }
        try {
            jpaRepository.deleteAll();
            if (idKeyManagerPort != null) {
                var maxIdFromDatabase = jpaRepository.findMaxId().orElse(0L);
                idKeyManagerPort.synchronizeWithDatabase(maxIdFromDatabase);
            }
        } catch (InvalidDataAccessResourceUsageException e) {
        } catch (Exception e) {
        }
    }
}

