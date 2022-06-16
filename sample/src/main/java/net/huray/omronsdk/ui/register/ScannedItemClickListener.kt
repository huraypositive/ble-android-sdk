package net.huray.omronsdk.ui.register

import net.huray.omronsdk.model.Device

interface ScannedItemClickListener {
    fun onDeviceClickListener(device: Device)
}