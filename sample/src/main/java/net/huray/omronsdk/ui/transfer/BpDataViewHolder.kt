package net.huray.omronsdk.ui.transfer

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import net.huray.omronsdk.databinding.ItemOmronBpDataBinding

class BpDataViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView) {
    private val binding = ItemOmronBpDataBinding.bind(containerView)
    val tvTimeStamp = binding.tvOmronBpTime
    val tvLowPressure = binding.tvOmronLowBp
    val tvHighPressure = binding.tvOmronHighBp
}