package fleet_monitor.model;

import java.time.Instant;

public class Device {

    private String id;
    private String name;
    private Instant lastHeartbeat;
    private String lastHeartbeatStatus;

    public Device() {
    }

    public Device(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getLastHeartbeat() {
        return lastHeartbeat;
    }

    public void setLastHeartbeat(Instant lastHeartbeat) {
        this.lastHeartbeat = lastHeartbeat;
    }

    public String getLastHeartbeatStatus() {
        return lastHeartbeatStatus;
    }

    public void setLastHeartbeatStatus(String lastHeartbeatStatus) {
        this.lastHeartbeatStatus = lastHeartbeatStatus;
    }
}