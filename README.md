# Omron Android SDK
오므론 블루투스 기기 SDK

## 적용 기기
- 오므론 체성분계 [Omron HBF-222T]
- 오므론 혈압계 [Omron HEM-9200T]

## 의존성 추가
```gradle
// in your root build.gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}

// in your app-level build.gradle
dependencies {
        implementation 'com.github.huraypositive:ble-android-sdk:Tag'
}
```

## Sample Code
- sample 모듈 참고

## 사용 방법
- OmronDeviceManager 클래스 (기기 스캔, 연결, 데이터 요청)
- sample 코드가 Java로 작성되어있는 관계로 예시 코드 또한 Java로 설명한다.

## Manifest.xml 권한 추가
```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

## 1. 기기 페어링 (Register)
#### 사용할 클래스(Activity 혹은 Fragment)에 OmronDeviceManager.RegisterListener 인터페이스를 구현한다.
```Java
public classRegisterActivity extends AppCompatActivity implements OmronDeviceManager.RegisterListener {
    @Override
    void onScanned(List<DiscoveredDevice> discoveredDevices) {
        // 스캔된 기기 목록이 들어오는 콜백 메서드
    }

    @Override
    void onRegisterFailed(OHQCompletionReason reason) {
        // 기기 등록이 실패했을 때 호출되는 콜백 메서드
        // 연결 취소 혹은 연결 시간 초과(30초)
    }

    @Override
    void onRegisterSuccess() {
        // 기기 등록이 성공했을 때 호출되는 콜백 메서드
    }
}
```

#### OmronManager 객체 초기화 예시 (Activity의 경우 onCreate()에서 초기화)
```Java
private OmronDeviceManager omronManager;

private void initDeviceManager() {
    omronManager = new OmronDeviceManager(
        OHQDeviceCategory.BodyCompositionMonitor, // or OHQDeviceCategory.BloodPressureMonitor
        OHQSessionType.REGISTER,
        this);
}
```

#### 기기 스캔 예시
```Java
private void startScanOmron() {
    // 스캔 시작 버튼과 스캔 중지 버튼이 같은 경우
    if (omronManager.isScanning()) {
        omronManager.stopScan();
        return;
    }
    omronManager.startScan();
    showScanningView();
}
```

#### 기기 연결 예시 (체성분계)
```Java
private void connectWeightDevice(int position) {
    if (userIndex == 0) {
        // 사용자 번호 선택해야 함
        return;
    }

    deviceAddress = adapter.getDeviceAddress(position);
    WeightDeviceInfo deviceData = WeightDeviceInfo.newInstanceForRegister(deviceAddress, userIndex);
    omronManager.connectWeightDevice(deviceData);
    showLoadingView();
    // 30초 동안 연결되지 않으면 연결 실패
}
```

#### 기기 연결 예시 (혈압계)
```Java
private void connectBpDevice(int position) {
    deviceAddress = adapter.getDeviceAddress(position);
    omronManager.connectBpDevice(deviceAddress);
    showLoadingView();
    // 30초 동안 연결되지 않으면 연결 실패
}
```

## 2. 측정 데이터 가져오기 (Transfer)
#### 사용할 클래스(Activity 혹은 Fragment)에 OmronDeviceManager.RegisterListener 인터페이스를 구현한다.
```Java
public classRegisterActivity extends AppCompatActivity implements OmronDeviceManager.TransferListener {
    @Override
    void onTransferFailed(OHQCompletionReason reason) {
        // 데이터 요청 실패 시 호출되는 콜백 메서드
        // 요청 취소 혹은 요청 시간 초과(15초)
    }

    @Override
    void onTransferSuccess(SessionData sessionData) {
        // 데이터 요청 성공 시 호출되는 콜백 메서드
        List<Map<OHQMeasurementRecordKey, Object>> results = sessionData.getMeasurementRecords();

        if (results.isEmpty()) {
            // 요청은 성공했지만 새로 측정된 데이터가 없음
            return;
        }

        if (deviceType.isBpDevice()) {
             updateBpData(results);
            return;
        }

        if (deviceType.isWeightDevice()) {
            updateWeightData(results);
            saveSequenceNumber(sessionData.getSequenceNumberOfLatestRecord());

            if (omronManager.isUserInfoChanged(sessionData, OmronOption.getDemoUser())) {
                updateIncrementDataKey();
            }

            return;
        }
    }
}
```

#### OmronManager 객체 초기화 예시 (Activity의 경우 onCreate()에서 초기화)
```Java
private OmronDeviceManager omronManager;

private void initDeviceManager() {
    omronManager = new OmronDeviceManager(
        OHQDeviceCategory.BodyCompositionMonitor, // or OHQDeviceCategory.BloodPressureMonitor
        OHQSessionType.TRANSFER,
        this);
}
```

