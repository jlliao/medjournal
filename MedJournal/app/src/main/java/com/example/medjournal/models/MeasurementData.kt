package com.example.medjournal.models

import java.util.*

data class MeasurementData(
    val userName: String,
    val typeOfMeasurement: String,
    val datetimeEntered: String,
    val measuredVal: Float
) {
    constructor() : this("UserX", "cholesterol", Date().toString(), 7.5f)
    constructor(type: String, value: Float) : this(
        "UserX",
        type,
        Calendar.getInstance().time.toString(),
        value
    )

    constructor(uid: String, type: String, value: Float) : this(
        uid,
        type,
        Calendar.getInstance().time.toString(),
        value
    )
}