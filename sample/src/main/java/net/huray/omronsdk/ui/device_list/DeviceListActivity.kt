package net.huray.omronsdk.ui.device_list

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import net.huray.omronsdk.databinding.ActivityDeviceListBinding
import net.huray.omronsdk.ui.register.DeviceRegisterActivity
import net.huray.omronsdk.ui.transfer.DeviceTransferActivity
import net.huray.omronsdk.utils.Const

class DeviceListActivity : AppCompatActivity(), DeviceItemClickListener {
    private val permission = Manifest.permission.ACCESS_FINE_LOCATION

    private var adapter: DeviceListAdapter = DeviceListAdapter(this)

    private lateinit var binding: ActivityDeviceListBinding

    private val isPermissionGranted: Boolean
        get() = ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
        binding.rvDeviceList.adapter = adapter

        requestPermission()
    }

    override fun onResume() {
        adapter.initDeviceList()
        super.onResume()
    }

    override fun onItemClicked(isConnected: Boolean, deviceNumber: Int) {
        moveScreen(isConnected, deviceNumber)
    }

    private fun moveScreen(isConnected: Boolean, deviceNumber: Int) {
        if (isPermissionGranted) {
            moveToActivity(isConnected, deviceNumber)
            return
        }
        requestPermission()
    }

    private fun moveToActivity(isConnected: Boolean, deviceNumber: Int) {
        if (isConnected) {
            val intent = Intent(this, DeviceTransferActivity::class.java)
            intent.putExtra(Const.EXTRA_DEVICE_TYPE, deviceNumber)
            startActivity(intent)
            return
        }

        val intent = Intent(this, DeviceRegisterActivity::class.java)
        intent.putExtra(Const.EXTRA_DEVICE_TYPE, deviceNumber)
        startActivity(intent)
    }

    private fun requestPermission() {
        if (isPermissionGranted) return
        ActivityCompat.requestPermissions(this, arrayOf(permission), REQUEST_CODE)
    }

    companion object {
        const val REQUEST_CODE = 11
    }
}