package fleet_monitor.controller;

import fleet_monitor.model.Device;
import fleet_monitor.service.DeviceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import fleet_monitor.model.Heartbeat;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

@RestController
@RequestMapping("/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping
    public ResponseEntity<Void> registerDevice(@RequestBody Device device) {
        deviceService.registerDevice(device);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PostMapping("/{id}/heartbeat")
    public ResponseEntity<Void> receiveHeartbeat(
            @PathVariable String id,
            @RequestBody Heartbeat heartbeat) {

        deviceService.receiveHeartbeat(id, heartbeat);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices() {
        return ResponseEntity.ok(deviceService.getAllDevices());
    }
    @GetMapping("/summary")
    public ResponseEntity<Map<String, Integer>> getSummary() {
        return ResponseEntity.ok(deviceService.getSummary());
    }
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getDevice(@PathVariable String id) {
        Device device = deviceService.getDevice(id);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", device.getId());
        response.put("name", device.getName());
        response.put("status", deviceService.getStatus(device));
        response.put("lastHeartbeat", device.getLastHeartbeat());

        return ResponseEntity.ok(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Void> handleIllegalArgumentException() {
        return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
}