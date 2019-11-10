package com.example.medjournal.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.example.medjournal.R
import com.example.medjournal.models.MedEvent
import kotlinx.android.extensions.LayoutContainer

class MedEventsAdapter(val onClick: (MedEvent) -> Unit) :
    RecyclerView.Adapter<MedEventsAdapter.MedEventsViewHolder>() {

    val events = mutableListOf<MedEvent>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedEventsViewHolder {
        return MedEventsViewHolder(parent.inflate(R.layout.med_item_event))
    }

    override fun onBindViewHolder(viewHolder: MedEventsViewHolder, position: Int) {
        viewHolder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    inner class MedEventsViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        init {
            itemView.setOnClickListener {
                onClick(events[adapterPosition])
            }
        }

        fun bind(medEvent: MedEvent) {
            containerView.findViewById<TextView>(R.id.med_item_event_time_text).text = medEvent.time
            containerView.findViewById<TextView>(R.id.med_item_event_name_text).text = medEvent.medName
            containerView.findViewById<TextView>(R.id.med_item_event_info_text).text = medEvent.info
        }
    }

}