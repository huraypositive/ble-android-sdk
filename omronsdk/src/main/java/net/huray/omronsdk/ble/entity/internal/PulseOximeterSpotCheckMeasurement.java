package net.huray.omronsdk.ble.entity.internal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.huray.omronsdk.utility.Bytes;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PulseOximeterSpotCheckMeasurement {

    private final static int SCALE = 3;

    @NonNull
    private PulseOximeterSpotCheckMeasurementFlags mFlags;
    @Nullable
    private BigDecimal mSpO2;
    @Nullable
    private BigDecimal mPulseRate;
    @Nullable
    private String mTimeStamp;
    private int mMeasurementStatus;
    private long mDeviceAndSensorStatus;
    @Nullable
    private BigDecimal mPulseAmplitudeIndex;
    
    public PulseOximeterSpotCheckMeasurement(@NonNull byte[] data) {
        int index = 0;
        
        mFlags = new PulseOximeterSpotCheckMeasurementFlags((int) data[index]);
        index += 1;
        
        mSpO2 = new BigDecimal(Bytes.parse2BytesAsSFloat(data, index, true).floatValue()).setScale(SCALE, RoundingMode.HALF_UP);
        index += 2;
        
        mPulseRate = new BigDecimal(Bytes.parse2BytesAsSFloat(data, index, true).floatValue()).setScale(SCALE, RoundingMode.HALF_UP);
        index += 2;
        
        if( mFlags.mTimeStampBit == 1 ){
            mTimeStamp = Bytes.parse7BytesAsDateString(data, index, true);
            index += 7;
        } else {
            mTimeStamp = null;
        }

        if( mFlags.mMeasurementStatusBit == 1 ){
            mMeasurementStatus = Bytes.parse2BytesAsInt(data, index, true);
            index += 2;
        } else {
            mMeasurementStatus = 0;
        }

        if( mFlags.mDeviceAndSensorStatusBit == 1 ){
            byte[] tmpData = new byte[4];
            for(int i = 0; i < 3; i++){
                tmpData[i] = data[index + i];
            }
            tmpData[3] = 0;
            mDeviceAndSensorStatus = Bytes.parse4BytesAsLong(tmpData, 0, true);
            index += 3;
        } else {
            mDeviceAndSensorStatus = 0;
        }
        
        if( mFlags.mPulseAmplitudeIndexBit == 1 ){
            mPulseAmplitudeIndex = new BigDecimal(Bytes.parse2BytesAsSFloat(data, index, true).floatValue()).setScale(SCALE, RoundingMode.HALF_UP);
        } else {
            mPulseAmplitudeIndex = null;
        }
    }
    
    @NonNull
    public BigDecimal getSpO2() {
        return mSpO2;
    }
    
    @Nullable
    public BigDecimal getPulseRate() {
        return mPulseRate;
    }
    
    @Nullable
    public String getTimeStamp() {
        return mTimeStamp;
    }
    
    @Nullable
    public int getMeasurementStatus() {
        return mMeasurementStatus;
    }
    
    @Nullable
    public long getDeviceAndSensorStatus() {
        return mDeviceAndSensorStatus;
    }
    
    @Nullable
    public BigDecimal getPulseAmplitudeIndex() {
        return mPulseAmplitudeIndex;
    }
    
    @Nullable
    public boolean isTimeStampPresent() {
        return (mFlags.mTimeStampBit == 1);
    }
    
    @Nullable
    public boolean isMeasurementStatusPresent() {
        return (mFlags.mMeasurementStatusBit == 1);
    }
    
    @Nullable
    public boolean isDeviceAndSensorStatusPresent() {
        return (mFlags.mDeviceAndSensorStatusBit == 1);
    }
    
    @Nullable
    public boolean isPulseAmplitudeIndexPresent() {
        return (mFlags.mPulseAmplitudeIndexBit == 1);
    }
    
    @Nullable
    public boolean isDeviceClockIsNotSet() {
        return (mFlags.mDeviceClockIsNotSetBit == 1);
    }
    
    @Override
    public String toString() {
        return "PulseOximeterSpotCheckMeasurement{" +
                "mFlags=" + mFlags + 
                ", mSpO2=" + mSpO2 +
                ", mPulseRate=" + mPulseRate +
                ", mTimeStamp='" + mTimeStamp + '\'' +
                ", mMeasurementStatus=" + mMeasurementStatus +
                ", mDeviceAndSensorStatus=" + mDeviceAndSensorStatus +
                ", mPulseAmplitudeIndex=" + mPulseAmplitudeIndex +
                '}';
    }
    
    private class PulseOximeterSpotCheckMeasurementFlags {
       public int mTimeStampBit = -1;
        public int mMeasurementStatusBit = -1;
        public int mDeviceAndSensorStatusBit = -1;
        public int mPulseAmplitudeIndexBit = -1;
        public int mDeviceClockIsNotSetBit = -1;
        
        public PulseOximeterSpotCheckMeasurementFlags(int value){
            mTimeStampBit               = ((value >> 0) & 0x0001);
            mMeasurementStatusBit       = ((value >> 1) & 0x0001);
            mDeviceAndSensorStatusBit   = ((value >> 2) & 0x0001);
            mPulseAmplitudeIndexBit     = ((value >> 3) & 0x0001);
            mDeviceClockIsNotSetBit     = ((value >> 4) & 0x0001);
        }
    }
}
