package com.example.medjournal.models

import java.util.*
import kotlin.collections.ArrayList

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


