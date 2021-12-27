package net.huray.omronsdk.model;

public class Device {
    private final String name;
    private final String address;

    public Device(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }
}
