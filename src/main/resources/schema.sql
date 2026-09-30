CREATE TABLE devices (
                         id VARCHAR(100) PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         last_heartbeat TIMESTAMP WITH TIME ZONE,
                         last_heartbeat_status VARCHAR(20)
);