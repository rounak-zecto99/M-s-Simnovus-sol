import json
import threading
import time
import urllib.request
from datetime import datetime, timezone

BASE_URL = "http://localhost:8080"

devices = {
    f"device-{i:02d}": f"Lab Device {i:02d}"
    for i in range(1, 6)
}

stop_events = {}
threads = {}


def send_request(method, url, data=None):
    body = None

    if data is not None:
        body = json.dumps(data).encode("utf-8")

    request = urllib.request.Request(
        url,
        data=body,
        method=method,
        headers={"Content-Type": "application/json"}
    )

    try:
        with urllib.request.urlopen(request) as response:
            return response.status
    except Exception as e:
        print(f"Request failed: {e}")
        return None


def register_device(device_id, name):
    status = send_request(
        "POST",
        f"{BASE_URL}/devices",
        {
            "id": device_id,
            "name": name
        }
    )

    if status == 201:
        print(f"[REGISTERED] {device_id}")
    elif status == 409:
        print(f"[EXISTS] {device_id}")
    else:
        print(f"[REGISTER FAILED] {device_id} -> {status}")


def heartbeat_loop(device_id):
    stop_event = stop_events[device_id]

    while not stop_event.is_set():
        timestamp = datetime.now(timezone.utc).isoformat()

        status = send_request(
            "POST",
            f"{BASE_URL}/devices/{device_id}/heartbeat",
            {
                "timestamp": timestamp,
                "status": "OK"
            }
        )

        if status == 204:
            print(f"[HEARTBEAT] {device_id}")
        else:
            print(f"[HEARTBEAT FAILED] {device_id} -> {status}")

        stop_event.wait(5)


def start_device(device_id):
    if device_id in threads and threads[device_id].is_alive():
        print(f"{device_id} is already running.")
        return

    stop_events[device_id] = threading.Event()

    thread = threading.Thread(
        target=heartbeat_loop,
        args=(device_id,),
        daemon=True
    )

    threads[device_id] = thread
    thread.start()

    print(f"[STARTED] {device_id}")


def stop_device(device_id):
    if device_id not in stop_events:
        print(f"{device_id} is not running.")
        return

    stop_events[device_id].set()
    print(f"[STOPPED] {device_id}")


def main():
    print("Registering devices...")

    for device_id, name in devices.items():
        register_device(device_id, name)

    print()
    print("Starting all devices...")

    for device_id in devices:
        start_device(device_id)

    print()
    print("Commands:")
    print("  stop device-01")
    print("  start device-01")
    print("  status")
    print("  quit")
    print()

    while True:
        command = input("> ").strip()

        if command == "quit":
            for device_id in devices:
                stop_device(device_id)
            break

        parts = command.split()

        if len(parts) == 2:
            action, device_id = parts

            if device_id not in devices:
                print("Unknown device.")
                continue

            if action == "stop":
                stop_device(device_id)

            elif action == "start":
                start_device(device_id)

            else:
                print("Unknown command.")

        elif command == "status":
            for device_id, thread in threads.items():
                state = "RUNNING" if thread.is_alive() else "STOPPED"
                print(f"{device_id}: {state}")

        else:
            print("Unknown command.")


if __name__ == "__main__":
    main()