# Omron Android SDK
오므론 블루투스 기기 SDK의 사용을 단순하게 하기 위한 라이브러리 (OMRON 샘플코드 v4.1.4 기준)

## Jitpack 배포
해당 라이브러리는 jitpack으로 배포되었으며, 아래의 과정을 통해 새 버전을 배포할 수 있다.
- 깃허브 저장소에서 새로운 태그를 지정한다.
- [Jitpack](https://jitpack.io/) 홈페이지의 `Git repo url` 검색창에 `huraypositive/omron-android-sdk`를 넣어 repo를 찾은 후, 새로 추가한 태그의 버전을 빌드한다.
- 안드로이드 프로젝트에서 새로 빌드된 버전으로 업데이트하여 정상 배포되었는지를 테스트한다.

## 적용 기기
- 오므론 체성분계 [Omron HBF-222T]
- 오므론 혈압계 [Omron HEM-9200T]
- 오므론 혈압계 [Omron HEM-7155T]
- 오므론 혈압계 [Omron HEM-7142T]

## 의존성 추가
```gradle
// in your settings.gradle
dependencyResolutionManagement {
    repositories {
        ...
        mavenCentral()
        maven { url "https://jitpack.io" }
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
<uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />

```
- Location, Scan, Connect 권한은 런타임에서 명시적으로 요청해야 한다.
- Android 기기의 OS 버전이 12 이상인 경우, OmronDeviceManager 객체를 초기화하기 전에 Scan, Connect의 권한이 허용되어있지 않다면 `java.lang.SecurityException` 예외가 발생한다. 따라서 권한이 허용되어있지 않았을 때 OmronDeviceManager를 사용하는 화면으로 진입하지 못하도록 로직을 구성해야 함에 유의한다. 
- [공식문서](https://developer.android.com/guide/topics/connectivity/bluetooth/permissions) 참고

## 1. 기기 등록 (Register)
#### 1-1. OmronManager 객체 초기화
```kotlin
// 파라미터로 넘겨주는 sessionType이  `REGISTER`임에 유의한다.
private val omronManager: OmronDeviceManager = OmronDeviceManager(
    context, 
    deviceCategory,
    OHQSessionType.REGISTER,
    this
)
```

#### 1-2. 사용할 클래스(예: `ViewModel`)에 OmronDeviceManager.RegisterListener 인터페이스를 구현한다.
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

#### 1-3. 기기 스캔
```kotlin
fun startScan(targetDevices: List<OmronDeviceType>) {
    omronManager.startScan(targetDevices)
}
```

#### 1-4a. 기기 연결 (체성분계 - HBF-222T)
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

#### 1-4a. 기기 연결 (혈압계 - HEM-9200T, HEM-7155T, HEM-7142T)
```kotlin
private fun connectBpDevice(deviceAddress: String) {
    omronManager.connectBpDevice(deviceAddress)
    // 30초 동안 연결되지 않으면 연결 실패 -> onRegisterFailed() 함수 호출됨
}
```
`NOTE`: HEM-7155T 모델의 경우 기기에서 userIndex를 선택할 수 있으나, 측정 기록을 가져올 때 userIndex를 기기로 전달해서 인덱스에 해당하는 기록만 가져오는 기능을 제공하지 않는다. 
따라서 해당 기능을 구현하려면 userIndex 값을 SharedPreferences등을 사용해 로컬에 저장한 후 측정시 넘어온 데이터에서 userIndex를 필터링 하여 사용하는 수밖에 없다.

## 2. 측정 데이터 가져오기 (Transfer)
#### 2-1. OmronManager 객체 초기화
```kotlin
// 파라미터로 넘겨주는 sessionType이  `TRANSFER`임에 유의한다.
private val omronManager: OmronDeviceManager = OmronDeviceManager(
    context,
    deviceCategory,
    OHQSessionType.TRANSFER,
    this
)
```

#### 2-2. 사용할 클래스(예: `ViewModel`)에 OmronDeviceManager.RegisterListener 인터페이스를 구현한다.
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
        if (omronDeviceType.isBloodPressureMonitor()) {
            omronManager.requestBpData(deviceAddress)
            return
        }

        if (omronDeviceType.isWeightDevice()) {
            omronManager.requestWeightData(deviceInfo)
        }
    }
}
```
