package net.huray.omronsdk.ui.device_list

interface DeviceItemClickListener {
    fun onItemClicked(isConnected: Boolean, deviceNumber: Int)
}