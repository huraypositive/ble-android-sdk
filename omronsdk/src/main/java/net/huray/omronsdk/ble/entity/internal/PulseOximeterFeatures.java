package net.huray.omronsdk.ble.entity.internal;

import androidx.annotation.NonNull;

import net.huray.omronsdk.utility.Bytes;

public class PulseOximeterFeatures {
    public PulseOximeterSupportedFeatures mSupportedFlags;
    public int mMeasurementStatusSupport;
    public long mDeviceAndSensorStatusSupport;

    public PulseOximeterFeatures(@NonNull byte[] data){
        int index = 0;

        int flags = Bytes.parse2BytesAsInt(data, index, true);
        mSupportedFlags = new PulseOximeterSupportedFeatures( flags );

        if( mSupportedFlags.mMeasurementStatusSupportBit == 1 ){
            mMeasurementStatusSupport = Bytes.parse2BytesAsInt(data, index, true);
            index += 2;
        }

        if( mSupportedFlags.mDeviceAndSensorStatusSupportBit == 1 ){
            byte[] tmpData = new byte[4];
            for(int i = 0; i < 3; i++){
                tmpData[i] = data[index + i];
            }
            tmpData[3] = 0;
            mDeviceAndSensorStatusSupport = Bytes.parse4BytesAsLong(tmpData, 0, true);
        }
    }
    
    @NonNull
    public boolean isMeasurementStatusSupportPresent() {
        return (mSupportedFlags.mDeviceAndSensorStatusSupportBit == 1);
    }
    
    @NonNull
    public boolean isDeviceAndSensorStatusSupportPresent() {
        return (mSupportedFlags.mMeasurementStatusSupportBit == 1);
    }
    
    @NonNull
    public boolean isMeasurementStoragePresent() {
        return (mSupportedFlags.mMeasurementStorageForSpotCheckMeasurementsBit == 1);
    }
    
    @NonNull
    public boolean isTimeStampPresent() {
        return (mSupportedFlags.mTimeStampForSpotCheckMeasurementsBit == 1);
    }
    
    @NonNull
    public boolean isPulseAmplitudeIndexPresent() {
        return (mSupportedFlags.mPulseAmplitudeIndexFieldBit == 1);
    }
    
    @Override
    public String toString() {
        return "PulseOximeterFeatures{" +
                "mSupportedFlags=" + mSupportedFlags +
                "mMeasurementStatusSupport=" + mMeasurementStatusSupport +
                "mDeviceAndSensorStatusSupport=" + mDeviceAndSensorStatusSupport +
                '}';
    }
    
    private class PulseOximeterSupportedFeatures {
        public int mMeasurementStatusSupportBit = 0;
        public int mDeviceAndSensorStatusSupportBit = 0;
        public int mMeasurementStorageForSpotCheckMeasurementsBit = 0;
        public int mTimeStampForSpotCheckMeasurementsBit = 0;
        public int mSpO2PrFastMetricBit = 0;
        public int mSpO2PrSlowMetricBit = 0;
        public int mPulseAmplitudeIndexFieldBit = 0;
        public int mMultipleBondsFeatureBit = 0;
        
        public PulseOximeterSupportedFeatures(int value){
            mMeasurementStatusSupportBit                    = (value >> 0) & 0x0001;
            mDeviceAndSensorStatusSupportBit                = (value >> 1) & 0x0001;
            mMeasurementStorageForSpotCheckMeasurementsBit  = (value >> 2) & 0x0001;
            mTimeStampForSpotCheckMeasurementsBit           = (value >> 3) & 0x0001;
            mSpO2PrFastMetricBit                            = (value >> 4) & 0x0001;
            mSpO2PrSlowMetricBit                            = (value >> 5) & 0x0001;
            mPulseAmplitudeIndexFieldBit                    = (value >> 6) & 0x0001;
            mMultipleBondsFeatureBit                        = (value >> 7) & 0x0001;
        }
    }
}

