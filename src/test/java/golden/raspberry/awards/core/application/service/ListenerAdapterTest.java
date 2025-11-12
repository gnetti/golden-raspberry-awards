package golden.raspberry.awards.core.application.service;

import golden.raspberry.awards.core.application.port.out.ChangeDetectionPort;
import golden.raspberry.awards.core.application.port.out.DataArchivingPort;
import golden.raspberry.awards.core.application.port.out.InformationEmissionPort;
import golden.raspberry.awards.core.application.port.out.ProcessObservationPort;
import golden.raspberry.awards.core.application.port.out.ResultRecordingPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("ListenerAdapter Tests")
class ListenerAdapterTest {

    private ResultRecordingPort resultRecordingPort;
    private DataArchivingPort dataArchivingPort;
    private ChangeDetectionPort changeDetectionPort;
    private ProcessObservationPort processObservationPort;
    private InformationEmissionPort informationEmissionPort;
    private ListenerAdapter adapter;

    @BeforeEach
    void setUp() {
        resultRecordingPort = mock(ResultRecordingPort.class);
        dataArchivingPort = mock(DataArchivingPort.class);
        changeDetectionPort = mock(ChangeDetectionPort.class);
        processObservationPort = mock(ProcessObservationPort.class);
        informationEmissionPort = mock(InformationEmissionPort.class);
        adapter = new ListenerAdapter(
                resultRecordingPort,
                dataArchivingPort,
                changeDetectionPort,
                processObservationPort,
                informationEmissionPort
        );
    }

    @Test
    @DisplayName("Should record result when result is not null")
    void shouldRecordResultWhenResultIsNotNull() {
        var result = "Test result";

        adapter.recordResult(result);

        verify(resultRecordingPort).record(result);
    }

    @Test
    @DisplayName("Should not record result when result is null")
    void shouldNotRecordResultWhenResultIsNull() {
        adapter.recordResult(null);

        verify(resultRecordingPort, never()).record(any());
    }

    @Test
    @DisplayName("Should archive data when data is not null")
    void shouldArchiveDataWhenDataIsNotNull() {
        var data = "Test data";

        adapter.archiveData(data);

        verify(dataArchivingPort).archive(data);
    }

    @Test
    @DisplayName("Should not archive data when data is null")
    void shouldNotArchiveDataWhenDataIsNull() {
        adapter.archiveData(null);

        verify(dataArchivingPort, never()).archive(any());
    }

    @Test
    @DisplayName("Should preserve data when data is not null")
    void shouldPreserveDataWhenDataIsNotNull() {
        var data = "Test data";

        adapter.preserveData(data);

        verify(dataArchivingPort).preserve(data);
    }

    @Test
    @DisplayName("Should not preserve data when data is null")
    void shouldNotPreserveDataWhenDataIsNull() {
        adapter.preserveData(null);

        verify(dataArchivingPort, never()).preserve(any());
    }

    @Test
    @DisplayName("Should detect changes when both before and after are not null")
    void shouldDetectChangesWhenBothBeforeAndAfterAreNotNull() {
        var before = "Before state";
        var after = "After state";
        var changes = "Detected changes";

        when(changeDetectionPort.detect(before, after)).thenReturn(changes);

        var result = adapter.detectChanges(before, after);

        assertEquals(changes, result);
        verify(changeDetectionPort).detect(before, after);
    }

    @Test
    @DisplayName("Should return null when before is null")
    void shouldReturnNullWhenBeforeIsNull() {
        var result = adapter.detectChanges(null, "After state");

        assertNull(result);
        verify(changeDetectionPort, never()).detect(any(), any());
    }

    @Test
    @DisplayName("Should return null when after is null")
    void shouldReturnNullWhenAfterIsNull() {
        var result = adapter.detectChanges("Before state", null);

        assertNull(result);
        verify(changeDetectionPort, never()).detect(any(), any());
    }

    @Test
    @DisplayName("Should return null when both before and after are null")
    void shouldReturnNullWhenBothBeforeAndAfterAreNull() {
        var result = adapter.detectChanges(null, null);

        assertNull(result);
        verify(changeDetectionPort, never()).detect(any(), any());
    }

    @Test
    @DisplayName("Should observe process when process is not null")
    void shouldObserveProcessWhenProcessIsNotNull() {
        var process = "Test process";

        adapter.observeProcess(process);

        verify(processObservationPort).observe(process);
    }

    @Test
    @DisplayName("Should not observe process when process is null")
    void shouldNotObserveProcessWhenProcessIsNull() {
        adapter.observeProcess(null);

        verify(processObservationPort, never()).observe(any());
    }

    @Test
    @DisplayName("Should emit with session when information and sessionId are not null and sessionId is not blank")
    void shouldEmitWithSessionWhenInformationAndSessionIdAreNotNullAndSessionIdIsNotBlank() {
        var information = "Test information";
        var sessionId = "session123";

        adapter.emitWithSession(information, sessionId);

        verify(informationEmissionPort).withSession(information, sessionId);
    }

    @Test
    @DisplayName("Should not emit with session when information is null")
    void shouldNotEmitWithSessionWhenInformationIsNull() {
        adapter.emitWithSession(null, "session123");

        verify(informationEmissionPort, never()).withSession(any(), any());
    }

    @Test
    @DisplayName("Should not emit with session when sessionId is null")
    void shouldNotEmitWithSessionWhenSessionIdIsNull() {
        adapter.emitWithSession("Test information", null);

        verify(informationEmissionPort, never()).withSession(any(), any());
    }

    @Test
    @DisplayName("Should not emit with session when sessionId is blank")
    void shouldNotEmitWithSessionWhenSessionIdIsBlank() {
        adapter.emitWithSession("Test information", "   ");

        verify(informationEmissionPort, never()).withSession(any(), any());
    }

    @Test
    @DisplayName("Should not emit with session when sessionId is empty")
    void shouldNotEmitWithSessionWhenSessionIdIsEmpty() {
        adapter.emitWithSession("Test information", "");

        verify(informationEmissionPort, never()).withSession(any(), any());
    }

    @Test
    @DisplayName("Should store data when data is not null")
    void shouldStoreDataWhenDataIsNotNull() {
        var data = "Test data";

        adapter.storeData(data);

        verify(resultRecordingPort).store(data);
    }

    @Test
    @DisplayName("Should not store data when data is null")
    void shouldNotStoreDataWhenDataIsNull() {
        adapter.storeData(null);

        verify(resultRecordingPort, never()).store(any());
    }

    @Test
    @DisplayName("Should throw exception when ResultRecordingPort is null")
    void shouldThrowExceptionWhenResultRecordingPortIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new ListenerAdapter(null, dataArchivingPort, changeDetectionPort, processObservationPort, informationEmissionPort));

        assertEquals("ResultRecordingPort cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when DataArchivingPort is null")
    void shouldThrowExceptionWhenDataArchivingPortIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new ListenerAdapter(resultRecordingPort, null, changeDetectionPort, processObservationPort, informationEmissionPort));

        assertEquals("DataArchivingPort cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when ChangeDetectionPort is null")
    void shouldThrowExceptionWhenChangeDetectionPortIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new ListenerAdapter(resultRecordingPort, dataArchivingPort, null, processObservationPort, informationEmissionPort));

        assertEquals("ChangeDetectionPort cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when ProcessObservationPort is null")
    void shouldThrowExceptionWhenProcessObservationPortIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new ListenerAdapter(resultRecordingPort, dataArchivingPort, changeDetectionPort, null, informationEmissionPort));

        assertEquals("ProcessObservationPort cannot be null", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when InformationEmissionPort is null")
    void shouldThrowExceptionWhenInformationEmissionPortIsNull() {
        var exception = assertThrows(NullPointerException.class, () ->
                new ListenerAdapter(resultRecordingPort, dataArchivingPort, changeDetectionPort, processObservationPort, null));

        assertEquals("InformationEmissionPort cannot be null", exception.getMessage());
    }
}

