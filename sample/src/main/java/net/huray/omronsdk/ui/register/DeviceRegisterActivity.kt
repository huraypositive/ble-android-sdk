package net.huray.omronsdk.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import net.huray.omronsdk.R
import net.huray.omronsdk.ble.enumerate.OHQCompletionReason
import net.huray.omronsdk.ble.enumerate.OmronDeviceType
import net.huray.omronsdk.common.BaseActivity
import net.huray.omronsdk.databinding.ActivityOmronDeviceRegisterBinding
import net.huray.omronsdk.model.Device
import net.huray.omronsdk.model.DeviceConnectionState
import net.huray.omronsdk.ui.transfer.DeviceTransferActivity
import net.huray.omronsdk.utils.Const

class DeviceRegisterActivity : BaseActivity(), ScannedItemClickListener {
    private lateinit var binding: ActivityOmronDeviceRegisterBinding

    private lateinit var adapter: DeviceRegisterAdapter
    private lateinit var omronDeviceType: OmronDeviceType

    private var userIndex = 0
    private val radioButtons = mutableListOf<Int>()

    private val viewModel: DeviceRegisterViewModel by viewModelsFactory {
        val deviceId = intent.getIntExtra(Const.EXTRA_DEVICE_TYPE, 0)
        omronDeviceType = OmronDeviceType.fromId(deviceId)

        DeviceRegisterViewModel(omronDeviceType)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOmronDeviceRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        initObservers()
        initViews()
    }

    override fun onDeviceClickListener(device: Device) {
        if (omronDeviceType.isHBF222F && userIndex == 0) {
            Toast.makeText(this, getString(R.string.select_user_index), Toast.LENGTH_SHORT).show()
            return
        }

        viewModel.connectDevice(
            deviceAddress = device.address,
            userIndex = userIndex
        )
    }

    fun onRadioButtonClicked(view: View) {
        val checked = (view as RadioButton).isChecked

        for (i in radioButtons.indices) {
            if (view.getId() == radioButtons[i]) {
                if (checked) userIndex = i + 1
                return
            }
        }
    }

    private fun initObservers() {
        viewModel.connectionEvent.observe(this) { state ->
            when (state) {
                is DeviceConnectionState.Scanning -> handleScanningEvent()
                is DeviceConnectionState.OnScanned -> adapter.updateOmronDevices(state.discoveredDevices)
                is DeviceConnectionState.Failed -> handleCancelEvent(state.reason)
                is DeviceConnectionState.ConnectionSuccess -> handleSuccessEvent()
                else -> {}
            }
        }
    }

    private fun initViews() {
        adapter = DeviceRegisterAdapter(this)

        binding.tvScanTitle.text = omronDeviceType.modelName
        binding.rvScannedDeviceList.adapter = adapter

        initRadioButtons()
    }

    private fun handleCancelEvent(reason: OHQCompletionReason) {
        if (reason.isCanceled) {
            Toast.makeText(this, getString(R.string.connection_canceled), Toast.LENGTH_SHORT).show()
            setViewForReadyToScan()
            return
        }

        if (reason.isFailedToConnect || reason.isFailedToRegisterUser || reason.isTimeOut) {
            Toast.makeText(this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show()
            setViewForReadyToScan()
            return
        }
    }

    private fun handleScanningEvent() {
        binding.btnScan.text = getString(R.string.stop_scan_device)
        binding.tvScanDescription.text = getString(R.string.scanning_device)
    }

    private fun handleSuccessEvent() {
        Toast.makeText(
            this,
            getString(R.string.connection_success),
            Toast.LENGTH_SHORT
        ).show()

        moveToRequestActivity()
    }

    private fun initRadioButtons() {
        radioButtons.add(R.id.rb_one)
        radioButtons.add(R.id.rb_two)
        radioButtons.add(R.id.rb_three)
        radioButtons.add(R.id.rb_four)

        if (omronDeviceType.isHBF222F) {
            binding.radioGroup.visibility = View.VISIBLE
            return
        }

        binding.radioGroup.visibility = View.INVISIBLE
    }

    private fun setViewForReadyToScan() {
        binding.btnScan.text = getString(R.string.start_scan_device)
        binding.tvScanDescription.text = getString(R.string.click_device_scan_button)
    }

    private fun moveToRequestActivity() {
        val intent = Intent(this, DeviceTransferActivity::class.java)
        intent.putExtra(Const.EXTRA_DEVICE_TYPE, omronDeviceType.id)
        startActivity(intent)
        finish()
    }
}