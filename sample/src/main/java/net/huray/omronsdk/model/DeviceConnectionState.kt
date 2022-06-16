package net.huray.omronsdk.model

import net.huray.omronsdk.ble.entity.DiscoveredDevice
import net.huray.omronsdk.ble.enumerate.OHQCompletionReason

sealed class DeviceConnectionState {

    object Scanning : DeviceConnectionState()

    data class Failed(
        val reason: OHQCompletionReason
    ) : DeviceConnectionState()

    data class OnScanned(
        val discoveredDevices: List<DiscoveredDevice>
    ) : DeviceConnectionState()

    object Connecting : DeviceConnectionState()

    object ConnectionSuccess : DeviceConnectionState()
}
