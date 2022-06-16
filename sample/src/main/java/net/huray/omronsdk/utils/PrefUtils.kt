package net.huray.omronsdk.utils

import android.annotation.SuppressLint
import android.content.Context
import net.huray.omronsdk.App.Companion.instance
import net.huray.omronsdk.ble.entity.WeightDeviceInfo

@SuppressLint("CommitPrefEdits")
object PrefUtils {
    private val SHARED_PREFERENCES =
        instance.getSharedPreferences(Const.PREF_NAME, Context.MODE_PRIVATE)

    /**
     * 혈압계 HEM-9200T
     */
    fun saveBpMonitorHem9200tDeviceAddress(deviceAddress: String?) {
        SHARED_PREFERENCES
            .edit()
            .putString(Const.PREF_HEM_9200T_ADDRESS, deviceAddress)
            .apply()
    }

    fun getBpMonitorHem9200tAddress(): String? {
        return SHARED_PREFERENCES.getString(Const.PREF_HEM_9200T_ADDRESS, null)
    }

    /**
     * 혈압계 HEM-7155T
     */
    fun saveBpMonitorHem7155tAddress(deviceAddress: String?) {
        SHARED_PREFERENCES
            .edit()
            .putString(Const.PREF_HEM_7155T_ADDRESS, deviceAddress)
            .apply()
    }

    fun getBpMonitorHem7155tAddress(): String? {
        return SHARED_PREFERENCES.getString(Const.PREF_HEM_7155T_ADDRESS, null)
    }

    /**
     * 체성분계 HBF-222F
     */
    fun saveBodyCompositionMonitorHbf222tAddress(deviceAddress: String) {
        SHARED_PREFERENCES
            .edit()
            .putString(Const.PREF_HBF_222T_ADDRESS, deviceAddress)
            .apply()
    }

    fun getBodyCompositionMonitorHbf222tAddress(): String? {
        return SHARED_PREFERENCES.getString(Const.PREF_HBF_222T_ADDRESS, null)
    }

    fun saveBodyCompositionMonitorHbf222tUserIndex(userIndex: Int?) {
        SHARED_PREFERENCES
            .edit()
            .putInt(Const.PREF_HBF_222T_USER_INDEX, userIndex!!)
            .apply()
    }

    fun getBodyCompositionMonitorHbf222tUserIndex(): Int {
        return SHARED_PREFERENCES.getInt(Const.PREF_HBF_222T_USER_INDEX, 0)
    }

    fun saveBodyCompositionMonitorHbf222tSequenceNumber(seqNum: Int) {
        SHARED_PREFERENCES
            .edit()
            .putInt(Const.PREF_HBF_222T_SEQUENCE_NUMBER, seqNum)
            .apply()
    }

    fun getBodyCompositionMonitorHbf222tSequenceNumber(): Int {
        return SHARED_PREFERENCES.getInt(Const.PREF_HBF_222T_SEQUENCE_NUMBER, 0)
    }

    fun saveOmronBleDataBaseIncrementKey(key: Long) {
        SHARED_PREFERENCES
            .edit()
            .putLong(Const.PREF_HBF_222T_DB_CHANGE_KEY, key)
            .apply()
    }

    fun getOmronBleDataBaseIncrementKey(): Long {
        return SHARED_PREFERENCES.getLong(Const.PREF_HBF_222T_DB_CHANGE_KEY, 0)
    }

    fun getBodyCompositionMonitorHbf222tTransferInfo(): WeightDeviceInfo {
        val address = getBodyCompositionMonitorHbf222tAddress()
        val userIndex = getBodyCompositionMonitorHbf222tUserIndex()
        val seqNumber = getBodyCompositionMonitorHbf222tSequenceNumber()
        val incrementKey = getOmronBleDataBaseIncrementKey()
        return WeightDeviceInfo.newInstanceForTransfer(
            Const.demoUser,
            address,
            userIndex,
            seqNumber,
            incrementKey
        )
    }

    fun removeOmronWeightDeice() {
        SHARED_PREFERENCES
            .edit()
            .putString(Const.PREF_HBF_222T_ADDRESS, null)
            .putInt(Const.PREF_HBF_222T_USER_INDEX, -1)
            .putInt(Const.PREF_HBF_222T_SEQUENCE_NUMBER, -1)
            .apply()
    }
}