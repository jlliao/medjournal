package com.example.medjournal.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView
import com.example.medjournal.R
import com.example.medjournal.models.MedInfo
import kotlinx.android.extensions.LayoutContainer

class MedSummaryAdapter(val onClick: (MedInfo) -> Unit) :
    RecyclerView.Adapter<MedSummaryAdapter.MedSummaryViewHolder>() {

    val events = mutableListOf<MedInfo>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedSummaryViewHolder {
        return MedSummaryViewHolder(parent.inflate(R.layout.med_summary_event))
    }

    override fun onBindViewHolder(viewHolder: MedSummaryViewHolder, position: Int) {
        viewHolder.bind(events[position])
    }

    override fun getItemCount(): Int = events.size

    inner class MedSummaryViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        init {
            itemView.setOnClickListener {
                onClick(events[adapterPosition])
            }
        }

        fun bind(medInfo: MedInfo) {
            containerView.findViewById<TextView>(R.id.med_summary_event_name_text).text =
                medInfo.medName
            containerView.findViewById<TextView>(R.id.med_summary_event_info_text).text =
                "started date: " + medInfo.startDate
        }
    }

}