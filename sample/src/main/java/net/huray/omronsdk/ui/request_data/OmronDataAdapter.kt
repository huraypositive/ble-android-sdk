package net.huray.omronsdk.ui.request_data

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import net.huray.omronsdk.R
import net.huray.omronsdk.ble.enumerate.OmronDeviceType
import net.huray.omronsdk.model.WeightData
import net.huray.omronsdk.model.BpData
import net.huray.omronsdk.ui.request_data.OmronDataAdapter.WeightViewHolder
import net.huray.omronsdk.ui.request_data.OmronDataAdapter.BpViewHolder
import java.util.ArrayList

class OmronDataAdapter(private val context: Context, private val omronDeviceType: OmronDeviceType) :
    BaseAdapter() {
    private val weightDataList: MutableList<WeightData> = ArrayList()
    private val bpDataList: MutableList<BpData> = ArrayList()
    fun addWeightData(data: List<WeightData>?) {
        weightDataList.addAll(data!!)
        notifyDataSetChanged()
    }

    fun addBpData(data: List<BpData>?) {
        bpDataList.addAll(data!!)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return if (omronDeviceType.is9200T) {
            bpDataList.size
        } else weightDataList.size
    }

    override fun getItem(i: Int): Any {
        return if (omronDeviceType.is9200T) {
            bpDataList[i]
        } else weightDataList[i]
    }

    override fun getItemId(i: Int): Long {
        return i.toLong()
    }

    override fun getView(position: Int, view: View, parent: ViewGroup): View {
        var view = view
        val inflater = LayoutInflater.from(context)

        if (view == null) {
            when (omronDeviceType) {
                OmronDeviceType.BODY_COMPOSITION_MONITOR_HBF_222F -> {
                    view = inflater.inflate(R.layout.item_omron_weight_data, parent, false)
                    setWeightDataView(view, position)
                }
                OmronDeviceType.BP_MONITOR_HEM_9200T -> {
                    view = inflater.inflate(R.layout.item_omron_bp_data, parent, false)
                    setBpDatView(view, position)
                }

                OmronDeviceType.BP_MONITOR_HEM_7155T -> {}

                else -> {}
            }
        }
        return view
    }

    private fun setWeightDataView(view: View, position: Int) {
        val holder = WeightViewHolder()
        holder.tvTimeStamp = view.findViewById(R.id.tv_omron_weight_time)
        holder.tvWeight = view.findViewById(R.id.tv_omron_weight_value)
        holder.tvBodyFat = view.findViewById(R.id.tv_omron_weight_body_fat)
        holder.tvTimeStamp?.text = weightDataList[position].timeStamp
        holder.tvWeight?.text = weightDataList[position].weight.toString()
        holder.tvBodyFat?.text = weightDataList[position].bodyFat.toString()
        view.tag = holder
    }

    private fun setBpDatView(view: View, position: Int) {
        val holder = BpViewHolder()
        holder.tvTimeStamp = view.findViewById(R.id.tv_omron_bp_time)
        holder.tvLowPressure = view.findViewById(R.id.tv_omron_low_bp)
        holder.tvHighPressure = view.findViewById(R.id.tv_omron_high_bp)
        holder.tvTimeStamp?.text = bpDataList[position].timeStamp
        holder.tvLowPressure?.text = bpDataList[position].dbp.toString()
        holder.tvHighPressure?.text = bpDataList[position].sbp.toString()
        view.tag = holder
    }

    private inner class WeightViewHolder {
        var tvTimeStamp: TextView? = null
        var tvWeight: TextView? = null
        var tvBodyFat: TextView? = null
    }

    private inner class BpViewHolder {
        var tvTimeStamp: TextView? = null
        var tvLowPressure: TextView? = null
        var tvHighPressure: TextView? = null
    }
}