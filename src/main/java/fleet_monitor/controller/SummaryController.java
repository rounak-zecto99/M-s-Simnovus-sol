package fleet_monitor.controller;

import fleet_monitor.service.DeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class SummaryController {

    private final DeviceService deviceService;

    public SummaryController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Integer>> getSummary() {
        return ResponseEntity.ok(deviceService.getSummary());
    }
}