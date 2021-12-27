package net.huray.omronsdk.model;

public class WeightData {
    final String timeStamp;
    final float bodyFat;
    final float weight;

    public WeightData(String timeStamp, float bodyFat, float weight) {
        this.timeStamp = timeStamp;
        this.bodyFat = bodyFat;
        this.weight = weight;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public float getBodyFat() {
        return bodyFat;
    }

    public float getWeight() {
        return weight;
    }
}
