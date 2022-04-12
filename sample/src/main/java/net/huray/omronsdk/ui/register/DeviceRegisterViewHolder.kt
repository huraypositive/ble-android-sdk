package net.huray.omronsdk.ui.register

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import net.huray.omronsdk.databinding.ItemScannedDeviceBinding

class DeviceRegisterViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView) {
    private val binding = ItemScannedDeviceBinding.bind(containerView)
    val tvName = binding.tvScannedDeviceName
    val tvAddress = binding.tvScannedDeviceAddress
}