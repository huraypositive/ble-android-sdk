package net.huray.omronsdk.utils;

import androidx.annotation.Nullable;

import net.huray.omronsdk.App;
import net.huray.omronsdk.ble.entity.WeightDeviceInfo;

public class PrefUtils {

    public static void setOmronBleBpDeviceAddress(@Nullable String deviceAddress) {
        App.getInstance().getSecurePreferences()
                .edit()
                .putString(Const.PREF_OMRON_BP_DEVICE_ADDRESS, deviceAddress)
                .apply();
    }

    @Nullable
    public static String getOmronBleBpDeviceAddress() {
        return App.getInstance().getSecurePreferences()
                .getString(Const.PREF_OMRON_BP_DEVICE_ADDRESS, null);
    }

    public static void setOmronBleWeightDeviceAddress(@Nullable String deviceAddress) {
        App.getInstance().getSecurePreferences()
                .edit()
                .putString(Const.PREF_OMRON_WEIGHT_DEVICE_ADDRESS, deviceAddress)
                .apply();
    }

    @Nullable
    public static String getOmronBleWeightDeviceAddress() {
        return App.getInstance().getSecurePreferences()
                .getString(Const.PREF_OMRON_WEIGHT_DEVICE_ADDRESS, null);
    }

    public static void setOmronBleWeightDeviceUserIndex(Integer userIndex) {
        App.getInstance().getSecurePreferences()
                .edit()
                .putInt(Const.PREF_OMRON_WEIGHT_DEVICE_USER_INDEX, userIndex)
                .apply();
    }

    public static Integer getOmronBleWeightDeviceUserIndex() {
        return App.getInstance().getSecurePreferences()
                .getInt(Const.PREF_OMRON_WEIGHT_DEVICE_USER_INDEX, 0);
    }

    public static void setOmronBleWeightDeviceSequenceNumber(int seqNum) {
        App.getInstance().getSecurePreferences()
                .edit()
                .putInt(Const.PREF_OMRON_WEIGHT_DEVICE_SEQ, seqNum)
                .apply();
    }

    public static int getOmronBleWeightDeviceSequenceNumber() {
        return App.getInstance().getSecurePreferences()
                .getInt(Const.PREF_OMRON_WEIGHT_DEVICE_SEQ, 0);
    }

    public static void setOmronBleDataBaseIncrementKey(long key) {
        App.getInstance().getSecurePreferences()
                .edit()
                .putLong(Const.PREF_OMRON_WEIGHT_DEVICE_DB_CHANGE_KEY, key)
                .apply();
    }

    public static long getOmronBleDataBaseIncrementKey() {
        return App.getInstance().getSecurePreferences()
                .getLong(Const.PREF_OMRON_WEIGHT_DEVICE_DB_CHANGE_KEY, 0);
    }

    public static WeightDeviceInfo getOmronWeightTransferInfo() {
        String address = getOmronBleWeightDeviceAddress();
        int userIndex = getOmronBleWeightDeviceUserIndex();
        int seqNumber = getOmronBleWeightDeviceSequenceNumber();
        long incrementKey = getOmronBleDataBaseIncrementKey();

        return WeightDeviceInfo.newInstanceForTransfer(
                Const.getDemoUser(),
                address,
                userIndex,
                seqNumber,
                incrementKey);
    }

    public static void removeOmronWeightDeice() {
        App.getInstance().getSecurePreferences()
                .edit()
                .putString(Const.PREF_OMRON_WEIGHT_DEVICE_ADDRESS, null)
                .putInt(Const.PREF_OMRON_WEIGHT_DEVICE_USER_INDEX, -1)
                .putInt(Const.PREF_OMRON_WEIGHT_DEVICE_SEQ, -1)
                .apply();
    }
}
