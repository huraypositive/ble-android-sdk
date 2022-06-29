package net.huray.omronsdk.ble.enumerate;

public enum OmronDeviceType {
    UNKNOWN_DEVICE(99, OHQDeviceCategory.Unknown, "Unknown ModelName", "Unknown LocalName"),
    BODY_COMPOSITION_MONITOR_HBF_222F(0, OHQDeviceCategory.WeightScale, "체성분계 Omron HBF-222T", "BLEsmart_0001040"),
    BP_MONITOR_HEM_9200T(1, OHQDeviceCategory.BloodPressureMonitor, "혈압계 Omron HEM-9200T", "BLEsmart_0000011"),
    BP_MONITOR_HEM_7155T(2, OHQDeviceCategory.BloodPressureMonitor, "혈압계 Omron HEM-7155T", "BLEsmart_0000056"),
    BP_MONITOR_HEM_7142T(3, OHQDeviceCategory.BloodPressureMonitor, "혈압계 Omron HEM-7142T", "BLESmart_0000059");

    private final int id;
    private final OHQDeviceCategory category;
    private final String modelName;
    private final String localName;

    OmronDeviceType(int number, OHQDeviceCategory category, String modelName, String localName) {
        this.id = number;
        this.category = category;
        this.modelName = modelName;
        this.localName = localName;
    }

    public int getId() {
        return id;
    }

    public OHQDeviceCategory getCategory() {
        return category;
    }

    public String getLocalName() {
        return localName;
    }

    public String getModelName() {
        return modelName;
    }

    public static OmronDeviceType fromId(int id) {
        for (OmronDeviceType device : values()) {
            if (id == device.id) return device;
        }

        return UNKNOWN_DEVICE;
    }

    public static OmronDeviceType fromLocalName(String localName) {
        for (OmronDeviceType device : values()) {
            if (localName.startsWith(device.getLocalName())) return device;
        }

        return OmronDeviceType.UNKNOWN_DEVICE;
    }

    public boolean isHBF222F() {
        return this == BODY_COMPOSITION_MONITOR_HBF_222F;
    }

    public boolean isHEM9200T() {
        return this == BP_MONITOR_HEM_9200T;
    }

    public boolean isHEM7155T() {
        return this == BP_MONITOR_HEM_7155T;
    }

    public boolean isHEM7142T() {
        return this == BP_MONITOR_HEM_7142T;
    }

    public boolean isBloodPressureMonitor() {
        return category == OHQDeviceCategory.BloodPressureMonitor;
    }

    public boolean isWeightDevice() {
        return category == OHQDeviceCategory.WeightScale;
    }

    public boolean isSameModel(String localName) {
        return localName.startsWith(this.localName);
    }
}
