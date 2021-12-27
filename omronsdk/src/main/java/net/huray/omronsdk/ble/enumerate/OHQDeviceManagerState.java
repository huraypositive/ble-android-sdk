package net.huray.omronsdk.ble.enumerate;


import androidx.annotation.NonNull;

import net.huray.omronsdk.androidcorebluetooth.CBManagerState;

public enum OHQDeviceManagerState {
    Unknown(CBManagerState.Unknown),
    Unsupported(CBManagerState.Unsupported),
    Unauthorized(CBManagerState.Unauthorized),
    PoweredOff(CBManagerState.PoweredOff),
    PoweredOn(CBManagerState.PoweredOn);
    @NonNull
    private CBManagerState mValue;

    OHQDeviceManagerState(@NonNull CBManagerState value) {
        mValue = value;
    }

    @NonNull
    public static OHQDeviceManagerState valueOf(@NonNull CBManagerState value) {
        for (OHQDeviceManagerState type : values()) {
            if (type.value() == value) {
                return type;
            }
        }
        return OHQDeviceManagerState.Unknown;
    }

    @NonNull
    public CBManagerState value() {
        return mValue;
    }
}
