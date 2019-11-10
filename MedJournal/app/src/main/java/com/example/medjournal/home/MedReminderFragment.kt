package com.example.medjournal.home


import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medjournal.R
import com.example.medjournal.models.MedInfo
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import kotlinx.android.extensions.LayoutContainer
import org.threeten.bp.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import kotlin.collections.ArrayList

internal fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

data class MedEvent(val info: String, val time: String,
                    val medName: String, val date: String)

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

/**
 * A simple [Fragment] subclass.
 */
class MedReminderFragment : Fragment(), OnDateSelectedListener {

    private val eventsAdapter = MedEventsAdapter {

    }

    private lateinit var widget: MaterialCalendarView
    private val medications: MutableList<MedInfo> = ArrayList()
    private val events = mutableMapOf<String, List<MedEvent>>()
    private var selectedDate = SimpleDateFormat("mm/dd/yyyy").format(Calendar.getInstance().time)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.test_username)

        val medView = inflater.inflate(R.layout.fragment_med_reminder, container, false)
        widget = medView.findViewById(R.id.calendarView)
        widget.setOnDateChangedListener(this)

        val addMedBtn = medView.findViewById<FloatingActionButton>(R.id.add_med_button)
        addMedBtn.setOnClickListener {
            medView.findNavController().navigate(R.id.action_home_to_med)
        }

        getMedInfoFromFirebaseDatabase()

        val medRv = medView.findViewById<RecyclerView>(R.id.med_item_event_rv)
        medRv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        medRv.adapter = eventsAdapter
        medRv.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))

        return medView
    }

    override fun onDateSelected(
        widget: MaterialCalendarView,
        date: CalendarDay,
        selected: Boolean
    ) {
        //if (selected) Log.d("Calendar", "Current Date: ${date.date}") else "No date selected"
        if (selected) {
            val date = DateTimeFormatter.ofPattern("MM/dd/yyyy").format(date.date)
            if (selectedDate != date) {
                selectedDate = date
                updateAdapterForDate(date)
            }
//            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), getString(R.string.toast_no_selection), Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateAdapterForDate(date: String) {
        eventsAdapter.events.clear()
        eventsAdapter.events.addAll(events[date].orEmpty())
        eventsAdapter.notifyDataSetChanged()
    }

    private fun getMedInfoFromFirebaseDatabase() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().reference.child("medications").child(uid!!)

        val medInfoListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(HomeActivity.TAG, "loadMedInfo:onCancelled", databaseError.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                medications.clear()
                events.clear()
                //medications = ArrayList()

                dataSnapshot.children.forEach {
                    val medication = it.getValue(MedInfo::class.java)
                    medications.add(medication!!)
//                    Log.d(TAG, medications[0].medName ?: "NULLL")
                    parseMedInfoIntoMedEvent(medication)

                }
                Log.d(HomeActivity.TAG, "Update MedInfo Successfully")

            }
        }

        ref.addListenerForSingleValueEvent(medInfoListener)
    }

    private fun parseMedInfoIntoMedEvent(medInfo: MedInfo) {
        val medItemInfo = medInfo.amount.toString() + " " + medInfo.unit
        for (i in 0 until medInfo.times) {

            when (medInfo.days[0]) {
                "Every day" -> {
                    var startDate = SimpleDateFormat("MM/dd/yyyy").parse(medInfo.startDate!!)
                    for (j in 0 until 15) {
                        val sDate = SimpleDateFormat("MM/dd/yyyy").format(startDate)
                        val singleMedEvent = MedEvent(medItemInfo, medInfo.medTimes[i], medInfo.medName!!, sDate)
                        events[sDate] = events[sDate].orEmpty().plus(singleMedEvent)
                        val c = Calendar.getInstance()
                        c.time = startDate
                        c.add(Calendar.DAY_OF_MONTH, 1)
                        startDate = c.time
                    }
                }
                "Every other day" -> {
                    var startDate = SimpleDateFormat("MM/dd/yyyy").parse(medInfo.startDate!!)
                    for (j in 0 until 15) {
                        val sDate = SimpleDateFormat("MM/dd/yyyy").format(startDate)
                        val singleMedEvent = MedEvent(medItemInfo, medInfo.medTimes[i], medInfo.medName!!, sDate)
                        events[sDate] = events[sDate].orEmpty().plus(singleMedEvent)
                        val c = Calendar.getInstance()
                        c.time = startDate
                        c.add(Calendar.DAY_OF_MONTH, 2)
                        startDate = c.time
                    }
                }
                else -> Toast.makeText(requireContext(), "Function not implemented for specific days", Toast.LENGTH_LONG)
                    .show()

            }

        }
    }


}