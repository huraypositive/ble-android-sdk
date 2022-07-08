package net.huray.omronsdk;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.huray.omronsdk.ble.OHQDeviceManager;
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
    final private static int DEFAULT_WAIT_TIME = 30;
    final private static int MAX_WAIT_TIME = 300;

    private final ScanController scanController;
    private final SessionController sessionController;
    private final LoggingManager loggingManager = new LoggingManager();

    private final OHQDeviceCategory deviceCategory;
    private final OHQSessionType sessionType;
    private final List<OmronDeviceType> targetDevices = new ArrayList<>();

    private RegisterListener registerListener;
    private TransferListener transferListener;

    private OmronDeviceManager(
            Context context,
            OHQDeviceCategory deviceCategory,
            OHQSessionType sessionType
    ) {
        OHQDeviceManager.init(context);

        scanController = new ScanController(this);
        sessionController = new SessionController(this);
        this.deviceCategory = deviceCategory;
        this.sessionType = sessionType;
    }

    public OmronDeviceManager(
            Context context,
            OHQDeviceCategory deviceCategory,
            OHQSessionType sessionType,
            RegisterListener listener
    ) {
        this(context, deviceCategory, sessionType);
        this.registerListener = listener;
    }

    public OmronDeviceManager(
            Context context,
            OHQDeviceCategory deviceCategory,
            OHQSessionType sessionType,
            TransferListener listener
    ) {
        this(context, deviceCategory, sessionType);
        this.transferListener = listener;
    }

    @Override
    public void onScan(@NonNull @NotNull List<DiscoveredDevice> discoveredDevices) {
        validateScanListener();

        List<DiscoveredDevice> filteredList = new ArrayList<>();
        for (DiscoveredDevice device : discoveredDevices) {
            if (device.getLocalName() == null) continue;

            if (isTargeted(device.getLocalName())) {
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

    public void startScan(List<OmronDeviceType> targetDevices) {
        scanController.setFilteringDeviceCategory(deviceCategory);
        this.targetDevices.clear();
        this.targetDevices.addAll(targetDevices);

        stopScan();
        scanController.startScan();
    }

    public void stopScan() {
        scanController.stopScan();
    }

    public void connectWeightDevice(WeightDeviceInfo info) {
        connectWeightDevice(info, null);
    }

    public void connectWeightDevice(WeightDeviceInfo info, @Nullable Integer waitTimeSec) {
        stopScan();

        startSession(info.getAddress(), info, waitTimeSec);
    }

    public void connectBpDevice(String address) {
        connectBpDevice(address, null);
    }

    public void connectBpDevice(String address, @Nullable Integer waitTimeSec) {
        stopScan();

        startSession(address, null, waitTimeSec);
    }

    public void requestWeightData(WeightDeviceInfo info) {
        requestWeightData(info, null);
    }

    public void requestWeightData(WeightDeviceInfo info, @Nullable Integer waitTimeSec) {
        startSession(info.getAddress(), info, waitTimeSec);
    }

    public void requestBpData(String address) {
        requestBpData(address, null);
    }

    public void requestBpData(String address, @Nullable Integer waitTimeSec) {
        startSession(address, null, waitTimeSec);
    }

    public void cancelSession() {
        sessionController.cancel();
    }

    /**
     * 오므론 기기와 연결하는 세션을 시작한다.
     * 세션은 기기와 최초 연결 그리고 데이터 수신을 위한 연결에 사용한다.
     *
     * @waitTimeSec session에 적용하는 타임아웃(초)
     * 최소 30초, 최대 300초이며, 이외의 범위에서는 기본값 30초로 적용된다.
     */
    private void startSession(
            String deviceAddress,
            @Nullable WeightDeviceInfo weightDeviceInfo,
            @Nullable Integer waitTimeSec
    ) {
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
                final int timeout = getSessionTimeOut(waitTimeSec);

                handler.post(() -> {
                    sessionController.setConfig(OmronOption.getConfig());
                    sessionController.startSession(deviceAddress, getOptionKeys(weightDeviceInfo, timeout));
                });
            }
        });
    }

    private int getSessionTimeOut(@Nullable Integer customWaitTime) {
        if (customWaitTime == null) {
            return DEFAULT_WAIT_TIME;
        }

        if (customWaitTime <= DEFAULT_WAIT_TIME) {
            return DEFAULT_WAIT_TIME;
        }

        if (customWaitTime >= MAX_WAIT_TIME) {
            return MAX_WAIT_TIME;
        }

        return customWaitTime;
    }

    private Map<OHQSessionOptionKey, Object> getOptionKeys(@Nullable WeightDeviceInfo weightDeviceInfo, int waitTimeSec) {
        if (deviceCategory == OHQDeviceCategory.WeightScale ||
                deviceCategory == OHQDeviceCategory.BodyCompositionMonitor) {
            if (weightDeviceInfo == null) {
                throw new NullPointerException("weightDeviceInfo is null");
            }
            return OmronOption.getWeightOptionsKeys(weightDeviceInfo, waitTimeSec);
        }

        return OmronOption.getOptionsKeys(waitTimeSec);
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

    private void setSessionFailed(OHQCompletionReason reason) {
        if (sessionType == OHQSessionType.REGISTER) {
            registerListener.onRegisterFailed(reason);
            return;
        }

        transferListener.onTransferFailed(reason);
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

    private boolean isTargeted(String localName) {
        for (OmronDeviceType devices : targetDevices) {
            if (devices.isSameModel(localName)) return true;
        }

        return false;
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
