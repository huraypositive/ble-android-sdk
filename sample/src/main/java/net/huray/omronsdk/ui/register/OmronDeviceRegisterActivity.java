package net.huray.omronsdk.ui.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import net.huray.omronsdk.OmronDeviceManager;
import net.huray.omronsdk.R;
import net.huray.omronsdk.ble.entity.DiscoveredDevice;
import net.huray.omronsdk.ble.entity.WeightDeviceInfo;
import net.huray.omronsdk.ble.enumerate.OmronDeviceType;
import net.huray.omronsdk.ble.enumerate.OHQCompletionReason;
import net.huray.omronsdk.ble.enumerate.OHQSessionType;
import net.huray.omronsdk.ui.request_data.OmronTransferActivity;
import net.huray.omronsdk.utils.Const;
import net.huray.omronsdk.utils.PrefUtils;

import java.util.ArrayList;
import java.util.List;


public class OmronDeviceRegisterActivity extends AppCompatActivity
        implements OmronDeviceManager.RegisterListener {

    private DeviceRegisterAdapter adapter;
    private OmronDeviceType omronDeviceType;
    private OmronDeviceManager omronManager;

    private Button btnScan;
    private TextView tvDescription;
    private ConstraintLayout progressBarContainer;

    private int userIndex = 0;
    private String deviceAddress;

    private List<Integer> radioButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omron_device_register);

        setDeviceType();
        initViews();
        initDeviceManager();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopScanOmron();
    }

    private void setDeviceType() {
        int deviceTypeNumber = getIntent().getIntExtra(Const.EXTRA_DEVICE_TYPE, 0);
        omronDeviceType = OmronDeviceType.getDeviceType(deviceTypeNumber);
    }

    private void initDeviceManager() {
        omronManager = new OmronDeviceManager(
                omronDeviceType.getOmronDeviceCategory(),
                OHQSessionType.REGISTER,
                this);
    }

    private void startScanOmron() {
        if (omronManager.isScanning()) {
            stopScanOmron();
            return;
        }
        omronManager.startScan();
        btnScan.setText(getString(R.string.stop_scan_device));
        tvDescription.setText(getString(R.string.scanning_device));
    }

    private void stopScanOmron() {
        omronManager.stopScan();
        setViewForReadyToScan();
    }

    private void setViewForReadyToScan() {
        btnScan.setText(getString(R.string.start_scan_device));
        tvDescription.setText(getString(R.string.click_device_scan_button));
    }

    private void connectDevice(int position) {
        if (omronDeviceType.isHBF222F()) {
            connectWeightDevice(position);
            return;
        }

        connectBpDevice(position);
    }

    private void connectWeightDevice(int position) {
        if (userIndex == 0) {
            Toast.makeText(this, getString(R.string.select_user_index), Toast.LENGTH_SHORT).show();
            return;
        }

        deviceAddress = adapter.getDeviceAddress(position);
        WeightDeviceInfo deviceInfo = WeightDeviceInfo.newInstanceForRegister(
                Const.getDemoUser(), // This should be real user data in product code
                deviceAddress,
                userIndex);
        omronManager.connectWeightDevice(deviceInfo);
        showLoadingView();
    }

    private void connectBpDevice(int position) {
        deviceAddress = adapter.getDeviceAddress(position);
        omronManager.connectBpDevice(deviceAddress);
        showLoadingView();
    }

    private void initViews() {
        TextView tvTitle = findViewById(R.id.tv_scan_title);
        tvTitle.setText(omronDeviceType.getName());

        adapter = new DeviceRegisterAdapter(this, omronDeviceType);
        ListView listView = findViewById(R.id.lv_scanned_device_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> connectDevice(position));

        btnScan = findViewById(R.id.btn_scan);
        btnScan.setOnClickListener(v -> startScanOmron());

        tvDescription = findViewById(R.id.tv_scan_description);
        progressBarContainer = findViewById(R.id.progress_container);

        Button btnStopConnection = findViewById(R.id.btn_stop_connection);
        btnStopConnection.setOnClickListener(v -> omronManager.cancelSession());

        initRadioButtons();
    }

    private void initRadioButtons() {
        radioButtons = new ArrayList<>();
        radioButtons.add(R.id.rb_one);
        radioButtons.add(R.id.rb_two);
        radioButtons.add(R.id.rb_three);
        radioButtons.add(R.id.rb_four);

        RadioGroup radioGroup = findViewById(R.id.radio_group);
        if (omronDeviceType.isHBF222F()) {
            radioGroup.setVisibility(View.VISIBLE);
            return;
        }

        radioGroup.setVisibility(View.INVISIBLE);
    }

    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        for (int i = 0; i < radioButtons.size(); i++) {
            if (view.getId() == radioButtons.get(i)) {
                if (checked) userIndex = i + 1;
                return;
            }
        }
    }

    private void showLoadingView() {
        progressBarContainer.setVisibility(View.VISIBLE);
    }

    private void hideLoadingView() {
        progressBarContainer.setVisibility(View.GONE);
    }

    private void completeRegister() {
        if (deviceAddress == null) {
            throw new IllegalArgumentException("deviceAddress");
        }

        if (omronDeviceType.isHBF222F()) {
            PrefUtils.setBodyCompositionMonitor_HBF222T_Address(deviceAddress);
            PrefUtils.setBodyCompositionMonitor_HBF222T_UserIndex(userIndex);
            return;
        }

        PrefUtils.setBpMonitor_HEM9200T_DeviceAddress(deviceAddress);
    }

    private void moveToRequestActivity() {
        Intent intent = new Intent(this, OmronTransferActivity.class);
        intent.putExtra(Const.EXTRA_DEVICE_TYPE, omronDeviceType.getNumber());
        startActivity(intent);
        finish();
    }

    @Override
    public void onScanned(List<DiscoveredDevice> discoveredDevices) {
        adapter.updateOmronDevices(discoveredDevices);
    }

    @Override
    public void onRegisterFailed(OHQCompletionReason reason) {
        hideLoadingView();

        if (reason.isCanceled()) {
            Toast.makeText(this, getString(R.string.connection_canceled), Toast.LENGTH_SHORT).show();
            setViewForReadyToScan();
            return;
        }

        if (reason.isFailedToConnect() || reason.isFailedToRegisterUser() || reason.isTimeOut()) {
            Toast.makeText(this, getString(R.string.connection_failed), Toast.LENGTH_SHORT).show();
            setViewForReadyToScan();
        }
    }

    @Override
    public void onRegisterSuccess() {
        hideLoadingView();

        Toast.makeText(this, getString(R.string.connection_success), Toast.LENGTH_SHORT).show();
        completeRegister();
        moveToRequestActivity();
    }
}