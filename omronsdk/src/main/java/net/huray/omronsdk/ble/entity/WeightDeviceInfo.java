package net.huray.omronsdk.ble.entity;

import net.huray.omronsdk.ble.enumerate.OHQSessionType;
import net.huray.omronsdk.ble.enumerate.OHQUserDataKey;

import java.util.Map;

public class WeightDeviceInfo {
    private final Map<OHQUserDataKey, Object> userData;
    private final String address;
    private final int index;
    private int sequenceNumber = -1;
    private long incrementKey = 0;
    private final OHQSessionType sessionType;

    public static WeightDeviceInfo newInstanceForRegister(Map<OHQUserDataKey, Object> userData,
                                                          String address,
                                                          int index) {
        return new WeightDeviceInfo(userData, address, index);
    }

    public static WeightDeviceInfo newInstanceForTransfer(Map<OHQUserDataKey, Object> userData,
                                                          String address,
                                                          int index,
                                                          int sequenceNumber,
                                                          long incrementKey) {
        return new WeightDeviceInfo(userData, address, index, sequenceNumber, incrementKey);
    }

    private WeightDeviceInfo(Map<OHQUserDataKey, Object> userData, String address, int index) {
        this.userData = userData;
        this.address = address;
        this.index = index;
        this.sessionType = OHQSessionType.REGISTER;
    }

    private WeightDeviceInfo(Map<OHQUserDataKey, Object> userData, String address, int index,
                             int sequenceNumber, long incrementKey) {
        this.userData = userData;
        this.address = address;
        this.index = index;
        this.sequenceNumber = sequenceNumber;
        this.incrementKey = incrementKey;
        this.sessionType = OHQSessionType.TRANSFER;
    }

    public Map<OHQUserDataKey, Object> getUserData() {
        return userData;
    }

    public String getAddress() {
        return address;
    }

    public int getIndex() {
        return index;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public long getIncrementKey() {
        return incrementKey;
    }

    public OHQSessionType getSessionType() {
        return sessionType;
    }
}
