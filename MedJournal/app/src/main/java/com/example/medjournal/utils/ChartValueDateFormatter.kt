package com.example.medjournal.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class ChartValueDateFormatter(private val targetDateFormat: String) : IndexAxisValueFormatter() {
    // TODO: fix this. Currently x-axis is empty
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return SimpleDateFormat(
            targetDateFormat,
            Locale.getDefault()
        ).format(Date(value.toLong() * 1000))
    }
}