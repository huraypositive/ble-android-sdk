package net.huray.omronsdk.ui.register

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import net.huray.omronsdk.R
import net.huray.omronsdk.ble.entity.DiscoveredDevice
import net.huray.omronsdk.ble.enumerate.OmronDeviceType
import net.huray.omronsdk.model.Device
import java.util.*

class DeviceRegisterAdapter(
    private val clickListener: ScannedItemClickListener,
    private val omronDeviceType: OmronDeviceType
) : RecyclerView.Adapter<DeviceRegisterViewHolder>() {

    private val devices: MutableList<Device> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceRegisterViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_scanned_device, parent, false)
        return DeviceRegisterViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceRegisterViewHolder, position: Int) {
        holder.tvName.text = devices[position].name
        holder.tvAddress.text = devices[position].address

        holder.vgDevice.setOnClickListener {
            clickListener.onDeviceClickListener(devices[position])
        }
    }

    override fun getItemCount(): Int = devices.size

    fun updateOmronDevices(datum: List<DiscoveredDevice>) {
        devices.clear()

        for (device in datum) {
            devices.add(Device(omronDeviceType.getName(), device.address))
            notifyItemChanged(devices.lastIndex)
        }
    }
}