package com.example.medjournal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medjournal.R
import com.example.medjournal.models.MeasurementData
import kotlinx.android.synthetic.main.medication_text.view.*
import java.util.*
import kotlin.collections.ArrayList

class MeasurementHistoryRvAdapter(private val items: ArrayList<MeasurementData>, val context: Context) : RecyclerView.Adapter<MeasurementHistoryRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.measurement_history_single_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val format = java.text.SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
            Locale.ENGLISH)
        val d = format.parse(items[position].datetimeEntered)!!

        holder.meas_time_tv.text = java.text.SimpleDateFormat("hh:mm a").format(d.time)
        holder.meas_date_tv.text = java.text.SimpleDateFormat("dd MMM yyyy").format(d.date)
        holder.meas_val_tv.text = items[position].measuredVal.toString()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val meas_val_tv: TextView = v.findViewById(R.id.measurement_value_text)
        val meas_date_tv: TextView = v.findViewById(R.id.measurement_event_date_text)
        val meas_time_tv: TextView = v.findViewById(R.id.measurement_event_time_text)
    }
}