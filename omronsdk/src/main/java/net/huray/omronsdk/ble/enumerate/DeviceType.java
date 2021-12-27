package net.huray.omronsdk.ble.enumerate;

public enum DeviceType {
    OMRON_WEIGHT(0),
    OMRON_BP(1);

    private final int number;

    DeviceType(int number) {
        this.number = number;
    }

    public static DeviceType getDeviceType(int number) {
        if (number == 0) return OMRON_WEIGHT;
        return OMRON_BP;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        if (this == OMRON_WEIGHT) return "오므론 체성분계 Omron HBF-222T";
        return "오므론 혈압계 Omron HEM-9200T";
    }

    public OHQDeviceCategory getOmronDeviceCategory() {
        if (isWeightDevice()) {
            return OHQDeviceCategory.BodyCompositionMonitor;
        }

        if (isBpDevice()) {
            return OHQDeviceCategory.BloodPressureMonitor;
        }

        throw new IllegalStateException("Unknown Device!!");
    }

    public boolean isWeightDevice() {
        return this == OMRON_WEIGHT;
    }

    public boolean isBpDevice() {
        return this == OMRON_BP;
    }
}
