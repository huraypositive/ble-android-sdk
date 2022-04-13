package net.huray.omronsdk.model

abstract class OmronHealthData(
    open val timeStamp: String,
) {

    data class WeightData(
        override val timeStamp: String,
        val bodyFat: Float,
        val weight: Float
    ) : OmronHealthData(timeStamp)

    data class BpData(
        override val timeStamp: String,
        val sbp: Float,
        val dbp: Float,
        val pulseRate: Float
    ) : OmronHealthData(timeStamp)
}