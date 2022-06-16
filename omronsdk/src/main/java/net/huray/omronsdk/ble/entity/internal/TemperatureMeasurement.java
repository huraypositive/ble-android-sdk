package net.huray.omronsdk.ble.entity.internal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import net.huray.omronsdk.utility.Bytes;
import net.huray.omronsdk.utility.StringEx;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class TemperatureMeasurement {

    private final static int SCALE = 1;
    public static final String TEMPERATURE_UNIT_CELSIUS = "℃";
    public static final String TEMPERATURE_UNIT_FAHRENHEIT = "℉";

    public static final String TEMPERATURE_TYPE_ORAL = "Oral";
    public static final String TEMPERATURE_TYPE_UNDERARM = "Underarm";
    public static final String TEMPERATURE_TYPE_BODY = "Body (general)";

    public static final String TEMPERATURE_MEASUREMENT_ERROR = "Err ";

    @NonNull
    private TemperatureMeasurementFlags mFlags;
    @Nullable
    private String mTemperature;
    @Nullable
    private BigDecimal mTemperatureType;
    @Nullable
    private String mTimeStamp;

    @NonNull
    private String mTemperatureUnitStr;
    @NonNull
    private String mTemperatureTypeStr;


    public TemperatureMeasurement(@NonNull byte[] data) {
        int offset = 0;
        
        mFlags = new TemperatureMeasurementFlags((int) data[offset]);
        offset += 1;

        try{

            BigDecimal value = new BigDecimal(Bytes.parse4BytesAsFloat32(data, offset, true).floatValue()).setScale(SCALE, RoundingMode.HALF_UP);
// The sample code when using a thermometer displaying Fahrenheit is as follows.
//          BigDecimal value = new BigDecimal(Bytes.parse4BytesAsFloat32(data, offset, true).floatValue() * 9 / 5 + 32).setScale(SCALE, RoundingMode.HALF_UP);
//          mFlags.mTemperatureUnitsBit = 1;

            mTemperature = StringEx.toDecimalString(value, SCALE, SCALE);
        }catch (NumberFormatException e){
            mTemperature = TEMPERATURE_MEASUREMENT_ERROR;
        };

        offset += 4;

        if( mFlags.mTemperatureUnitsBit == 0 ) {
            mTemperatureUnitStr = TEMPERATURE_UNIT_CELSIUS;
        } else {
            mTemperatureUnitStr = TEMPERATURE_UNIT_FAHRENHEIT;
        }

        if( mFlags.mTimeStampBit == 1 ){
            mTimeStamp = Bytes.parse7BytesAsDateString(data, offset, true);
            offset += 7;
        } else {
            mTimeStamp = null;
        }

        if( mFlags.mTemperatureTypeBit == 1 ){
            mTemperatureType = new BigDecimal(data[offset] & 0xff);
            offset += 1;
        } else {
            mTemperatureType = BigDecimal.valueOf(0xFF);
        }

        switch (mTemperatureType.intValue()){
            case 0x01:
                mTemperatureTypeStr = TEMPERATURE_TYPE_UNDERARM;
                break;
            case 0x02:
                mTemperatureTypeStr = TEMPERATURE_TYPE_BODY;
                break;
            case 0x06:
                mTemperatureTypeStr = TEMPERATURE_TYPE_ORAL;
                break;
            case 0xFF://NotSupported
            default:
                mTemperatureTypeStr = null;
                break;
        }
    }
    
    @NonNull
    public String getTemperature() {
        return mTemperature;
    }
    
    @Nullable
    public String getTimeStamp() {
        return mTimeStamp;
    }
    
    @Nullable
    public boolean isTimeStampPresent() {
        return (mFlags.mTimeStampBit == 1);
    }
    
    @Nullable
    public boolean isTemperatureTypePresent() {
        return (mFlags.mTemperatureTypeBit == 1);
    }

    @NonNull
    public String getTemperatureUnit() {
        return mTemperatureUnitStr;
    }

    @NonNull
    public String getTemperatureType() {
        return mTemperatureTypeStr;
    }

    @Override
    public String toString() {
        return "TemperatureMeasurement{" +
                "mFlags=" + mFlags + 
                ", mTemperature=" + mTemperature +
                ", mTimeStamp='" + mTimeStamp + '\'' +
                ", mTemperatureType=" + mTemperatureType +
                '}';
    }
    
    private class TemperatureMeasurementFlags {
        public int mTemperatureUnitsBit = -1;
        public int mTimeStampBit = -1;
        public int mTemperatureTypeBit = -1;
        
        public TemperatureMeasurementFlags(int value){
            mTemperatureUnitsBit        = ((value >> 0) & 0x0001);
            mTimeStampBit               = ((value >> 1) & 0x0001);
            mTemperatureTypeBit         = ((value >> 2) & 0x0001);
        }
    }
}
