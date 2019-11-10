package com.example.medjournal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medjournal.R
import kotlinx.android.synthetic.main.medication_text.view.*

class MeasurementMenuRvAdapter(private val items: ArrayList<String>, val context: Context) : RecyclerView.Adapter<MeasurementMenuRvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.measurement_menu_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name_tv.text = items[position]
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) { // OnClickListner
        val name_tv: TextView = v.findViewById(R.id.single_measurement_name)
    }
}