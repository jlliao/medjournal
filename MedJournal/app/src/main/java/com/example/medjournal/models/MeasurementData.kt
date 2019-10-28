package com.example.medjournal.models

import com.google.firebase.database.DatabaseReference
import java.util.*

data class MeasurementData(val userName: String, val typeOfMeasurement: String, val datetimeEntered: String, val measuredVal: Float) {
    constructor(): this("UserX", "cholesterol", Date().toString(), 7.5f)
    constructor(type: String, value: Float): this("UserX",type, Date().toString(), value)
    constructor(uid: String, type: String, value: Float): this(uid,type, Date().toString(), value)

    fun saveToFirebase(myRef : DatabaseReference) {
        myRef.child("measurements").child(userName).child(typeOfMeasurement).child(datetimeEntered).setValue(measuredVal)
    }
}