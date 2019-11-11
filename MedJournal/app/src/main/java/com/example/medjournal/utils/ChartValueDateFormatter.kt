package com.example.medjournal.utils

import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import java.text.SimpleDateFormat
import java.util.*

/** A class for formatting datetime values for X-axes in charts
 * @param targetDateFormat a string format into which the datetime object should be converted
 */
class ChartValueDateFormatter(private val targetDateFormat: String) : IndexAxisValueFormatter() {
    /* TODO: fix this. Currently x-axis is empty */

    /** Formatting method, which returns a string with the date in correct format
     * @param value float value of the date
     * @param axis x or y axis (we only use x-axis)
     * @return a string with the date in correct format
     */
    override fun getFormattedValue(value: Float, axis: AxisBase?): String {
        return SimpleDateFormat(
            targetDateFormat,
            Locale.getDefault()
        ).format(Date(value.toLong() * 1000))
    }
}