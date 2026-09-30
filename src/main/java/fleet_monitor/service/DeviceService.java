package fleet_monitor.service;

import fleet_monitor.model.Device;
import fleet_monitor.model.DeviceStatus;
import fleet_monitor.model.Heartbeat;
import fleet_monitor.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final Clock clock;

    @Autowired
    public DeviceService(DeviceRepository deviceRepository) {
        this(deviceRepository, Clock.systemUTC());
    }

    public DeviceService(DeviceRepository deviceRepository, Clock clock) {
        this.deviceRepository = deviceRepository;
        this.clock = clock;
    }

    public void registerDevice(Device device) {
        if (deviceRepository.existsById(device.getId())) {
            throw new IllegalArgumentException("Device already exists");
        }

        deviceRepository.save(device);
    }

    public void receiveHeartbeat(String id, Heartbeat heartbeat) {
        if (!deviceRepository.existsById(id)) {
            throw new NoSuchElementException("Device not found");
        }

        deviceRepository.updateHeartbeat(id, heartbeat);
    }

    public List<Device> getAllDevices() {
        return deviceRepository.findAll();
    }

    public Device getDevice(String id) {
        return deviceRepository.findById(id);
    }

    public DeviceStatus getStatus(Device device) {
        if (device.getLastHeartbeat() == null) {
            return DeviceStatus.OFFLINE;
        }

        Instant now = Instant.now(clock);

        if (device.getLastHeartbeat().isAfter(now.minusSeconds(30))) {
            return DeviceStatus.ONLINE;
        }

        return DeviceStatus.OFFLINE;
    }

    public Map<String, Integer> getSummary() {
        List<Device> devices = deviceRepository.findAll();

        int online = 0;

        for (Device device : devices) {
            if (getStatus(device) == DeviceStatus.ONLINE) {
                online++;
            }
        }

        Map<String, Integer> summary = new LinkedHashMap<>();
        summary.put("total", devices.size());
        summary.put("online", online);
        summary.put("offline", devices.size() - online);

        return summary;
    }
}