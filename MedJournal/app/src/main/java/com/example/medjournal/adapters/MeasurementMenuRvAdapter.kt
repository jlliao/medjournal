package com.example.medjournal.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medjournal.R

/** Adapter that listens to changes in measurement types holder (each different measurement type added / removed)
 */
class MeasurementMenuRvAdapter(private val items: ArrayList<String>, val context: Context) :
    RecyclerView.Adapter<MeasurementMenuRvAdapter.MeasurementTypeViewHolder>() {

    /** Return a new MeasurementMenuRvAdapter when ViewHolder is created
     * @return MeasurementMenuRvAdapter that holds a view of a Measurement type textview in RecyclerView
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeasurementTypeViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.measurement_menu_item, parent, false)
        return MeasurementTypeViewHolder(view)
    }

    /** Bind view with a Measurement type
     */
    override fun onBindViewHolder(holder: MeasurementTypeViewHolder, position: Int) {
        holder.nameTv.text = items[position]
    }

    /** Return a number of unique Measurement types
     */
    override fun getItemCount(): Int {
        return items.size
    }

    /** Viewholder that holds a view of a Measurement type textview in RecyclerView
     */
    class MeasurementTypeViewHolder(v: View) : RecyclerView.ViewHolder(v) { // OnClickListner
        val nameTv: TextView = v.findViewById(R.id.single_measurement_name)
    }
}