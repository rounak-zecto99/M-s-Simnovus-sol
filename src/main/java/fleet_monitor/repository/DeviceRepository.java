package fleet_monitor.repository;

import fleet_monitor.model.Device;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import fleet_monitor.model.Heartbeat;
import java.util.List;

@Repository
public class DeviceRepository {

    private final JdbcTemplate jdbcTemplate;

    public DeviceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void save(Device device) {
        String sql = """
                INSERT INTO devices (id, name)
                VALUES (?, ?)
                """;

        jdbcTemplate.update(
                sql,
                device.getId(),
                device.getName()
        );
    }
    public boolean existsById(String id) {
        String sql = "SELECT COUNT(*) FROM devices WHERE id = ?";

        Integer count = jdbcTemplate.queryForObject(
                sql,
                Integer.class,
                id
        );

        return count != null && count > 0;
    }
    public void updateHeartbeat(String id, Heartbeat heartbeat) {
        String sql = """
            UPDATE devices
            SET last_heartbeat = ?,
                last_heartbeat_status = ?
            WHERE id = ?
            """;

        jdbcTemplate.update(
                sql,
                heartbeat.getTimestamp(),
                heartbeat.getStatus(),
                id
        );
    }
    public List<Device> findAll() {
        String sql = """
            SELECT id, name, last_heartbeat, last_heartbeat_status
            FROM devices
            """;

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Device device = new Device();
            device.setId(rs.getString("id"));
            device.setName(rs.getString("name"));

            if (rs.getTimestamp("last_heartbeat") != null) {
                device.setLastHeartbeat(
                        rs.getTimestamp("last_heartbeat").toInstant()
                );
            }

            device.setLastHeartbeatStatus(
                    rs.getString("last_heartbeat_status")
            );

            return device;
        });
    }
    public Device findById(String id) {
        String sql = """
            SELECT id, name, last_heartbeat, last_heartbeat_status
            FROM devices
            WHERE id = ?
            """;

        return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
            Device device = new Device();
            device.setId(rs.getString("id"));
            device.setName(rs.getString("name"));

            if (rs.getTimestamp("last_heartbeat") != null) {
                device.setLastHeartbeat(
                        rs.getTimestamp("last_heartbeat").toInstant()
                );
            }

            device.setLastHeartbeatStatus(
                    rs.getString("last_heartbeat_status")
            );

            return device;
        }, id);
    }
}