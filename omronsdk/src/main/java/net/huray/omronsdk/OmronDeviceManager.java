package net.huray.omronsdk;

import androidx.annotation.NonNull;

import net.huray.omronsdk.ble.controller.ScanController;
import net.huray.omronsdk.ble.controller.SessionController;
import net.huray.omronsdk.ble.controller.util.AppLog;
import net.huray.omronsdk.ble.entity.DiscoveredDevice;
import net.huray.omronsdk.ble.entity.OmronOption;
import net.huray.omronsdk.ble.entity.SessionData;
import net.huray.omronsdk.ble.entity.WeightDeviceInfo;
import net.huray.omronsdk.ble.enumerate.OHQCompletionReason;
import net.huray.omronsdk.ble.enumerate.OHQConnectionState;
import net.huray.omronsdk.ble.enumerate.OHQDeviceCategory;
import net.huray.omronsdk.ble.enumerate.OHQGender;
import net.huray.omronsdk.ble.enumerate.OHQSessionOptionKey;
import net.huray.omronsdk.ble.enumerate.OHQSessionType;
import net.huray.omronsdk.ble.enumerate.OHQUserDataKey;
import net.huray.omronsdk.ble.enumerate.OmronDeviceType;
import net.huray.omronsdk.ble.system.LoggingManager;
import net.huray.omronsdk.utility.Handler;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class OmronDeviceManager implements ScanController.Listener, SessionController.Listener {
    private final ScanController scanController = new ScanController(this);
    private final SessionController sessionController = new SessionController(this);
    private final LoggingManager loggingManager = new LoggingManager();

    private final OmronDeviceType deviceType;
    private final OHQSessionType sessionType;

    private RegisterListener registerListener;
    private TransferListener transferListener;

    private WeightDeviceInfo weightDeviceInfo;

    private String deviceAddress;

    private boolean isScanning = false;
    private boolean isScanAllCategory = false;

    private OmronDeviceManager(OmronDeviceType deviceType, OHQSessionType sessionType) {
        this.deviceType = deviceType;
        this.sessionType = sessionType;
    }

    public OmronDeviceManager(OmronDeviceType deviceType,
                              OHQSessionType sessionType,
                              RegisterListener listener) {
        this(deviceType, sessionType);
        this.registerListener = listener;
    }

    public OmronDeviceManager(OmronDeviceType deviceType,
                              OHQSessionType sessionType,
                              TransferListener listener) {
        this(deviceType, sessionType);
        this.transferListener = listener;
    }

    public boolean isScanning() {
        return isScanning;
    }

    public void startScan(Boolean scanAllCategory) {
        isScanAllCategory = scanAllCategory;
        startScan();
    }

    public void startScan() {
        scanController.setFilteringDeviceCategory(deviceType.getOmronDeviceCategory());

        if (isScanning) return;

        isScanning = true;
        scanController.startScan();
    }

    public void stopScan() {
        if (isScanning) {
            isScanning = false;
            scanController.stopScan();
        }
    }

    public void connectWeightDevice(WeightDeviceInfo info) {
        weightDeviceInfo = info;
        deviceAddress = info.getAddress();
        stopScan();

        startOmronSession();
    }

    public void connectBpDevice(String address) {
        deviceAddress = address;
        stopScan();

        startOmronSession();
    }

    public void requestWeightData(WeightDeviceInfo info) {
        weightDeviceInfo = info;
        deviceAddress = info.getAddress();

        startOmronSession();
    }

    public void requestBpData(String address) {
        deviceAddress = address;

        startOmronSession();
    }

    public void cancelSession() {
        sessionController.cancel();
    }

    /**
     * 오므론 기기와 연결하는 세션을 시작한다.
     * 세션은 기기와 최초 연결 그리고 데이터 수신을 위한 연결에 사용한다.
     */
    private void startOmronSession() {
        if (sessionController.isInSession()) {
            AppLog.i("세션이 이미 시작되었음");
            return;
        }

        final Handler handler = new Handler();
        loggingManager.start(new LoggingManager.ActionListener() {
            @Override
            public void onSuccess() {
                onStarted();
            }

            @Override
            public void onFailure() {
                onStarted();
            }

            private void onStarted() {
                handler.post(() -> {
                    sessionController.setConfig(OmronOption.getConfig());
                    sessionController.startSession(deviceAddress, getOptionKeys());
                });
            }
        });
    }

    private Map<OHQSessionOptionKey, Object> getOptionKeys() {
        if (deviceType.getOmronDeviceCategory() == OHQDeviceCategory.WeightScale) {
            if (weightDeviceInfo == null) {
                throw new NullPointerException("weightDeviceInfo is null");
            }
            return OmronOption.getWeightOptionsKeys(weightDeviceInfo);
        }

        return OmronOption.getOptionsKeys(sessionType);
    }

    /**
     * checkIfUserInfoChanged
     * <p>
     * 체성분계에 저장된 사용자정보와 앱의 사용자 정보 비교한다.
     * 두 정보가 다를 경우 다음 데이터 전송때 체성분계에 저장된 사용자 정보를 앱을 기준으로 수정한다.
     */
    public boolean isUserInfoChanged(SessionData sessionData, Map<OHQUserDataKey, Object> user) {
        if (sessionData.getUserData() != null && sessionData.getDatabaseChangeIncrement() != null) {
            Map<OHQUserDataKey, Object> deviceUser = sessionData.getUserData();

            BigDecimal userHeight = (BigDecimal) user.get(OHQUserDataKey.HeightKey);
            BigDecimal deviceHeight = (BigDecimal) deviceUser.get(OHQUserDataKey.HeightKey);

            String userBirthDate = (String) user.get(OHQUserDataKey.DateOfBirthKey);
            String deviceBirthDate = (String) deviceUser.get(OHQUserDataKey.DateOfBirthKey);

            OHQGender userGender = (OHQGender) user.get(OHQUserDataKey.GenderKey);
            OHQGender deviceGender = (OHQGender) deviceUser.get(OHQUserDataKey.GenderKey);

            boolean isHeightChanged = !Objects.equals(userHeight, deviceHeight);
            boolean isBirthDateChanged = !Objects.equals(userBirthDate, deviceBirthDate);
            boolean isGenderChanged = !Objects.equals(userGender, deviceGender);

            return isBirthDateChanged || isHeightChanged || isGenderChanged;
        }

        return false;
    }

    @Override
    public void onScan(@NonNull @NotNull List<DiscoveredDevice> discoveredDevices) {
        validateScanListener();

        if (isScanAllCategory) {
            registerListener.onScanned(discoveredDevices);
            return;
        }

        List<DiscoveredDevice> filteredList = new ArrayList<>();
        for (DiscoveredDevice device : discoveredDevices) {
            if (device.getLocalName() == null) continue;
            if (device.getLocalName().startsWith(deviceType.getTypeId())) {
                filteredList.add(device);
            }
        }

        registerListener.onScanned(filteredList);
    }

    @Override
    public void onScanCompletion(@NonNull @NotNull OHQCompletionReason reason) {
    }

    @Override
    public void onConnectionStateChanged(@NonNull @NotNull OHQConnectionState connectionState) {
    }

    @Override
    public void onSessionComplete(@NonNull @NotNull SessionData sessionData) {
        OHQCompletionReason reason = sessionData.getCompletionReason();

        assert reason != null;
        if (reason.isCanceled() ||
                reason.isFailedToConnect() ||
                reason.isFailedToRegisterUser() ||
                reason.isTimeOut()) {
            setSessionFailed(sessionData.getCompletionReason());
            return;
        }

        if (sessionType == OHQSessionType.REGISTER) {
            validateScanListener();
            registerListener.onRegisterSuccess();
            return;
        }

        validateTransferListener();
        transferListener.onTransferSuccess(sessionData);
    }

    private void setSessionFailed(OHQCompletionReason reason) {
        if (sessionType == OHQSessionType.REGISTER) {
            registerListener.onRegisterFailed(reason);
        }

        if (sessionType == OHQSessionType.TRANSFER) {
            transferListener.onTransferFailed(reason);
        }
    }

    private void validateScanListener() throws IllegalStateException {
        if (registerListener == null) {
            throw new IllegalStateException("RegisterListener is not initialized");
        }
    }

    private void validateTransferListener() throws IllegalStateException {
        if (transferListener == null) {
            throw new IllegalStateException("TransferListener is not initialized");
        }
    }

    public interface RegisterListener {

        void onScanned(List<DiscoveredDevice> discoveredDevices);

        void onRegisterFailed(OHQCompletionReason reason);

        void onRegisterSuccess();
    }

    public interface TransferListener {

        void onTransferFailed(OHQCompletionReason reason);

        void onTransferSuccess(SessionData sessionData);
    }
}
