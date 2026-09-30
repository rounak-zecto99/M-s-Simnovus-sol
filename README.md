# Mini Device Fleet Monitor

## 1. What the Project Does

This project is a **Spring Boot REST API** for monitoring a fleet of devices.

It supports:

* Registering devices
* Receiving device heartbeats
* Checking individual device status
* Viewing all devices
* Viewing fleet-wide online/offline summary

> **Status Logic:** A device is considered `ONLINE` if its latest heartbeat was received within the last 30 seconds. Otherwise, it is considered `OFFLINE`.

A Python simulator is included to simulate 5 devices sending heartbeats every 5 seconds.

---

## 2. Design / Architecture

The application follows a simple layered architecture:

```text
REST Controller
      ↓
Service Layer
      ↓
Repository Layer
      ↓
H2 Database

```

* **Controller:** Handles REST API requests and responses.
* **Service:** Contains device registration, heartbeat processing, status calculation, and summary logic.
* **Repository:** Uses Spring JDBC to access the database.
* **Database:** H2 in-memory database storing device and latest heartbeat information.
* **Simulator:** Python script that simulates multiple devices sending heartbeats.

Device status is calculated dynamically from the latest heartbeat rather than being stored as a separate status value.

---

## 3. Prerequisites

* Java 21
* Python 3.x
* Git

*Note: Maven does not need to be installed separately because the project includes the Maven Wrapper.*

### Check Java

```bash
java -version

```

### Check Python

* **Linux/macOS:**
```bash
python --version

```


* **Windows:**
```cmd
py --version

```



---

## 4. Build the Application

Clone the repository and enter the project directory:

```bash
git clone <repository-url>
cd fleet-monitor

```

Build the project:

* **Windows:**
```cmd
.\mvnw.cmd clean package

```


* **Linux/macOS:**
```bash
./mvnw clean package

```



---

## 5. Run the Application

* **Windows:**
```cmd
.\mvnw.cmd spring-boot:run

```


* **Linux/macOS:**
```bash
./mvnw spring-boot:run

```



The application runs at: `http://localhost:8080`

*Note: The application uses an H2 in-memory database, so data is reset when the application restarts.*

---

## 6. Run the Simulator

Make sure the Spring Boot application is running first, then open another terminal in the project root.

* **Windows:**
```cmd
py simulator\simulator.py

```


* **Linux/macOS:**
```bash
python3 simulator/simulator.py

```



The simulator registers 5 devices and sends heartbeats every 5 seconds.

### Available Commands

* `stop device-01`
* `start device-01`
* `status`
* `quit`

**Example:**

```text
stop device-03

```

After more than 30 seconds without heartbeats, `device-03` should become `OFFLINE`.

---

## 7. Run the Tests

* **Windows:**
```cmd
.\mvnw.cmd test

```


* **Linux/macOS:**
```bash
./mvnw test

```



### Test Coverage

* Device registration
* Duplicate registration
* Heartbeat processing
* Devices without heartbeats
* Online status within 30 seconds
* Offline status after 30 seconds

The service uses an injected `Clock` so the time-dependent status logic can be tested deterministically.

---

## 8. Example API Requests

### Register Device

`POST http://localhost:8080/devices`

* **Content-Type:** `application/json`

```json
{
  "id": "device-01",
  "name": "Lab Device 01"
}

```

* **Response:** `201 Created`

---

### Send Heartbeat

`POST http://localhost:8080/devices/device-01/heartbeat`

* **Content-Type:** `application/json`

```json
{
  "timestamp": "2026-09-30T11:38:00Z",
  "status": "OK"
}

```

* **Response:** `204 No Content`

---

### Get All Devices

`GET http://localhost:8080/devices`

---

### Get Device

`GET http://localhost:8080/devices/device-01`

**Example Response:**

```json
{
  "id": "device-01",
  "name": "Lab Device 01",
  "status": "ONLINE",
  "lastHeartbeat": "2026-09-30T11:38:00Z"
}

```

---

### Get Fleet Summary

`GET http://localhost:8080/summary`

**Example Response:**

```json
{
  "total": 5,
  "online": 4,
  "offline": 1
}

```

---

## 9. Assumptions

* Device IDs are unique.
* A device must be registered before sending a heartbeat.
* A device with no heartbeat is considered `OFFLINE`.
* A heartbeat within the last 30 seconds means the device is `ONLINE`.
* Heartbeat timestamps use ISO-8601 format with UTC information.
* Only the latest heartbeat is required to determine the current status.
* H2 in-memory storage is sufficient for this assessment.

---

## 10. Known Limitations

* Device data is lost when the application restarts because H2 is in-memory.
* Only the latest heartbeat is stored; heartbeat history is not maintained.
* Authentication and authorization are not implemented.
* Error responses are basic and could be standardized further.
* The simulator is intended for local testing rather than production use.
* The application is designed as a single service and does not address distributed scaling.

---

## 11. Improvements With One Additional Day

With another day, I would:

1. Replace H2 with PostgreSQL for persistent storage.
2. Add request validation and structured error responses.
3. Add controller/integration tests.
4. Store heartbeat history for monitoring and analysis.
5. Add logging and application metrics.
6. Add Docker support.
7. Improve scalability for larger device fleets.

---

## AI Usage

ChatGPT was used as a development assistant for architecture discussions, debugging, reviewing implementation approaches, testing edge cases, and documentation.

One improvement made during development was using an injected Java `Clock` for the heartbeat status logic, allowing the 30-second timeout behavior to be tested deterministically.

Before submission, I personally verified that the application builds, tests pass, the REST APIs work, and the simulator can demonstrate the `ONLINE` to `OFFLINE` transition.