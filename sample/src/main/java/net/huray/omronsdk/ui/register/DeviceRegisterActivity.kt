package net.huray.omronsdk.ui.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import net.huray.omronsdk.utils.PrefUtils.saveBodyCompositionMonitorHbf222tAddress
import net.huray.omronsdk.utils.PrefUtils.saveBodyCompositionMonitorHbf222tUserIndex
import net.huray.omronsdk.utils.PrefUtils.saveBpMonitorHem9200tDeviceAddress
import androidx.appcompat.app.AppCompatActivity
import net.huray.omronsdk.OmronDeviceManager.RegisterListener
import net.huray.omronsdk.ble.enumerate.OmronDeviceType
import net.huray.omronsdk.OmronDeviceManager
import net.huray.omronsdk.R
import net.huray.omronsdk.ble.enumerate.OHQSessionType
import net.huray.omronsdk.ble.entity.WeightDeviceInfo
import net.huray.omronsdk.ui.transfer.DeviceTransferActivity
import net.huray.omronsdk.ble.entity.DiscoveredDevice
import net.huray.omronsdk.ble.enumerate.OHQCompletionReason
import net.huray.omronsdk.databinding.ActivityOmronDeviceRegisterBinding
import net.huray.omronsdk.model.Device
import net.huray.omronsdk.utils.Const

class DeviceRegisterActivity : AppCompatActivity(), RegisterListener, ScannedItemClickListener {
    private lateinit var binding: ActivityOmronDeviceRegisterBinding

    private lateinit var adapter: DeviceRegisterAdapter
    private lateinit var omronDeviceType: OmronDeviceType
    private lateinit var omronManager: OmronDeviceManager

    private var userIndex = 0
    private var deviceAddress: String? = null
    private val radioButtons = mutableListOf<Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOmronDeviceRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initOmronManager()
        initViews()
    }

    override fun onStop() {
        super.onStop()
        stopScanOmron()
    }

    override fun onScanned(discoveredDevices: List<DiscoveredDevice>) {
        adapter.updateOmronDevices(discoveredDevices)
    }

    override fun onRegisterFailed(reason: OHQCompletionReason) {
        hideLoadingView()

        if (reason.isCanceled) {
            Toast.makeText(this, getString(R.string.connection_canceled), Toast.LENGTH_SHORT).show()
            setViewForReadyToScan()
            return
        }

        if (reason.isFailedToConnect || reason.isFailedToRegisterUser || reason.isTimeOut) {
            Toast.makeText(this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show()
            setViewForReadyToScan()
        }
    }

    override fun onRegisterSuccess() {
        hideLoadingView()
        Toast.makeText(this, getString(R.string.connection_success), Toast.LENGTH_SHORT).show()
        completeRegister()
        moveToRequestActivity()
    }

    override fun onDeviceClickListener(device: Device) {
        connectDevice(device.address)
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

    private fun initOmronManager() {
        val typeNumber = intent.getIntExtra(Const.EXTRA_DEVICE_TYPE, 0)
        omronDeviceType = OmronDeviceType.getDeviceType(typeNumber)

        omronManager = OmronDeviceManager(
            omronDeviceType.omronDeviceCategory,
            OHQSessionType.REGISTER,
            this
        )

        adapter = DeviceRegisterAdapter(this, omronDeviceType)
    }

    private fun initViews() {
        binding.tvScanTitle.text = omronDeviceType.getName()
        binding.rvScannedDeviceList.adapter = adapter

        binding.btnScan.setOnClickListener { startScanOmron() }
        binding.btnStopConnection.setOnClickListener { omronManager.cancelSession() }
        initRadioButtons()
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

    private fun startScanOmron() {
        if (omronManager.isScanning) {
            stopScanOmron()
            return
        }

        omronManager.startScan()
        binding.btnScan.text = getString(R.string.stop_scan_device)
        binding.tvScanDescription.text = getString(R.string.scanning_device)
    }

    private fun stopScanOmron() {
        omronManager.stopScan()
        setViewForReadyToScan()
    }

    private fun setViewForReadyToScan() {
        binding.btnScan.text = getString(R.string.start_scan_device)
        binding.tvScanDescription.text = getString(R.string.click_device_scan_button)
    }

    private fun connectDevice(deviceAddress: String) {
        this.deviceAddress = deviceAddress
        if (omronDeviceType.isHBF222F) {
            connectWeightDevice(deviceAddress)
            return
        }

        connectBpDevice(deviceAddress)
    }

    private fun connectWeightDevice(deviceAddress: String) {
        if (userIndex == 0) {
            Toast.makeText(this, getString(R.string.select_user_index), Toast.LENGTH_SHORT).show()
            return
        }

        val deviceInfo = WeightDeviceInfo.newInstanceForRegister(
            Const.demoUser,  // This should be real user data in product code
            deviceAddress,
            userIndex
        )
        omronManager.connectWeightDevice(deviceInfo)

        showLoadingView()
    }

    private fun connectBpDevice(deviceAddress: String) {
        omronManager.connectBpDevice(deviceAddress)
        showLoadingView()
    }

    private fun showLoadingView() {
        binding.progressContainer.visibility = View.VISIBLE
    }

    private fun hideLoadingView() {
        binding.progressContainer.visibility = View.GONE
    }

    private fun completeRegister() {
        requireNotNull(deviceAddress) { "deviceAddress is null" }

        if (omronDeviceType.isHBF222F) {
            saveBodyCompositionMonitorHbf222tAddress(deviceAddress!!)
            saveBodyCompositionMonitorHbf222tUserIndex(userIndex)
            return
        }

        saveBpMonitorHem9200tDeviceAddress(deviceAddress)
    }

    private fun moveToRequestActivity() {
        val intent = Intent(this, DeviceTransferActivity::class.java)
        intent.putExtra(Const.EXTRA_DEVICE_TYPE, omronDeviceType.number)
        startActivity(intent)
        finish()
    }
}