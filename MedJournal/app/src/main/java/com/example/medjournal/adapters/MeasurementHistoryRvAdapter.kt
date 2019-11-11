package com.example.medjournal.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medjournal.R
import com.example.medjournal.models.MeasurementData
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

/** Adapter that listens to changes to Measurements ArrayList
 */
@Suppress("DEPRECATION")
class MeasurementHistoryRvAdapter(private val items: ArrayList<MeasurementData>, val context: Context) : RecyclerView.Adapter<MeasurementHistoryRvAdapter.RecentMeasurementsViewHolder>() {

    /** Return RecentMeasurementsViewHolder when ViewHolder is created
     * @return RecentMeasurementsViewHolder which holds view of each individual item
     * inside MeasurementHistoryRecyclerview
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecentMeasurementsViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.measurement_history_single_item, parent, false)
        return RecentMeasurementsViewHolder(view)
    }

    /** Bind view to changes to Measurements ArrayList
     */
    override fun onBindViewHolder(holder: RecentMeasurementsViewHolder, position: Int) {
        val format = SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
            Locale.ENGLISH)
        val d = format.parse(items[position].datetimeEntered)!!

        holder.measTimeTv.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(d.time)
        holder.measDateTv.text = SimpleDateFormat("dd MMM y").format(d)
        holder.measValTv.text = items[position].measuredVal.toString()
    }

    /** Return the number of measurements
     */
    override fun getItemCount(): Int {
        return items.size
    }

    /** ViewHolder that holds data for measurement history RecyclerView items (recent measurements)
     */
    class RecentMeasurementsViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val measValTv: TextView = v.findViewById(R.id.measurement_value_text)
        val measDateTv: TextView = v.findViewById(R.id.measurement_event_date_text)
        val measTimeTv: TextView = v.findViewById(R.id.measurement_event_time_text)
    }
}