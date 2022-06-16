package net.huray.omronsdk.ui.transfer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import net.huray.omronsdk.OmronDeviceManager
import net.huray.omronsdk.ble.entity.SessionData
import net.huray.omronsdk.ble.enumerate.OHQCompletionReason
import net.huray.omronsdk.ble.enumerate.OHQMeasurementRecordKey
import net.huray.omronsdk.ble.enumerate.OHQSessionType
import net.huray.omronsdk.ble.enumerate.OmronDeviceType
import net.huray.omronsdk.model.DeviceConnectionState
import net.huray.omronsdk.model.OmronHealthData
import net.huray.omronsdk.utils.Const
import net.huray.omronsdk.utils.PrefUtils
import java.math.BigDecimal
import java.util.stream.Collectors

class DeviceTransferViewModel(
    private val omronDeviceType: OmronDeviceType
) : ViewModel(), OmronDeviceManager.TransferListener {

    private val _loadingEvent = MutableLiveData<Boolean>()
    val loadingEvent: LiveData<Boolean> get() = _loadingEvent

    private val _connectionEvent = MutableLiveData<DeviceConnectionState>()
    val connectionEvent: LiveData<DeviceConnectionState> get() = _connectionEvent

    private val _dataTransferEvent = MutableLiveData<List<OmronHealthData>>()
    val dataTransferEvent: LiveData<List<OmronHealthData>> get() = _dataTransferEvent

    private val _noDataEvent = MutableLiveData<Boolean>()
    val noDataEvent: LiveData<Boolean> get() = _noDataEvent

    private val omronManager: OmronDeviceManager = OmronDeviceManager(
        omronDeviceType,
        OHQSessionType.TRANSFER,
        this
    )

    override fun onTransferFailed(reason: OHQCompletionReason?) {
        _loadingEvent.postValue(false)
    }

    override fun onTransferSuccess(sessionData: SessionData?) {
        _loadingEvent.postValue(false)
        val results = sessionData?.measurementRecords

        when {
            results.isNullOrEmpty() -> _noDataEvent.postValue(true)
            omronDeviceType.isHEM9200T || omronDeviceType.isHEM7155T -> updateBloodPressureData(results)
            omronDeviceType.isHBF222F -> {
                updateBodyCompositionData(results)
                handleBodyCompositionSettingAfterTransfer(sessionData)
            }
        }
    }

    fun requestData() {
        _loadingEvent.value = true

        when {
            omronDeviceType.isHEM9200T -> {
                omronManager.requestBpData(PrefUtils.getBpMonitorHem9200tAddress())
            }
            omronDeviceType.isHEM7155T -> {
                omronManager.requestBpData(PrefUtils.getBpMonitorHem7155tAddress())
            }
            omronDeviceType.isHBF222F -> {
                val info = PrefUtils.getBodyCompositionMonitorHbf222tTransferInfo()
                omronManager.requestWeightData(info)
            }
        }
    }

    fun cancel() {
        omronManager.cancelSession()
    }

    fun disconnectDevice() {
        when {
            omronDeviceType.isHBF222F -> PrefUtils.removeOmronWeightDeice()
            omronDeviceType.isHEM9200T -> PrefUtils.saveBpMonitorHem9200tDeviceAddress(null)
            omronDeviceType.isHEM7155T -> PrefUtils.saveBpMonitorHem7155tAddress(null)
        }
    }

    private fun updateBloodPressureData(results: List<Map<OHQMeasurementRecordKey, Any>>?) {
        val data = results!!.stream()
            .map { data: Map<OHQMeasurementRecordKey, Any> -> mapBpResult(data) }
            .collect(Collectors.toList())
        _dataTransferEvent.postValue(data)
    }

    private fun updateBodyCompositionData(results: List<Map<OHQMeasurementRecordKey, Any>>?) {
        val data = results!!.stream()
            .map { data: Map<OHQMeasurementRecordKey, Any> -> mapWeightResult(data) }
            .collect(Collectors.toList())
        _dataTransferEvent.postValue(data)
    }

    private fun mapWeightResult(data: Map<OHQMeasurementRecordKey, Any>): OmronHealthData.WeightData {
        return OmronHealthData.WeightData(
            data[OHQMeasurementRecordKey.TimeStampKey] as String,
            (data[OHQMeasurementRecordKey.BodyFatPercentageKey] as BigDecimal?)!!.toFloat(),
            (data[OHQMeasurementRecordKey.WeightKey] as BigDecimal?)!!.toFloat()
        )
    }

    private fun mapBpResult(data: Map<OHQMeasurementRecordKey, Any>): OmronHealthData.BpData {
        return OmronHealthData.BpData(
            data[OHQMeasurementRecordKey.TimeStampKey] as String,
            (data[OHQMeasurementRecordKey.SystolicKey] as BigDecimal?)!!.toFloat(),
            (data[OHQMeasurementRecordKey.DiastolicKey] as BigDecimal?)!!.toFloat(),
            (data[OHQMeasurementRecordKey.PulseRateKey] as BigDecimal?)!!.toFloat()
        )
    }

    private fun handleBodyCompositionSettingAfterTransfer(sessionData: SessionData) {
        if (sessionData.sequenceNumberOfLatestRecord != null) {
            PrefUtils.saveBodyCompositionMonitorHbf222tSequenceNumber(sessionData.sequenceNumberOfLatestRecord!!)
        }

        if (omronManager.isUserInfoChanged(sessionData, Const.demoUser)) {
            if (sessionData.databaseChangeIncrement != null) {
                PrefUtils.saveOmronBleDataBaseIncrementKey(sessionData.databaseChangeIncrement!!)
            }
        }
    }
}