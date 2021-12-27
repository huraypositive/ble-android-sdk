package net.huray.omronsdk.ui.device_list;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import net.huray.omronsdk.R;
import net.huray.omronsdk.ui.register.OmronDeviceRegisterActivity;
import net.huray.omronsdk.ui.request_data.OmronTransferActivity;
import net.huray.omronsdk.utils.Const;

public class DeviceListActivity extends AppCompatActivity {
    public static final int REQUEST_CODE = 11;
    private final String permission = Manifest.permission.ACCESS_FINE_LOCATION;

    private DeviceListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_list);

        requestPermission();
        initViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initViews();
    }

    private void initViews() {
        adapter = new DeviceListAdapter(this);
        ListView listView = findViewById(R.id.lv_device_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> moveScreen(position));
    }

    private void moveScreen(int position) {
        if (isPermissionGranted()) {
            moveToActivity(position);
            return;
        }

        requestPermission();
    }

    private void moveToActivity(int position) {
        if (adapter.getDeviceConnectionState(position)) {
            Intent intent = new Intent(this, OmronTransferActivity.class);
            intent.putExtra(Const.EXTRA_DEVICE_TYPE, adapter.getDeviceTypeNumber(position));
            startActivity(intent);
            return;
        }

        Intent intent = new Intent(this, OmronDeviceRegisterActivity.class);
        intent.putExtra(Const.EXTRA_DEVICE_TYPE, adapter.getDeviceTypeNumber(position));
        startActivity(intent);
    }

    private void requestPermission() {
        if (isPermissionGranted()) return;

        ActivityCompat.requestPermissions(this, new String[]{permission}, REQUEST_CODE);
    }

    private boolean isPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }
}
