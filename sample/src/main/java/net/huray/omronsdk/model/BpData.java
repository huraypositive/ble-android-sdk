package net.huray.omronsdk.model;

public class BpData {
    final String timeStamp;
    final float sbp;
    final float dbp;
    final float pulseRate;

    public BpData(String timeStamp, float sbp, float dbp, float pulseRate) {
        this.timeStamp = timeStamp;
        this.sbp = sbp;
        this.dbp = dbp;
        this.pulseRate = pulseRate;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public float getSbp() {
        return sbp;
    }

    public float getDbp() {
        return dbp;
    }

    public float getPulseRate() {
        return pulseRate;
    }
}
