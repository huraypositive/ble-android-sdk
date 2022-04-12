package net.huray.omronsdk.ui.register

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import net.huray.omronsdk.R
import net.huray.omronsdk.ble.enumerate.OmronDeviceType
import net.huray.omronsdk.ble.entity.DiscoveredDevice
import net.huray.omronsdk.model.Device
import java.util.ArrayList

class DeviceRegisterAdapter(
    private val context: Context,
    private val omronDeviceType: OmronDeviceType
) : BaseAdapter() {
    private val devices: MutableList<Device> = ArrayList()
    fun updateOmronDevices(datum: List<DiscoveredDevice>) {
        devices.clear()
        for (device in datum) {
            devices.add(Device(omronDeviceType.getName(), device.address))
        }
        notifyDataSetChanged()
    }

    fun getDeviceAddress(position: Int): String {
        return devices[position].address
    }

    override fun getCount(): Int {
        return devices.size
    }

    override fun getItem(position: Int): Any {
        return devices[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, view: View, parent: ViewGroup): View {
        var view = view
        val inflater = LayoutInflater.from(context)

        if (view == null) {
            view = inflater.inflate(R.layout.item_scanned_device, parent, false)
            val holder = ViewHolder()
            holder.tvName = view.findViewById(R.id.tv_scanned_device_name)
            holder.tvAddress = view.findViewById(R.id.tv_scanned_device_address)
            holder.tvName?.text = devices[position].name
            holder.tvAddress?.text = devices[position].address
            view.tag = holder
        }

        return view
    }

    private inner class ViewHolder {
        var tvName: TextView? = null
        var tvAddress: TextView? = null
    }
}