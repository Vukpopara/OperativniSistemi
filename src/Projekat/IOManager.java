package Projekat;

import java.util.HashMap;
import java.util.Map;

public class IOManager {
    private final Map<String, IODevice> devices;

    public IOManager() {
        this.devices = new HashMap<>();
    }

    public void addDevice(String name, IODevice device) {
        devices.put(name, device);
    }

    public IODevice getDevice(String name) {
        return devices.get(name);
    }

    public void submitOperation(String deviceName, IOOperation operation) {
        IODevice device = devices.get(deviceName);
        if (device != null) {
            device.addOperation(operation);
        } else {
            System.err.println("Greška: I/O uređaj '" + deviceName + "' nije pronađen.");
        }
    }

    public void tick() {
        for (IODevice device : devices.values()) {
            device.tick();
        }
    }
}