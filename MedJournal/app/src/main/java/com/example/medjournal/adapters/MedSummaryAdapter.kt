package com.example.medjournal.adapters

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.medjournal.R
import com.example.medjournal.models.MedInfo
import kotlinx.android.extensions.LayoutContainer

/** Adapter that listens to changes in medication info (each medicine item)
 */
class MedSummaryAdapter(val onClick: (MedInfo) -> Unit) :
    RecyclerView.Adapter<MedSummaryAdapter.MedSummaryViewHolder>() {

    /** Represents mutable list of medication info
     */
    val events = mutableListOf<MedInfo>()

    /** Return MedSummaryViewHolder when ViewHolder is created
     * @return MedSummaryViewHolder that holds view of individual item inside RecyclerView
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedSummaryViewHolder {
        return MedSummaryViewHolder(parent.inflate(R.layout.med_summary_event))
    }

    /** Bind view with medication info
     */
    override fun onBindViewHolder(viewHolder: MedSummaryViewHolder, position: Int) {
        viewHolder.bind(events[position])
    }

    /** Get number of events
     */
    override fun getItemCount(): Int = events.size

    /** ViewHolder that holds medication info (medicine item)
     */
    inner class MedSummaryViewHolder(override val containerView: View) :
        RecyclerView.ViewHolder(containerView), LayoutContainer {

        /** Add onClick listener to each item in RecyclerView
         */
        init {
            itemView.setOnClickListener {
                onClick(events[adapterPosition])
            }
        }

        /** Bind properties of medication info with TextView in RecyclerView item
         */
        fun bind(medInfo: MedInfo) {
            containerView.findViewById<TextView>(R.id.med_summary_event_name_text).text =
                medInfo.medName
            containerView.findViewById<TextView>(R.id.med_summary_event_info_text).text =
                "started date: " + medInfo.startDate
        }
    }

}