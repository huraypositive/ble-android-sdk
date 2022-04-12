package net.huray.omronsdk.ui.request_data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import net.huray.omronsdk.R;
import net.huray.omronsdk.ble.enumerate.OmronDeviceType;
import net.huray.omronsdk.model.BpData;
import net.huray.omronsdk.model.WeightData;

import java.util.ArrayList;
import java.util.List;

public class OmronDataAdapter extends BaseAdapter {
    private final Context context;
    private final OmronDeviceType omronDeviceType;

    private final List<WeightData> weightDataList = new ArrayList<>();
    private final List<BpData> bpDataList = new ArrayList<>();

    public OmronDataAdapter(Context context, OmronDeviceType omronDeviceType) {
        this.context = context;
        this.omronDeviceType = omronDeviceType;
    }

    public void addWeightData(List<WeightData> data) {
        weightDataList.addAll(data);
        notifyDataSetChanged();
    }

    public void addBpData(List<BpData> data) {
        bpDataList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (omronDeviceType.is9200T()) {
            return bpDataList.size();
        }

        return weightDataList.size();
    }

    @Override
    public Object getItem(int i) {
        if (omronDeviceType.is9200T()) {
            return bpDataList.get(i);
        }

        return weightDataList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);

        if (view == null) {
            switch (omronDeviceType) {
                case BODY_COMPOSITION_MONITOR_HBF_222F:
                    view = inflater.inflate(R.layout.item_omron_weight_data, parent, false);
                    setWeightDataView(view, position);
                    break;

                case BP_MONITOR_HEM_9200T:
                    view = inflater.inflate(R.layout.item_omron_bp_data, parent, false);
                    setBpDatView(view, position);
                    break;
            }
        }

        return view;
    }

    private void setWeightDataView(View view, int position) {
        WeightViewHolder holder = new WeightViewHolder();
        holder.tvTimeStamp = view.findViewById(R.id.tv_omron_weight_time);
        holder.tvWeight = view.findViewById(R.id.tv_omron_weight_value);
        holder.tvBodyFat = view.findViewById(R.id.tv_omron_weight_body_fat);

        holder.tvTimeStamp.setText(weightDataList.get(position).getTimeStamp());
        holder.tvWeight.setText(String.valueOf(weightDataList.get(position).getWeight()));
        holder.tvBodyFat.setText(String.valueOf(weightDataList.get(position).getBodyFat()));

        view.setTag(holder);
    }

    private void setBpDatView(View view, int position) {
        BpViewHolder holder = new BpViewHolder();
        holder.tvTimeStamp = view.findViewById(R.id.tv_omron_bp_time);
        holder.tvLowPressure = view.findViewById(R.id.tv_omron_low_bp);
        holder.tvHighPressure = view.findViewById(R.id.tv_omron_high_bp);

        holder.tvTimeStamp.setText(bpDataList.get(position).getTimeStamp());
        holder.tvLowPressure.setText(String.valueOf(bpDataList.get(position).getDbp()));
        holder.tvHighPressure.setText(String.valueOf(bpDataList.get(position).getSbp()));

        view.setTag(holder);
    }

    private class WeightViewHolder {
        TextView tvTimeStamp;
        TextView tvWeight;
        TextView tvBodyFat;
    }

    private class BpViewHolder {
        TextView tvTimeStamp;
        TextView tvLowPressure;
        TextView tvHighPressure;
    }
}