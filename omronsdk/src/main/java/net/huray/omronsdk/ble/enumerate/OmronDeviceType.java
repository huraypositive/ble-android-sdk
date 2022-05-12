package net.huray.omronsdk.ble.enumerate;

public enum OmronDeviceType {
    UNKNOWN_DEVICE(99),
    BODY_COMPOSITION_MONITOR_HBF_222F(0),
    BP_MONITOR_HEM_9200T(1),
    BP_MONITOR_HEM_7155T(2);

    private final int number;

    OmronDeviceType(int number) {
        this.number = number;
    }

    public static OmronDeviceType getDeviceType(int number) {
        if (number == 0) return BODY_COMPOSITION_MONITOR_HBF_222F;
        if (number == 1) return BP_MONITOR_HEM_9200T;
        if (number == 2) return BP_MONITOR_HEM_7155T;
        return UNKNOWN_DEVICE;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        if (this == BODY_COMPOSITION_MONITOR_HBF_222F) return "오므론 체성분계 Omron HBF-222T";
        if (this == BP_MONITOR_HEM_9200T) return "오므론 혈압계 Omron HEM-9200T";
        if (this == BP_MONITOR_HEM_7155T) return "오므론 혈압계 Omron HEM-7155T";
        return "미등록 기기";
    }

    public OHQDeviceCategory getOmronDeviceCategory() {
        if (isHBF222F()) {
            return OHQDeviceCategory.WeightScale;
        }

        if (isHEM9200T() || isHEM7155T()) {
            return OHQDeviceCategory.BloodPressureMonitor;
        }

        throw new IllegalStateException("Unknown Device!!");
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
}
