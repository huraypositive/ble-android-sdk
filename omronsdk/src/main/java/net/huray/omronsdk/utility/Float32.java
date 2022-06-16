package net.huray.omronsdk.utility;

//IEEE-11073 32-bit FLOAT
public class Float32 {

    public static int NaN = 0x007FFFFF;
    private int value;

    public Float32(int value) {
        this.value = value;
    }

    public static Float32 valueOf(int value) {
        return new Float32(value);
    }

    public float floatValue() {
        if (value == NaN) {
            return Float.NaN;
        } else {
            return (float) ((double) getMantissa(value) * Math.pow(10, getExponent(value)));
        }
    }

    public double doubleValue() {
        if (value == NaN) {
            return Double.NaN;
        } else {
            return ((double) getMantissa(value)) * Math.pow(10, getExponent(value));
        }
    }

    private int getExponent(int value) {
        return (int) ((value >> 24));
    }

    private int getMantissa(int value) {
        int mantissa = (value & 0x00FFFFFF);
        if ((mantissa & 0x00800000) != 0) {
            mantissa = -(0x1000000 - mantissa);
        }
        return mantissa;
    }
}
