package net.huray.omronsdk.ui.transfer

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import net.huray.omronsdk.utils.PrefUtils.getBodyCompositionMonitorHbf222tUserIndex
import net.huray.omronsdk.utils.PrefUtils.getBpMonitorHem9200tAddress
import net.huray.omronsdk.utils.PrefUtils.getBodyCompositionMonitorHbf222tTransferInfo
import net.huray.omronsdk.utils.PrefUtils.removeOmronWeightDeice
import net.huray.omronsdk.utils.PrefUtils.saveBpMonitorHem9200tDeviceAddress
import net.huray.omronsdk.utils.PrefUtils.saveBodyCompositionMonitorHbf222tSequenceNumber
import net.huray.omronsdk.utils.PrefUtils.saveOmronBleDataBaseIncrementKey
import net.huray.omronsdk.OmronDeviceManager.TransferListener
import net.huray.omronsdk.OmronDeviceManager
import net.huray.omronsdk.ble.enumerate.OmronDeviceType
import net.huray.omronsdk.R
import net.huray.omronsdk.ble.enumerate.OHQSessionType
import net.huray.omronsdk.ble.enumerate.OHQCompletionReason
import net.huray.omronsdk.ble.entity.SessionData
import net.huray.omronsdk.ble.enumerate.OHQMeasurementRecordKey
import net.huray.omronsdk.common.BaseActivity
import net.huray.omronsdk.databinding.ActivityOmronRequestBinding
import net.huray.omronsdk.model.OmronHealthData
import net.huray.omronsdk.utils.Const
import net.huray.omronsdk.utils.PrefUtils.saveBpMonitorHem7155tAddress
import java.math.BigDecimal
import java.util.stream.Collectors

class DeviceTransferActivity : BaseActivity(), TransferListener {
    private lateinit var binding: ActivityOmronRequestBinding

    private lateinit var adapter: DeviceTransferAdapter
    private lateinit var omronDeviceType: OmronDeviceType
    private lateinit var omronManager: OmronDeviceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOmronRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initOmronManager()
        initViews()
    }

    private fun initOmronManager() {
        val typeNumber = intent.getIntExtra(Const.EXTRA_DEVICE_TYPE, 0)
        omronDeviceType = OmronDeviceType.getDeviceType(typeNumber)

        omronManager = OmronDeviceManager(
            omronDeviceType.omronDeviceCategory,
            OHQSessionType.TRANSFER,
            this
        )

        adapter = DeviceTransferAdapter(omronDeviceType)
    }

    private fun initViews() {
        binding.tvIndexTitle.text = omronDeviceType.getName()
        binding.btnRequestOmronData.setOnClickListener { requestData() }
        binding.tvDisconnectOmronDevice.setOnClickListener { showConfirmDialog() }

        binding.rvRequestedDataList.adapter = adapter
        binding.btnStopConnection.setOnClickListener { omronManager.cancelSession() }

        if (omronDeviceType.isHBF222F) {
            binding.constraintUserIndex.visibility = View.VISIBLE
            binding.tvUserIndex.text = getBodyCompositionMonitorHbf222tUserIndex().toString()
        }
    }

    private fun requestData() {
        showLoadingView()

        if (omronDeviceType.is9200T) {
            omronManager.requestBpData(getBpMonitorHem9200tAddress())
            return
        }

        val info = getBodyCompositionMonitorHbf222tTransferInfo()
        omronManager.requestWeightData(info)
    }

    private fun showConfirmDialog() {
        val dialog = AlertDialog.Builder(this).create()
        dialog.setTitle(getString(R.string.alert))
        dialog.setMessage(getString(R.string.sure_to_disconnect))

        dialog.setButton(
            AlertDialog.BUTTON_NEGATIVE,
            getString(R.string.cacel)
        ) { _: DialogInterface?, _: Int -> dialog.dismiss() }

        dialog.setButton(
            AlertDialog.BUTTON_POSITIVE,
            getString(R.string.disconnect)
        ) { _: DialogInterface?, _: Int ->
            disconnectDevice()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun disconnectDevice() {
        when {
            omronDeviceType.isHBF222F -> removeOmronWeightDeice()
            omronDeviceType.is9200T -> saveBpMonitorHem9200tDeviceAddress(null)
            omronDeviceType.is7155T -> saveBpMonitorHem7155tAddress(null)
        }

        finish()
    }

    private fun showLoadingView() {
        binding.progressContainer.visibility = View.VISIBLE
    }

    private fun hideLoadingView() {
        binding.progressContainer.visibility = View.GONE
    }

    override fun onTransferFailed(reason: OHQCompletionReason) {
        hideLoadingView()

        if (reason.isCanceled) {
            Toast.makeText(
                this,
                getString(R.string.request_canceled),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (reason.isTimeOut) {
            Toast.makeText(
                this,
                getString(R.string.please_check_device_is_on),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onTransferSuccess(sessionData: SessionData) {
        hideLoadingView()

        val results = sessionData.measurementRecords

        if (results!!.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_data_to_bring), Toast.LENGTH_SHORT).show()
            return
        }

        Toast.makeText(this, getString(R.string.success_to_receive_data), Toast.LENGTH_SHORT).show()
        if (omronDeviceType.is9200T) {
            updateBpData(results)
            return
        }

        if (omronDeviceType.isHBF222F) {
            updateWeightData(results)
            if (sessionData.sequenceNumberOfLatestRecord != null) {
                saveBodyCompositionMonitorHbf222tSequenceNumber(sessionData.sequenceNumberOfLatestRecord!!)
            }

            if (omronManager.isUserInfoChanged(sessionData, Const.demoUser)) {
                if (sessionData.databaseChangeIncrement != null) {
                    saveOmronBleDataBaseIncrementKey(sessionData.databaseChangeIncrement!!)
                }
            }
        }
    }

    private fun updateBpData(results: List<Map<OHQMeasurementRecordKey, Any>>?) {
        val data = results!!.stream()
            .map { data: Map<OHQMeasurementRecordKey, Any> -> mapBpResult(data) }
            .collect(Collectors.toList())
        adapter.updateHealthData(data)
    }

    private fun updateWeightData(results: List<Map<OHQMeasurementRecordKey, Any>>?) {
        val data = results!!.stream()
            .map { data: Map<OHQMeasurementRecordKey, Any> -> mapWeightResult(data) }
            .collect(Collectors.toList())
        adapter.updateHealthData(data)
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
}