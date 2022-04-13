package net.huray.omronsdk.ui.device_list

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import net.huray.omronsdk.R
import net.huray.omronsdk.ble.enumerate.OmronDeviceType
import net.huray.omronsdk.utils.PrefUtils

class DeviceListAdapter(private val clickListener: DeviceItemClickListener) :
    RecyclerView.Adapter<DeviceListViewHolder>() {

    private val deviceStates = mutableListOf<DeviceStatus>()

    init {
        initDeviceList()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceListViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_device_list,
                parent, false
            )

        return DeviceListViewHolder(view)
    }

    override fun onBindViewHolder(holder: DeviceListViewHolder, position: Int) {
        holder.tvDevice.text = deviceStates[position].deviceType.getName()
        setIndicator(holder.ivConnectionIndicator, position)

        holder.viewGroup.setOnClickListener {
            clickListener.onItemClicked(
                deviceStates[position].isConnected,
                deviceStates[position].deviceType.number
            )
        }
    }

    override fun getItemCount(): Int = deviceStates.size

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    private fun setIndicator(view: ImageView?, position: Int) {
        if (deviceStates[position].isConnected) view!!.setImageResource(R.drawable.round_blue)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun initDeviceList() {
        initDeviceItems()
        notifyDataSetChanged()
    }

    private fun initDeviceItems() {
        deviceStates.add(
            DeviceStatus(
                OmronDeviceType.BODY_COMPOSITION_MONITOR_HBF_222F,
                PrefUtils.getBodyCompositionMonitorHbf222tAddress() != null
            )
        )
        deviceStates.add(
            DeviceStatus(
                OmronDeviceType.BP_MONITOR_HEM_9200T,
                PrefUtils.getBpMonitorHem9200tAddress() != null
            )
        )
        deviceStates.add(
            DeviceStatus(
                OmronDeviceType.BP_MONITOR_HEM_7155T,
                PrefUtils.getBpMonitorHem7155tAddress() != null
            )
        )
    }
}