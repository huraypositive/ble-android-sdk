package net.huray.omronsdk.utils;

import androidx.annotation.Nullable;

import net.huray.omronsdk.App;
import net.huray.omronsdk.ble.entity.WeightDeviceInfo;

public class PrefUtils {

    /**
     * 혈압계 HEM-9200T
     * */
    public static void setBpMonitor_HEM9200T_DeviceAddress(String deviceAddress) {
        App.getInstance().getSecurePreferences()
                .edit()
                .putString(Const.PREF_HEM_9200T_ADDRESS, deviceAddress)
                .apply();
    }

    public static String getBpMonitor_HEM9200T_Address() {
        return App.getInstance().getSecurePreferences()
                .getString(Const.PREF_HEM_9200T_ADDRESS, null);
    }

    /**
     * 혈압계 HEM-7155T
     * */
    public static void setBpMonitor_HEM7155T_Address(String deviceAddress) {
        App.getInstance().getSecurePreferences()
                .edit()
                .putString(Const.PREF_HEM_7155T_ADDRESS, deviceAddress)
                .apply();
    }

    public static String getBpMonitor_HEM7155T_Address() {
        return App.getInstance().getSecurePreferences()
                .getString(Const.PREF_HEM_7155T_ADDRESS, null);
    }

    /**
     * 체성분계 HBF-222F
     * */
    public static void setBodyCompositionMonitor_HBF222T_Address(String deviceAddress) {
        App.getInstance().getSecurePreferences()
                .edit()
                .putString(Const.PREF_HBF_222T_ADDRESS, deviceAddress)
                .apply();
    }

    @Nullable
    public static String getBodyCompositionMonitor_HBF222T_Address() {
        return App.getInstance().getSecurePreferences()
                .getString(Const.PREF_HBF_222T_ADDRESS, null);
    }

    public static void setBodyCompositionMonitor_HBF222T_UserIndex(Integer userIndex) {
        App.getInstance().getSecurePreferences()
                .edit()
                .putInt(Const.PREF_HBF_222T_USER_INDEX, userIndex)
                .apply();
    }

    public static Integer getBodyCompositionMonitor_HBF222T_UserIndex() {
        return App.getInstance().getSecurePreferences()
                .getInt(Const.PREF_HBF_222T_USER_INDEX, 0);
    }

    public static void setBodyCompositionMonitor_HBF222T_SequenceNumber(int seqNum) {
        App.getInstance().getSecurePreferences()
                .edit()
                .putInt(Const.PREF_HBF_222T_SEQUENCE_NUMBER, seqNum)
                .apply();
    }

    public static int getBodyCompositionMonitor_HBF222T_SequenceNumber() {
        return App.getInstance().getSecurePreferences()
                .getInt(Const.PREF_HBF_222T_SEQUENCE_NUMBER, 0);
    }

    public static void setOmronBleDataBaseIncrementKey(long key) {
        App.getInstance().getSecurePreferences()
                .edit()
                .putLong(Const.PREF_HBF_222T_DB_CHANGE_KEY, key)
                .apply();
    }

    public static long getOmronBleDataBaseIncrementKey() {
        return App.getInstance().getSecurePreferences()
                .getLong(Const.PREF_HBF_222T_DB_CHANGE_KEY, 0);
    }

    public static WeightDeviceInfo getBodyCompositionMonitor_HBF222T_TransferInfo() {
        String address = getBodyCompositionMonitor_HBF222T_Address();
        int userIndex = getBodyCompositionMonitor_HBF222T_UserIndex();
        int seqNumber = getBodyCompositionMonitor_HBF222T_SequenceNumber();
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
                .putString(Const.PREF_HBF_222T_ADDRESS, null)
                .putInt(Const.PREF_HBF_222T_USER_INDEX, -1)
                .putInt(Const.PREF_HBF_222T_SEQUENCE_NUMBER, -1)
                .apply();
    }
}
