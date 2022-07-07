package net.huray.omronsdk.ui.device_list

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES.S
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermissionUtil
import com.gun0912.tedpermission.normal.TedPermission
import net.huray.omronsdk.databinding.ActivityDeviceListBinding
import net.huray.omronsdk.ui.register.DeviceRegisterActivity
import net.huray.omronsdk.ui.transfer.DeviceTransferActivity
import net.huray.omronsdk.utils.Const

class DeviceListActivity : AppCompatActivity(), DeviceItemClickListener, PermissionListener {
    private val permissions: List<String>
        get() {
            val list = mutableListOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            )

            if (Build.VERSION.SDK_INT >= S) {
                list.add(Manifest.permission.BLUETOOTH_SCAN)
                list.add(Manifest.permission.BLUETOOTH_CONNECT)
            }

            return list
        }

    private var adapter: DeviceListAdapter = DeviceListAdapter(this)

    private lateinit var binding: ActivityDeviceListBinding

    private val isPermissionGranted: Boolean
        get() {
            if (permissions.isEmpty()) {
                return true
            }

            return TedPermissionUtil.isGranted(*permissions.toTypedArray())
        }

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

    override fun onItemClicked(isConnected: Boolean, deviceId: Int) {
        moveScreen(isConnected, deviceId)
    }

    override fun onPermissionGranted() {
        Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
        Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
    }

    private fun moveScreen(isConnected: Boolean, deviceId: Int) {
        if (isPermissionGranted) {
            moveToActivity(isConnected, deviceId)
            return
        }
        requestPermission()
    }

    private fun moveToActivity(isConnected: Boolean, deviceId: Int) {
        if (isConnected) {
            val intent = Intent(this, DeviceTransferActivity::class.java)
            intent.putExtra(Const.EXTRA_DEVICE_TYPE, deviceId)
            startActivity(intent)
            return
        }

        val intent = Intent(this, DeviceRegisterActivity::class.java)
        intent.putExtra(Const.EXTRA_DEVICE_TYPE, deviceId)
        startActivity(intent)
    }

    private fun requestPermission() {
        if (isPermissionGranted) return
        TedPermission.create()
            .setPermissionListener(this)
            .setPermissions(*permissions.toTypedArray()).check()
    }
}