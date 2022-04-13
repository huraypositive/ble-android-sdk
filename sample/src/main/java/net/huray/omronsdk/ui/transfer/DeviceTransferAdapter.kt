package net.huray.omronsdk.ui.transfer

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.huray.omronsdk.R
import net.huray.omronsdk.ble.enumerate.OmronDeviceType
import net.huray.omronsdk.model.OmronHealthData
import java.util.ArrayList

class DeviceTransferAdapter(
    private val omronDeviceType: OmronDeviceType
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val healthDataList = mutableListOf<OmronHealthData>()

    override fun getItemViewType(position: Int): Int {
        return omronDeviceType.number
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val itemRes = if (omronDeviceType.isHBF222F) R.layout.item_omron_weight_data
        else R.layout.item_omron_bp_data

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                itemRes,
                parent,
                false
            )

        if (omronDeviceType.isHBF222F) return WeightDataViewHolder(view)
        return BpDataViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when {
            omronDeviceType.isHBF222F -> {
                setWeightDataView(
                    holder = holder as WeightDataViewHolder,
                    weightData = healthDataList[position] as OmronHealthData.WeightData
                )
            }

            omronDeviceType.is9200T || omronDeviceType.is7155T -> {
                setBpDatView(
                    holder = holder as BpDataViewHolder,
                    bpData = healthDataList[position] as OmronHealthData.BpData
                )
            }
        }
    }

    override fun getItemCount(): Int = healthDataList.size

    @SuppressLint("NotifyDataSetChanged")
    fun updateHealthData(data: List<OmronHealthData>) {
        healthDataList.clear()
        healthDataList.addAll(data)
        notifyDataSetChanged()
    }

    private fun setWeightDataView(
        holder: WeightDataViewHolder,
        weightData: OmronHealthData.WeightData
    ) {
        holder.tvTimeStamp.text = weightData.timeStamp
        holder.tvWeight.text = weightData.weight.toString()
        holder.tvBodyFat.text = weightData.bodyFat.toString()
    }

    private fun setBpDatView(holder: BpDataViewHolder, bpData: OmronHealthData.BpData) {
        holder.tvTimeStamp.text = bpData.timeStamp
        holder.tvLowPressure.text = bpData.dbp.toString()
        holder.tvHighPressure.text = bpData.sbp.toString()
    }
}