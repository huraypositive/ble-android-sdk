package net.huray.omronsdk.ui.request_data;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import net.huray.omronsdk.OmronDeviceManager;
import net.huray.omronsdk.R;
import net.huray.omronsdk.ble.entity.SessionData;
import net.huray.omronsdk.ble.entity.WeightDeviceInfo;
import net.huray.omronsdk.ble.enumerate.OmronDeviceType;
import net.huray.omronsdk.ble.enumerate.OHQCompletionReason;
import net.huray.omronsdk.ble.enumerate.OHQMeasurementRecordKey;
import net.huray.omronsdk.ble.enumerate.OHQSessionType;
import net.huray.omronsdk.model.BpData;
import net.huray.omronsdk.model.WeightData;
import net.huray.omronsdk.utils.Const;
import net.huray.omronsdk.utils.PrefUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class OmronTransferActivity extends AppCompatActivity implements OmronDeviceManager.TransferListener {

    private OmronDeviceManager omronManager;
    private OmronDataAdapter adapter;
    private OmronDeviceType omronDeviceType;

    private ConstraintLayout progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_omron_request);

        setDeviceType();
        initViews();
        initDeviceManager();
    }

    private void setDeviceType() {
        int deviceTypeNumber = getIntent().getIntExtra(Const.EXTRA_DEVICE_TYPE, 0);
        omronDeviceType = OmronDeviceType.getDeviceType(deviceTypeNumber);
    }

    private void initViews() {
        TextView tvTitle = findViewById(R.id.tv_request_omron_title);
        tvTitle.setText(omronDeviceType.getName());

        Button btnRequest = findViewById(R.id.btn_request_omron_data);
        btnRequest.setOnClickListener(v -> requestData());

        TextView tvDisconnect = findViewById(R.id.tv_disconnect_omron_device);
        tvDisconnect.setOnClickListener(v -> showConfirmDialog());

        adapter = new OmronDataAdapter(this, omronDeviceType);
        ListView listView = findViewById(R.id.lv_requested_data_list);
        listView.setAdapter(adapter);

        progressBar = findViewById(R.id.progress_container);
        ConstraintLayout userIndexContainer = findViewById(R.id.constraint_user_index);
        TextView tvUserIndex = findViewById(R.id.tv_user_index);

        Button btnStop = findViewById(R.id.btn_stop_connection);
        btnStop.setOnClickListener(v -> omronManager.cancelSession());

        if (omronDeviceType.isHBF222F()) {
            userIndexContainer.setVisibility(View.VISIBLE);
            tvUserIndex.setText(String.valueOf(PrefUtils.getBodyCompositionMonitor_HBF222T_UserIndex()));
        }
    }

    private void requestData() {
        showLoadingView();

        if (omronDeviceType.is9200T()) {
            omronManager.requestBpData(PrefUtils.getBpMonitor_HEM9200T_Address());
            return;
        }

        WeightDeviceInfo info = PrefUtils.getBodyCompositionMonitor_HBF222T_TransferInfo();
        omronManager.requestWeightData(info);
    }

    private void showConfirmDialog() {
        AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setTitle(getString(R.string.alert));
        dialog.setMessage(getString(R.string.sure_to_disconnect));
        dialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(R.string.cacel), (dialogInterface, i) -> {
            dialog.dismiss();
        });
        dialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.disconnect), (dialogInterface, i) -> {
            disconnectDevice();
            dialog.dismiss();
        });

        dialog.show();
    }

    private void disconnectDevice() {
        if (omronDeviceType.isHBF222F()) {
            PrefUtils.removeOmronWeightDeice();
        }

        if (omronDeviceType.is9200T()) {
            PrefUtils.setBpMonitor_HEM9200T_DeviceAddress(null);
        }

        finish();
    }

    private void initDeviceManager() {
        omronManager = new OmronDeviceManager(
                omronDeviceType.getOmronDeviceCategory(),
                OHQSessionType.TRANSFER,
                this);
    }

    private void showLoadingView() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideLoadingView() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onTransferFailed(OHQCompletionReason reason) {
        hideLoadingView();

        if (reason.isCanceled()) {
            Toast.makeText(this, getString(R.string.request_canceled), Toast.LENGTH_SHORT).show();
            return;
        }

        if (reason.isTimeOut()) {
            Toast.makeText(this, getString(R.string.please_check_device_is_on), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTransferSuccess(SessionData sessionData) {
        hideLoadingView();
        List<Map<OHQMeasurementRecordKey, Object>> results = sessionData.getMeasurementRecords();

        if (results.isEmpty()) {
            Toast.makeText(this, getString(R.string.no_data_to_bring), Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, getString(R.string.success_to_receive_data), Toast.LENGTH_SHORT).show();

        if (omronDeviceType.is9200T()) {
             updateBpData(results);
            return;
        }

        if (omronDeviceType.isHBF222F()) {
            updateWeightData(results);

            if (sessionData.getSequenceNumberOfLatestRecord() != null) {
                PrefUtils.setBodyCompositionMonitor_HBF222T_SequenceNumber(sessionData.getSequenceNumberOfLatestRecord());
            }

            if (omronManager.isUserInfoChanged(sessionData, Const.getDemoUser())) {
                if (sessionData.getDatabaseChangeIncrement() != null) {
                    PrefUtils.setOmronBleDataBaseIncrementKey(sessionData.getDatabaseChangeIncrement());
                }
            }
        }
    }

    private void updateBpData(List<Map<OHQMeasurementRecordKey, Object>> results) {
        List<BpData> data = results.stream()
                .map(this::mapBpResult)
                .collect(Collectors.toList());
        adapter.addBpData(data);
    }

    private void updateWeightData(List<Map<OHQMeasurementRecordKey, Object>> results) {
        List<WeightData> data = results.stream()
                .map(this::mapWeightResult)
                .collect(Collectors.toList());
        adapter.addWeightData(data);
    }

    private WeightData mapWeightResult(Map<OHQMeasurementRecordKey, Object> data) {
        return new WeightData(
                (String) data.get(OHQMeasurementRecordKey.TimeStampKey),
                ((BigDecimal) data.get(OHQMeasurementRecordKey.BodyFatPercentageKey)).floatValue(),
                ((BigDecimal) data.get(OHQMeasurementRecordKey.WeightKey)).floatValue()
        );
    }

    private BpData mapBpResult(Map<OHQMeasurementRecordKey, Object> data) {
        return new BpData(
                (String) data.get(OHQMeasurementRecordKey.TimeStampKey),
                ((BigDecimal) data.get(OHQMeasurementRecordKey.SystolicKey)).floatValue(),
                ((BigDecimal) data.get(OHQMeasurementRecordKey.DiastolicKey)).floatValue(),
                ((BigDecimal) data.get(OHQMeasurementRecordKey.PulseRateKey)).floatValue()

        );
    }
}