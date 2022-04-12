package net.huray.omronsdk.ui.device_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.huray.omronsdk.R;
import net.huray.omronsdk.ble.enumerate.OmronDeviceType;
import net.huray.omronsdk.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;

public class DeviceListAdapter extends BaseAdapter {
    private final Context context;
    private final List<DeviceStateData> deviceStates = new ArrayList<>();

    public DeviceListAdapter(Context context) {
        this.context = context;
        initDeviceList();
    }

    private void initDeviceList() {
        initDeviceItems();
        notifyDataSetChanged();
    }

    private void initDeviceItems() {
        deviceStates.add(new DeviceStateData(OmronDeviceType.BODY_COMPOSITION_MONITOR_HBF_222F, PrefUtils.getBodyCompositionMonitor_HBF222T_Address() != null));
        deviceStates.add(new DeviceStateData(OmronDeviceType.BP_MONITOR_HEM_9200T, PrefUtils.getBpMonitor_HEM9200T_Address() != null));
        deviceStates.add(new DeviceStateData(OmronDeviceType.BP_MONITOR_HEM_7155T, PrefUtils.getBpMonitor_HEM7155T_Address() != null));
    }

    public int getDeviceTypeNumber(int position) {
        return deviceStates.get(position).getDeviceType().getNumber();
    }

    public boolean getDeviceConnectionState(int position) {
        return deviceStates.get(position).isConnected();
    }

    @Override
    public int getCount() {
        return deviceStates.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceStates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        final LayoutInflater inflater = LayoutInflater.from(context);

        if (view == null) {
            view = inflater.inflate(R.layout.item_device_list, parent, false);
            final ViewHolder holder = new ViewHolder();

            holder.tvDeviceName = view.findViewById(R.id.tv_device_item);
            holder.tvDeviceName.setText(deviceStates.get(position).getDeviceType().getName());

            holder.ivIndicator = view.findViewById(R.id.iv_connection_indicator);
            setIndicator(holder.ivIndicator, position);

            view.setTag(holder);
        }

        return view;
    }

    private void setIndicator(ImageView view, int position) {
        if (deviceStates.get(position).isConnected()) view.setImageResource(R.drawable.round_blue);
    }

    private class ViewHolder {
        private TextView tvDeviceName;
        private ImageView ivIndicator;
    }
}
