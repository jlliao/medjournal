package com.example.medjournal.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medjournal.R
import com.example.medjournal.models.MedEvent
import kotlinx.android.extensions.LayoutContainer

/** Adapter that listens to changes in medication events (each reminder item)
 */
class MedEventsAdapter(val onClick: (MedEvent) -> Unit) :
    RecyclerView.Adapter<MedEventsAdapter.MedEventsViewHolder>() {

    /** Represents mutable list of medication events
     */
    val events = mutableListOf<MedEvent>()

    /** Return MedEventsViewHolder when ViewHolder is created
     * @return MedEventsViewHolder that holds view of individual item inside RecyclerView
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedEventsViewHolder {
        return MedEventsViewHolder(parent.inflate(R.layout.med_item_event))
    }

    /** Bind view with medication events
     */
    override fun onBindViewHolder(viewHolder: MedEventsViewHolder, position: Int) {
        viewHolder.bind(events[position])
    }

    /** Get number of events
     */
    override fun getItemCount(): Int = events.size

    /** ViewHolder that holds medication events (reminder item)
     */
    inner class MedEventsViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        /** Add onClick listener to each item in RecyclerView
         */
        init {
            itemView.setOnClickListener {
                onClick(events[adapterPosition])
            }
        }

        /** Bind properties of medication event with TextView in RecyclerView item
         */
        fun bind(medEvent: MedEvent) {
            containerView.findViewById<TextView>(R.id.med_item_event_time_text).text = medEvent.time
            containerView.findViewById<TextView>(R.id.med_item_event_name_text).text =
                medEvent.medName
            containerView.findViewById<TextView>(R.id.med_item_event_info_text).text = medEvent.info
        }
    }

}