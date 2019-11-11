package com.example.medjournal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medjournal.R
import com.example.medjournal.models.MeasurementData
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MeasurementHistoryRvAdapter(private val items: ArrayList<MeasurementData>, val context: Context) : RecyclerView.Adapter<MeasurementHistoryRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.measurement_history_single_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val format = SimpleDateFormat("EE MMM dd HH:mm:ss z yyyy",
            Locale.ENGLISH)
        val d = format.parse(items[position].datetimeEntered)!!

        holder.measTimeTv.text = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(d.time)
        holder.measDateTv.text = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(d.date)
        holder.measValTv.text = items[position].measuredVal.toString()
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val measValTv: TextView = v.findViewById(R.id.measurement_value_text)
        val measDateTv: TextView = v.findViewById(R.id.measurement_event_date_text)
        val measTimeTv: TextView = v.findViewById(R.id.measurement_event_time_text)
    }
}