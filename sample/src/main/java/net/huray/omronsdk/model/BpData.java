package net.huray.omronsdk.model;

import java.math.BigDecimal;

public class BpData {
    final String timeStamp;
    final BigDecimal sbp;
    final BigDecimal dbp;
    final BigDecimal pulseRate;

    public BpData(String timeStamp, BigDecimal sbp, BigDecimal dbp, BigDecimal pulseRate) {
        this.timeStamp = timeStamp;
        this.sbp = sbp;
        this.dbp = dbp;
        this.pulseRate = pulseRate;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public BigDecimal getSbp() {
        return sbp;
    }

    public BigDecimal getDbp() {
        return dbp;
    }

    public BigDecimal getPulseRate() {
        return pulseRate;
    }
}
