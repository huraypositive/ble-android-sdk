package net.huray.omronsdk.ui.transfer

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import net.huray.omronsdk.databinding.ItemOmronWeightDataBinding

class WeightDataViewHolder(containerView: View) : RecyclerView.ViewHolder(containerView) {
    private val binding = ItemOmronWeightDataBinding.bind(containerView)
    val tvTimeStamp = binding.tvOmronWeightTime
    val tvWeight = binding.tvOmronWeightValue
    val tvBodyFat = binding.tvOmronWeightBodyFat
}