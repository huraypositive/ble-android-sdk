package net.huray.omronsdk.ui.register;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import net.huray.omronsdk.R;
import net.huray.omronsdk.ble.entity.DiscoveredDevice;
import net.huray.omronsdk.ble.enumerate.OmronDeviceType;
import net.huray.omronsdk.model.Device;

import java.util.ArrayList;
import java.util.List;

public class DeviceRegisterAdapter extends BaseAdapter {
    private final Context context;
    private final OmronDeviceType omronDeviceType;

    private final List<Device> devices = new ArrayList<>();

    public DeviceRegisterAdapter(Context context, OmronDeviceType omronDeviceType) {
        this.context = context;
        this.omronDeviceType = omronDeviceType;
    }

    public void updateOmronDevices(List<DiscoveredDevice> datum) {
        devices.clear();
        for (DiscoveredDevice device : datum) {
            devices.add(new Device(omronDeviceType.getName(), device.getAddress()));
        }
        notifyDataSetChanged();
    }

    public String getDeviceAddress(int position) {
        return devices.get(position).getAddress();
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
            view = inflater.inflate(R.layout.item_scanned_device, parent, false);
            final ViewHolder holder = new ViewHolder();

            holder.tvName = view.findViewById(R.id.tv_scanned_device_name);
            holder.tvAddress = view.findViewById(R.id.tv_scanned_device_address);
            holder.tvName.setText(devices.get(position).getName());
            holder.tvAddress.setText(devices.get(position).getAddress());

            view.setTag(holder);
        }

        return view;
    }

    private class ViewHolder {
        TextView tvName;
        TextView tvAddress;
    }
}
