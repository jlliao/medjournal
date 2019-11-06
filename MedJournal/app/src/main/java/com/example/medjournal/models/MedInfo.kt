package com.example.medjournal.models

import java.util.*
import kotlin.collections.ArrayList

data class MedInfo(val uid: String, val med_name: String, val times: Date, val amount: Int, val unit: String,
                   val times_array: ArrayList<Date>, val start_date: Date, val duration: Int, val days: ArrayList<String>)


