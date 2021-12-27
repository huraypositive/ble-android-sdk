package net.huray.omronsdk.ui.device_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.huray.omronsdk.R;
import net.huray.omronsdk.ble.enumerate.DeviceType;
import net.huray.omronsdk.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;

public class DeviceListAdapter extends BaseAdapter {
    private final Context context;
    private final List<DeviceType> devices = new ArrayList<>();
    private final List<Boolean> connectionStates = new ArrayList<>();

    public DeviceListAdapter(Context context) {
        this.context = context;
        initDeviceList();
    }

    private void initDeviceList() {
        initDeviceItems();
        notifyDataSetChanged();
    }

    private void initDeviceItems() {
        devices.add(DeviceType.OMRON_WEIGHT);
        devices.add(DeviceType.OMRON_BP);

        connectionStates.add(PrefUtils.getOmronBleWeightDeviceAddress() != null);
        connectionStates.add(PrefUtils.getOmronBleBpDeviceAddress() != null);
    }

    public int getDeviceTypeNumber(int position) {
        return devices.get(position).getNumber();
    }

    public boolean getDeviceConnectionState(int position) {
        return connectionStates.get(position);
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
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
            holder.tvDeviceName.setText(devices.get(position).getName());

            holder.ivIndicator = view.findViewById(R.id.iv_connection_indicator);
            setIndicator(holder.ivIndicator, position);

            view.setTag(holder);
        }

        return view;
    }

    private void setIndicator(ImageView view, int position) {
        if (connectionStates.get(position)) view.setImageResource(R.drawable.round_blue);
    }

    private class ViewHolder {
        private TextView tvDeviceName;
        private ImageView ivIndicator;
    }
}
