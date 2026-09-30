package fleet_monitor.service;

import fleet_monitor.model.Device;
import fleet_monitor.model.DeviceStatus;
import fleet_monitor.model.Heartbeat;
import fleet_monitor.repository.DeviceRepository;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DeviceServiceTest {

    private final DeviceRepository repository = mock(DeviceRepository.class);

    private final Instant now =
            Instant.parse("2026-09-30T12:00:00Z");

    private final Clock clock =
            Clock.fixed(now, ZoneOffset.UTC);

    private final DeviceService service =
            new DeviceService(repository, clock);

    @Test
    void shouldRegisterDevice() {
        Device device = new Device("device-01", "Lab Device 01");

        when(repository.existsById("device-01")).thenReturn(false);

        service.registerDevice(device);

        verify(repository).save(device);
    }

    @Test
    void shouldRejectDuplicateDevice() {
        Device device = new Device("device-01", "Lab Device 01");

        when(repository.existsById("device-01")).thenReturn(true);

        assertThrows(
                IllegalArgumentException.class,
                () -> service.registerDevice(device)
        );

        verify(repository, never()).save(device);
    }

    @Test
    void shouldStoreHeartbeat() {
        Heartbeat heartbeat = new Heartbeat();
        heartbeat.setTimestamp(now);
        heartbeat.setStatus("OK");

        when(repository.existsById("device-01")).thenReturn(true);

        service.receiveHeartbeat("device-01", heartbeat);

        verify(repository).updateHeartbeat("device-01", heartbeat);
    }

    @Test
    void heartbeatWithin30SecondsShouldBeOnline() {
        Device device = new Device(
                "device-01",
                "Lab Device 01"
        );

        device.setLastHeartbeat(
                now.minusSeconds(20)
        );

        assertEquals(
                DeviceStatus.ONLINE,
                service.getStatus(device)
        );
    }

    @Test
    void heartbeatOlderThan30SecondsShouldBeOffline() {
        Device device = new Device(
                "device-01",
                "Lab Device 01"
        );

        device.setLastHeartbeat(
                now.minusSeconds(31)
        );

        assertEquals(
                DeviceStatus.OFFLINE,
                service.getStatus(device)
        );
    }

    @Test
    void noHeartbeatShouldBeOffline() {
        Device device = new Device(
                "device-01",
                "Lab Device 01"
        );

        assertEquals(
                DeviceStatus.OFFLINE,
                service.getStatus(device)
        );
    }
}