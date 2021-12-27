package net.huray.omronsdk.ble.enumerate;

public enum OHQCompletionReason {
    Disconnected,
    Canceled,
    PoweredOff,
    Busy,
    InvalidDeviceIdentifier,
    FailedToConnect,
    FailedToTransfer,
    FailedToRegisterUser,
    FailedToAuthenticateUser,
    FailedToDeleteUser,
    FailedToSetUserData,
    OperationNotSupported,
    ConnectionTimedOut;

    public boolean isCanceled() {
        return this == Canceled;
    }

    public boolean isFailedToConnect() {
        return this == FailedToConnect;
    }

    public boolean isFailedToTransfer() {
        return this == FailedToTransfer;
    }

    public boolean isFailedToRegisterUser() {
        return this == FailedToRegisterUser;
    }

    public boolean isTimeOut() {
        return this == ConnectionTimedOut;
    }
}
