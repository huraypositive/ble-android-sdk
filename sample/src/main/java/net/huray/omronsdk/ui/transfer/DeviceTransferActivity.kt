package net.huray.omronsdk.ui.transfer

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import net.huray.omronsdk.R
import net.huray.omronsdk.ble.enumerate.OHQCompletionReason
import net.huray.omronsdk.ble.enumerate.OmronDeviceType
import net.huray.omronsdk.common.BaseActivity
import net.huray.omronsdk.databinding.ActivityOmronRequestBinding
import net.huray.omronsdk.model.DeviceConnectionState
import net.huray.omronsdk.utils.Const
import net.huray.omronsdk.utils.PrefUtils.getBodyCompositionMonitorHbf222tUserIndex

class DeviceTransferActivity : BaseActivity() {
    private lateinit var binding: ActivityOmronRequestBinding

    private lateinit var adapter: DeviceTransferAdapter
    private lateinit var omronDeviceType: OmronDeviceType

    private val viewModel: DeviceTransferViewModel by viewModelsFactory {
        val deviceId = intent.getIntExtra(Const.EXTRA_DEVICE_TYPE, 0)
        omronDeviceType = OmronDeviceType.fromId(deviceId)

        DeviceTransferViewModel(omronDeviceType)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOmronRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initViews()
        initObservers()
    }

    private fun initViews() {
        binding.tvRequestOmronTitle.text = omronDeviceType.modelName
        binding.tvDisconnectOmronDevice.setOnClickListener { showConfirmDialog() }

        adapter = DeviceTransferAdapter(omronDeviceType)
        binding.rvRequestedDataList.adapter = adapter

        if (omronDeviceType.isHBF222F) {
            binding.constraintUserIndex.visibility = View.VISIBLE
            binding.tvUserIndex.text = getBodyCompositionMonitorHbf222tUserIndex().toString()
        }
    }

    private fun initObservers() {
        viewModel.connectionEvent.observe(this) { state ->
            when (state) {
                is DeviceConnectionState.Failed -> handleFailureEvent(state.reason)
                else -> {}
            }
        }

        viewModel.dataTransferEvent.observe(this) {
            adapter.updateHealthData(it)
            Toast.makeText(this, getString(R.string.success_to_receive_data), Toast.LENGTH_SHORT).show()
        }

        viewModel.noDataEvent.observe(this) {
            Toast.makeText(this, getString(R.string.no_data_to_bring), Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleFailureEvent(reason: OHQCompletionReason) {
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
            viewModel.disconnectDevice()
            finish()
            dialog.dismiss()
        }

        dialog.show()
    }
}