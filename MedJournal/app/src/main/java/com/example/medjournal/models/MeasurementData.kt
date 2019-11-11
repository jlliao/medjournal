package com.example.medjournal.models

import java.util.*

/**
 * A data class of a measurement (holds each measurement entry)
 * @param userName user ID of the user who entered this measurement
 * @param typeOfMeasurement type of measurement entry (e.g. heart rate, cholesterol level, weight)
 * @param datetimeEntered date and time when the measurement was entered
 * @param measuredVal a float value of the measurement entry
 */
data class MeasurementData(
    val userName: String,
    val typeOfMeasurement: String,
    val datetimeEntered: String,
    val measuredVal: Float
) {
    /** Returns a sample Measurement data object (used only for testing)
     */
    constructor() : this("UserX", "cholesterol", Date().toString(), 7.5f)
    /** Returns a sample Measurement data object (used only for testing)
     */
    constructor(type: String, value: Float) : this(
        "UserX",
        type,
        Calendar.getInstance().time.toString(),
        value
    )
    /** Returns a default Measurement data object (used in production)
     */
    constructor(uid: String, type: String, value: Float) : this(
        uid,
        type,
        Calendar.getInstance().time.toString(),
        value
    )
}