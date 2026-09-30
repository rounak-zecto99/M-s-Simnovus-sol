package fleet_monitor.model;

import java.time.Instant;

public class Heartbeat {

    private Instant timestamp;
    private String status;

    public Heartbeat() {
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}