package net.huray.omronsdk.ui.device_list

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.huray.omronsdk.databinding.ItemDeviceListBinding

class DeviceListViewHolder(
    containerView: View,
    private val clickListener: DeviceItemClickListener
) : RecyclerView.ViewHolder(containerView) {
    private val binding = ItemDeviceListBinding.bind(containerView)
    val viewGroup = binding.vgDeviceItem
    val tvDevice = binding.tvDeviceItem
    val ivConnectionIndicator = binding.ivConnectionIndicator
}