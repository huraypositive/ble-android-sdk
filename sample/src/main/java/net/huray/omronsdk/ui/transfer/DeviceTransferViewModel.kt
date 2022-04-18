package net.huray.omronsdk.ui.transfer

import androidx.lifecycle.ViewModel
import net.huray.omronsdk.OmronDeviceManager
import net.huray.omronsdk.ble.entity.SessionData
import net.huray.omronsdk.ble.enumerate.OHQCompletionReason

class DeviceTransferViewModel : ViewModel(), OmronDeviceManager.TransferListener {

    override fun onTransferFailed(reason: OHQCompletionReason?) {
    }

    override fun onTransferSuccess(sessionData: SessionData?) {
    }
}