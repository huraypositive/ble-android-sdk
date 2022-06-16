# Omron Android SDK
오므론 블루투스 기기 SDK의 사용을 단순하게 하기 위한 라이브러리

## 적용 기기
- 오므론 체성분계 [Omron HBF-222T]
- 오므론 혈압계 [Omron HEM-9200T]
- 오므론 혈압계 [Omron HEM-7155T]

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
    implementation 'com.github.huraypositive:omron-android-sdk:$version'
}
```

## Sample Code
- sample 모듈 참고

## 사용 방법
- OmronDeviceManager 클래스 (기기 스캔, 연결, 데이터 요청)

## Manifest.xml 권한 추가
```xml
<uses-permission android:name="android.permission.BLUETOOTH" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
```

## 1. OHQDeviceManager 초기화
#### SDK를 사용하기 전 OHQDeviceManager 인스턴스를 초기화해줘야 한다. 
```kotlin
// in your App.kt
class App : Application {
    override fun onCreate() {
        super.onCreate()
        OHQDeviceManager.init(applicationContext)
    }
}
```

## 2. 기기 등록 (Register)
#### 2-1. OmronManager 객체 초기화
```kotlin
// 파라미터로 넘겨주는 sessionType이  `REGISTER`임에 유의한다.
private val omronManager: OmronDeviceManager = OmronDeviceManager(
    omronDeviceType.omronDeviceCategory,
    OHQSessionType.REGISTER,
    this
)
```

#### 2-2. 사용할 클래스(예: `ViewModel`)에 OmronDeviceManager.RegisterListener 인터페이스를 구현한다.
```kotlin
class DeviceRegisterViewModel() : ViewModel(), OmronDeviceManager.RegisterListener {
    override fun onScanned(discoveredDevices: List<DiscoveredDevice>?) {
        // 스캔된 기기 목록이 들어오는 콜백 메서드
    }

    override fun onRegisterFailed(reason: OHQCompletionReason?) {
        // 기기 등록이 실패했을 때 호출되는 콜백 메서드
        // 연결 취소 혹은 연결 시간 초과(30초)
    }

    override fun onRegisterSuccess() {
        // 기기 등록이 성공했을 때 호출되는 콜백 메서드
    }
}
```

#### 2-3. 기기 스캔
```kotlin
fun startScan() {
    if (omronManager.isScanning()) {
        return
    }
    
    omronManager.startScan()
}
```

#### 2-4a. 기기 연결 (체성분계 - HBF-222T)
```kotlin
private fun connectWeightDevice(deviceAddress: String, userIndex: Int) {
    if (userIndex == 0) {
        // 사용자 번호 선택해야 함
        return
    }
    
    val deviceInfo = WeightDeviceInfo.newInstanceForRegister(userData, deviceAddress, userIndex)
    omronManager.connectWeightDevice(deviceInfo)
    // 30초 동안 연결되지 않으면 연결 실패 -> onRegisterFailed() 함수 호출됨
}
```

#### 2-4a. 기기 연결 (혈압계 - HEM-9200T)
```kotlin
private fun connectBpDevice(deviceAddress: String) {
    omronManager.connectBpDevice(deviceAddress)
    // 30초 동안 연결되지 않으면 연결 실패 -> onRegisterFailed() 함수 호출됨
}
```

## 3. 측정 데이터 가져오기 (Transfer)
#### 3-1. OmronManager 객체 초기화
```kotlin
// 파라미터로 넘겨주는 sessionType이  `TRANSFER`임에 유의한다.
private val omronManager: OmronDeviceManager = OmronDeviceManager(
    omronDeviceType.omronDeviceCategory,
    OHQSessionType.TRANSFER,
    this
)
```

#### 3-2. 사용할 클래스(예: `ViewModel`)에 OmronDeviceManager.RegisterListener 인터페이스를 구현한다.
```kotlin
class DeviceTransferViewModel : ViewModel(), OmronDeviceManager.TransferListener {
    override fun onTransferFailed(reason: OHQCompletionReason?) {
        // 데이터 요청 실패 시 호출되는 콜백 메서드
        // 요청 취소 혹은 요청 시간 초과(30초)
    }

    override fun onTransferSuccess(sessionData: SessionData?) {
        // 데이터 요청 성공 시 호출되는 콜백 메서드
        
        val results = sessionData?.measurementRecords

        if (results.isNullOrEmpty) {
            // 요청은 성공했지만 새로 측정된 데이터가 없음
            return
        }

        if (omronDeviceType.isHBF222F) {
            updateBodyCompositionData(results)
            saveSequenceNumber(sessionData.getSequenceNumberOfLatestRecord())

            if (omronManager.isUserInfoChanged(sessionData)) {
                updateIncrementDataKey()
            }

            return
        }

        if (omronDeviceType.isHEM9200T || omronDeviceType.isHEM7155T) {
            updateBloodPressureData(results)
            return
        }
    }

    // 먼저 기기에서 측정 완료된 후 아래와 같이 데이터 요청
    fun requestData() {
        if (omronDeviceType.isHEM9200T || omronDeviceType.isHEM7155T) {
            omronManager.requestBpData(deviceAddress)
            return
        }

        if (omronDeviceType.isWeightDevice) {
            omronManager.requestWeightData(deviceInfo)
        }
    }
}
```