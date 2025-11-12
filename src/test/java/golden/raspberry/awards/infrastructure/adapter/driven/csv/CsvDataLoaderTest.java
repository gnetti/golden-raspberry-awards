package golden.raspberry.awards.infrastructure.adapter.driven.csv;

import golden.raspberry.awards.core.application.port.out.IdKeyManagerPort;
import golden.raspberry.awards.infrastructure.adapter.driven.persistence.entity.MovieEntity;
import golden.raspberry.awards.infrastructure.adapter.driven.persistence.repository.MovieJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("CsvDataLoader Tests")
class CsvDataLoaderTest {

    @TempDir
    Path tempDir;

    private MovieJpaRepository jpaRepository;
    private IdKeyManagerPort idKeyManagerPort;
    private CsvDataLoader loader;

    @BeforeEach
    void setUp() {
        jpaRepository = mock(MovieJpaRepository.class);
        idKeyManagerPort = mock(IdKeyManagerPort.class);
        loader = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                false,
                "data/movieList.csv",
                "data/primary-base/MovieList.csv",
                ";",
                6,
                "yes"
        );
    }

    @AfterEach
    void cleanup() throws IOException {
        var resourcesDir = Paths.get("src/main/resources");
        if (Files.exists(resourcesDir)) {
            try (var stream = Files.list(resourcesDir)) {
                stream.filter(path -> path.getFileName().toString().startsWith("test-movies-") && 
                                   path.getFileName().toString().endsWith(".csv"))
                      .forEach(path -> {
                          try {
                              Files.delete(path);
                          } catch (IOException e) {
                              // Ignore cleanup errors
                          }
                      });
            }
        }
    }

    @Test
    @DisplayName("Should throw exception when JpaRepository is null")
    void shouldThrowExceptionWhenJpaRepositoryIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new CsvDataLoader(null, idKeyManagerPort, false, "test.csv", "original.csv", ";", 6, "yes"));

        assertEquals("JpaRepository cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when IdKeyManagerPort is null")
    void shouldThrowExceptionWhenIdKeyManagerPortIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new CsvDataLoader(jpaRepository, null, false, "test.csv", "original.csv", ";", 6, "yes"));

        assertEquals("IdKeyManagerPort cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when csvFile is null")
    void shouldThrowExceptionWhenCsvFileIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new CsvDataLoader(jpaRepository, idKeyManagerPort, false, null, "original.csv", ";", 6, "yes"));

        assertEquals("CSV file path cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when csvOriginalFile is null")
    void shouldThrowExceptionWhenCsvOriginalFileIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new CsvDataLoader(jpaRepository, idKeyManagerPort, false, "test.csv", null, ";", 6, "yes"));

        assertEquals("CSV original file path cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when winnerYes is null")
    void shouldThrowExceptionWhenWinnerYesIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new CsvDataLoader(jpaRepository, idKeyManagerPort, false, "test.csv", "original.csv", ";", 6, null));

        assertEquals("Winner yes value cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should use default separator when csvSeparator is null")
    void shouldUseDefaultSeparatorWhenCsvSeparatorIsNull() {
        var loaderWithNullSeparator = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                false,
                "data/movieList.csv",
                "data/primary-base/MovieList.csv",
                null,
                6,
                "yes"
        );

        assertDoesNotThrow(() -> loaderWithNullSeparator.run());
    }

    @Test
    @DisplayName("Should use default separator when csvSeparator is empty")
    void shouldUseDefaultSeparatorWhenCsvSeparatorIsEmpty() {
        var loaderWithEmptySeparator = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                false,
                "data/movieList.csv",
                "data/primary-base/MovieList.csv",
                "",
                6,
                "yes"
        );

        assertDoesNotThrow(() -> loaderWithEmptySeparator.run());
    }

    @Test
    @DisplayName("Should run without errors when CSV file exists")
    void shouldRunWithoutErrorsWhenCsvFileExists() {
        assertDoesNotThrow(() -> loader.run());
    }

    @Test
    @DisplayName("Should handle exception during run gracefully")
    void shouldHandleExceptionDuringRunGracefully() {
        when(jpaRepository.findMaxId()).thenThrow(new RuntimeException("Database error"));

        assertDoesNotThrow(() -> loader.run());
    }

    @Test
    @DisplayName("Should handle exception during XML operations gracefully")
    void shouldHandleExceptionDuringXmlOperationsGracefully() {
        when(jpaRepository.findMaxId()).thenReturn(Optional.of(100L));
        doThrow(new RuntimeException("XML error")).when(idKeyManagerPort).synchronizeWithDatabase(anyLong());

        assertDoesNotThrow(() -> loader.run());
    }

    @Test
    @DisplayName("Should synchronize with database when resetToOriginal is false")
    void shouldSynchronizeWithDatabaseWhenResetToOriginalIsFalse() {
        when(jpaRepository.findMaxId()).thenReturn(Optional.of(100L));

        loader.run();

        verify(idKeyManagerPort).synchronizeWithDatabase(100L);
        verify(idKeyManagerPort, never()).resetLastId(anyLong());
    }

    @Test
    @DisplayName("Should reset last ID when resetToOriginal is true")
    void shouldResetLastIdWhenResetToOriginalIsTrue() {
        var resetLoader = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                true,
                "data/movieList.csv",
                "data/primary-base/MovieList.csv",
                ";",
                6,
                "yes"
        );
        when(jpaRepository.findMaxId()).thenReturn(Optional.of(100L));

        assertDoesNotThrow(() -> resetLoader.run());
    }

    @Test
    @DisplayName("Should handle gracefully when CSV file does not exist")
    void shouldHandleGracefullyWhenCsvFileDoesNotExist() {
        var emptyLoader = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                false,
                "absolutely-non-existent-file-that-will-never-exist.csv",
                "data/primary-base/MovieList.csv",
                ";",
                6,
                "yes"
        );
        when(jpaRepository.findMaxId()).thenReturn(Optional.of(0L));

        assertDoesNotThrow(() -> emptyLoader.run());
    }

    @Test
    @DisplayName("Should save entities when list is not empty")
    void shouldSaveEntitiesWhenListIsNotEmpty() {
        var entities = List.of(
                new MovieEntity(1L, 2020, "Movie 1", "Studio", "Producer", true)
        );
        when(jpaRepository.findMaxId()).thenReturn(Optional.of(1L));

        loader.run();

        verify(jpaRepository, atLeastOnce()).saveAll(any());
    }

    @Test
    @DisplayName("Should handle case when resetToOriginal is true and resetLastId is called")
    void shouldHandleCaseWhenResetToOriginalIsTrueAndResetLastIdIsCalled() {
        var resetLoader = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                true,
                "data/movieList.csv",
                "data/primary-base/MovieList.csv",
                ";",
                6,
                "yes"
        );
        when(jpaRepository.findMaxId()).thenReturn(Optional.of(100L));

        resetLoader.run();

        verify(idKeyManagerPort, atLeastOnce()).resetLastId(anyLong());
    }

    @Test
    @DisplayName("Should handle case when resetToOriginal is false and synchronizeWithDatabase is called")
    void shouldHandleCaseWhenResetToOriginalIsFalseAndSynchronizeWithDatabaseIsCalled() {
        when(jpaRepository.findMaxId()).thenReturn(Optional.of(100L));

        loader.run();

        verify(idKeyManagerPort, atLeastOnce()).synchronizeWithDatabase(anyLong());
        verify(idKeyManagerPort, never()).resetLastId(anyLong());
    }

    @Test
    @DisplayName("Should handle case when findMaxId returns empty optional")
    void shouldHandleCaseWhenFindMaxIdReturnsEmptyOptional() {
        when(jpaRepository.findMaxId()).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> loader.run());

        verify(idKeyManagerPort, atLeastOnce()).synchronizeWithDatabase(0L);
    }

    @Test
    @DisplayName("Should handle exception during resetCsvToOriginal")
    void shouldHandleExceptionDuringResetCsvToOriginal() {
        var resetLoader = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                true,
                "non-existent.csv",
                "non-existent-original.csv",
                ";",
                6,
                "yes"
        );

        assertDoesNotThrow(() -> resetLoader.run());
    }

    @Test
    @DisplayName("Should handle CSV with invalid ID format")
    void shouldHandleCsvWithInvalidIdFormat() throws Exception {
        var csvFile = createTempCsvFile("id;year;title;studios;producers;winner\ninvalid;2020;Movie;Studio;Producer;yes");
        var testLoader = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                false,
                csvFile,
                "data/primary-base/MovieList.csv",
                ";",
                6,
                "yes"
        );

        assertDoesNotThrow(() -> testLoader.run());
        verify(jpaRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should handle CSV with invalid year format")
    void shouldHandleCsvWithInvalidYearFormat() throws Exception {
        var csvFile = createTempCsvFile("id;year;title;studios;producers;winner\n1;invalid;Movie;Studio;Producer;yes");
        var testLoader = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                false,
                csvFile,
                "data/primary-base/MovieList.csv",
                ";",
                6,
                "yes"
        );

        assertDoesNotThrow(() -> testLoader.run());
        verify(jpaRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should handle CSV with year out of range")
    void shouldHandleCsvWithYearOutOfRange() throws Exception {
        var csvFile = createTempCsvFile("id;year;title;studios;producers;winner\n1;1899;Movie;Studio;Producer;yes\n2;2101;Movie2;Studio2;Producer2;yes");
        var testLoader = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                false,
                csvFile,
                "data/primary-base/MovieList.csv",
                ";",
                6,
                "yes"
        );

        assertDoesNotThrow(() -> testLoader.run());
        verify(jpaRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should handle CSV with null title field")
    void shouldHandleCsvWithNullTitleField() throws Exception {
        var csvFile = createTempCsvFile("id;year;title;studios;producers;winner\n1;2020;;Studio;Producer;yes");
        var testLoader = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                false,
                csvFile,
                "data/primary-base/MovieList.csv",
                ";",
                6,
                "yes"
        );

        assertDoesNotThrow(() -> testLoader.run());
        verify(jpaRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should handle CSV with blank title field")
    void shouldHandleCsvWithBlankTitleField() throws Exception {
        var csvFile = createTempCsvFile("id;year;title;studios;producers;winner\n1;2020;   ;Studio;Producer;yes");
        var testLoader = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                false,
                csvFile,
                "data/primary-base/MovieList.csv",
                ";",
                6,
                "yes"
        );

        assertDoesNotThrow(() -> testLoader.run());
        verify(jpaRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should handle CSV with invalid ID zero or negative")
    void shouldHandleCsvWithInvalidIdZeroOrNegative() throws Exception {
        var csvFile = createTempCsvFile("id;year;title;studios;producers;winner\n0;2020;Movie;Studio;Producer;yes\n-1;2020;Movie2;Studio2;Producer2;yes");
        var testLoader = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                false,
                csvFile,
                "data/primary-base/MovieList.csv",
                ";",
                6,
                "yes"
        );

        assertDoesNotThrow(() -> testLoader.run());
        verify(jpaRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should handle CSV with lines having less than minimum columns")
    void shouldHandleCsvWithLinesHavingLessThanMinimumColumns() throws Exception {
        var csvFile = createTempCsvFile("id;year;title;studios;producers;winner\n1;2020;Movie\n2;2021");
        var testLoader = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                false,
                csvFile,
                "data/primary-base/MovieList.csv",
                ";",
                6,
                "yes"
        );

        assertDoesNotThrow(() -> testLoader.run());
        verify(jpaRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should handle CSV with null line")
    void shouldHandleCsvWithNullLine() throws Exception {
        var csvFile = createTempCsvFile("id;year;title;studios;producers;winner\n");
        var testLoader = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                false,
                csvFile,
                "data/primary-base/MovieList.csv",
                ";",
                6,
                "yes"
        );

        assertDoesNotThrow(() -> testLoader.run());
    }

    @Test
    @DisplayName("Should handle CSV with winner field as no")
    void shouldHandleCsvWithWinnerFieldAsNo() throws Exception {
        var csvFile = createTempCsvFile("id;year;title;studios;producers;winner\n1;2020;Movie;Studio;Producer;no");
        var testLoader = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                false,
                csvFile,
                "data/primary-base/MovieList.csv",
                ";",
                6,
                "yes"
        );

        assertDoesNotThrow(() -> testLoader.run());
    }

    @Test
    @DisplayName("Should handle CSV with winner field as empty")
    void shouldHandleCsvWithWinnerFieldAsEmpty() throws Exception {
        var csvFile = createTempCsvFile("id;year;title;studios;producers;winner\n1;2020;Movie;Studio;Producer;");
        var testLoader = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                false,
                csvFile,
                "data/primary-base/MovieList.csv",
                ";",
                6,
                "yes"
        );

        assertDoesNotThrow(() -> testLoader.run());
    }

    @Test
    @DisplayName("Should handle CSV with winner field case insensitive")
    void shouldHandleCsvWithWinnerFieldCaseInsensitive() throws Exception {
        var csvFile = createTempCsvFile("id;year;title;studios;producers;winner\n1;2020;Movie;Studio;Producer;YES\n2;2021;Movie2;Studio2;Producer2;Yes");
        var testLoader = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                false,
                csvFile,
                "data/primary-base/MovieList.csv",
                ";",
                6,
                "yes"
        );

        assertDoesNotThrow(() -> testLoader.run());
    }

    @Test
    @DisplayName("Should handle CSV with empty file")
    void shouldHandleCsvWithEmptyFile() throws Exception {
        var csvFile = createTempCsvFile("");
        var testLoader = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                false,
                csvFile,
                "data/primary-base/MovieList.csv",
                ";",
                6,
                "yes"
        );

        assertDoesNotThrow(() -> testLoader.run());
        verify(jpaRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should handle CSV with only header")
    void shouldHandleCsvWithOnlyHeader() throws Exception {
        var csvFile = createTempCsvFile("id;year;title;studios;producers;winner");
        var testLoader = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                false,
                csvFile,
                "data/primary-base/MovieList.csv",
                ";",
                6,
                "yes"
        );

        assertDoesNotThrow(() -> testLoader.run());
        verify(jpaRepository, never()).saveAll(any());
    }

    @Test
    @DisplayName("Should handle CSV with valid data and winner yes")
    void shouldHandleCsvWithValidDataAndWinnerYes() throws Exception {
        var csvFile = createTempCsvFile("id;year;title;studios;producers;winner\n1;2020;Movie;Studio;Producer;yes");
        var testLoader = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                false,
                csvFile,
                "data/primary-base/MovieList.csv",
                ";",
                6,
                "yes"
        );

        assertDoesNotThrow(() -> testLoader.run());
        verify(jpaRepository, atLeastOnce()).saveAll(any());
    }

    private String createTempCsvFile(String content) throws IOException {
        var tempFile = tempDir.resolve("test-movies-" + System.nanoTime() + ".csv").toFile();
        try (var writer = new FileWriter(tempFile)) {
            writer.write(content);
        }
        var relativePath = "src/main/resources/" + tempFile.getName();
        var targetPath = Paths.get(relativePath);
        Files.createDirectories(targetPath.getParent());
        Files.copy(tempFile.toPath(), targetPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        
        tempFile.delete();
        return tempFile.getName();
    }

    @Test
    @DisplayName("Should call parseField method directly via reflection")
    void shouldCallParseFieldMethodDirectly() throws Exception {
        var csvFile = createTempCsvFile("id;year;title;studios;producers;winner\n1;2020;Test Movie;Studio;Producer;yes");
        var testLoader = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                false,
                csvFile,
                "data/primary-base/MovieList.csv",
                ";",
                6,
                "yes"
        );
        
        var method = CsvDataLoader.class.getDeclaredMethod("parseField", String.class, String.class, int.class);
        method.setAccessible(true);
        
        var result = method.invoke(testLoader, "Test Movie", "title", 1);
        assertEquals("Test Movie", result);
    }

    @Test
    @DisplayName("Should call parseWinner method directly via reflection")
    void shouldCallParseWinnerMethodDirectly() throws Exception {
        var csvFile = createTempCsvFile("id;year;title;studios;producers;winner\n1;2020;Test Movie;Studio;Producer;yes");
        var testLoader = new CsvDataLoader(
                jpaRepository,
                idKeyManagerPort,
                false,
                csvFile,
                "data/primary-base/MovieList.csv",
                ";",
                6,
                "yes"
        );
        
        var method = CsvDataLoader.class.getDeclaredMethod("parseWinner", String.class);
        method.setAccessible(true);
        
        var result = method.invoke(testLoader, "yes");
        assertEquals(Boolean.TRUE, result);
        
        var resultFalse = method.invoke(testLoader, "no");
        assertEquals(Boolean.FALSE, resultFalse);
        
        var resultNull = method.invoke(testLoader, (String) null);
        assertEquals(Boolean.FALSE, resultNull);
    }
}

