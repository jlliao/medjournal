package com.example.medjournal.models

import java.util.*

data class MeasurementData(
    val userName: String,
    val typeOfMeasurement: String,
    val datetimeEntered: String,
    val measuredVal: Float
) {

    constructor(uid: String, type: String, value: Float) : this(
        uid,
        type,
        Calendar.getInstance().time.toString(),
        value
    )
}