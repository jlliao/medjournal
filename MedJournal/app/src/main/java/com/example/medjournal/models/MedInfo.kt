package com.example.medjournal.models

import kotlin.collections.ArrayList

/**
 * A data class that contains a medicine information
 * @param uid user id
 * @param medName name of medicine
 * @param times how many times a day
 * @param amount dosage for each time
 * @param unit unit of dosage
 * @param medTimes an ArrayList of specific medication time
 * @param startDate starting date of medication
 * @param duration duration of medication, 0 means ongoing
 * @param days specific day of week to take dosage
 */
data class MedInfo(
    val uid: String? = "",
    val medName: String? = "",
    val times: Int = 0,
    val amount: Int = 1,
    val unit: String? = "",
    val medTimes: ArrayList<String> = ArrayList(),
    val startDate: String? = "",
    val duration: Int = 0,
    val days: ArrayList<String> = ArrayList()
)


