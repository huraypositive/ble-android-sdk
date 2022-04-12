package net.huray.omronsdk.ui.device_list

import net.huray.omronsdk.ble.enumerate.OmronDeviceType

data class DeviceStateData(
    val deviceType: OmronDeviceType,
    val isConnected: Boolean,
)