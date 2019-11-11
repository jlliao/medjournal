package com.example.medjournal.home


import android.os.Bundle
import android.provider.UserDictionary.Words.LOCALE
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.navigation.ActivityNavigator
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medjournal.R
import com.example.medjournal.adapters.MedEventsAdapter
import com.example.medjournal.models.MedEvent
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
import java.util.*
import kotlin.collections.ArrayList


/**
 * MedReminderFragement that controls the view of medication reminder
 */
class MedReminderFragment : Fragment(), OnDateSelectedListener {

    /**
     * Add medication events adapter
     */
    private val eventsAdapter = MedEventsAdapter {

    }

    /**
     * Add calendar view
     */
    private lateinit var widget: MaterialCalendarView

    /**
     * List of all medication info
     */
    private val medications: MutableList<MedInfo> = ArrayList()

    /**
     * Hashmap of medication events by key of date formatted in String
     */
    private val events = mutableMapOf<String, List<MedEvent>>()

    /**
     * selected date in calendar, default is today
     */
    private var selectedDate =
        SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(Calendar.getInstance().time)

    /**
     * Create view of medication reminder
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /**
         * Sudo user name on tool bar
         */
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.test_username)

        /**
         * Add onDateChangedListener for calendar view
         */
        val medView = inflater.inflate(R.layout.fragment_med_reminder, container, false)
        widget = medView.findViewById(R.id.calendarView)
        widget.setOnDateChangedListener(this)

        /**
         * Add medication button that directs user to MedActivity
         */
        val addMedBtn = medView.findViewById<FloatingActionButton>(R.id.add_med_button)
        addMedBtn.setOnClickListener {
            medView.findNavController().navigate(R.id.action_home_to_med)
        }

        getMedInfoFromFirebaseDatabase()

        /**
         * Set up adapter for recycler view
         */
        val medRv = medView.findViewById<RecyclerView>(R.id.med_item_event_rv)
        medRv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        medRv.adapter = eventsAdapter
        medRv.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))

        return medView
    }

    /**
     * Update adapter when selected date changes
     * @param widget calendar view
     * @param date selected date
     * @param selected whether date selected or not
     */
    override fun onDateSelected(
        widget: MaterialCalendarView,
        date: CalendarDay,
        selected: Boolean
    ) {
        //if (selected) Log.d("Calendar", "Current Date: ${date.date}") else "No date selected"
        if (selected) {
            val dates = DateTimeFormatter.ofPattern("MM/dd/yyyy").format(date.date)
            if (selectedDate != dates) {
                selectedDate = dates
                updateAdapterForDate(dates)
            }
//            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.toast_no_selection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Update adpater with specific date
     * @param date specific date string
     */
    private fun updateAdapterForDate(date: String) {
        eventsAdapter.events.clear()
        eventsAdapter.events.addAll(events[date].orEmpty())
        eventsAdapter.notifyDataSetChanged()
    }

    /**
     * Retrieve and listen to medication info from Firebase Database
     */
    private fun getMedInfoFromFirebaseDatabase() {
        val uid = FirebaseAuth.getInstance().uid
        val ref =
            FirebaseDatabase.getInstance().reference.child("medications").child(uid ?: "UserX")

        val medInfoListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(HomeActivity.TAG, "loadMedInfo:onCancelled", databaseError.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                medications.clear()
                events.clear()

                dataSnapshot.children.forEach {
                    val medication = it.getValue(MedInfo::class.java)
                    medications.add(medication!!)

                    parseMedInfoIntoMedEvent(medication)

                }

                eventsAdapter.notifyDataSetChanged()
                Log.d(HomeActivity.TAG, "Update MedInfo Successfully")

            }
        }

        ref.addListenerForSingleValueEvent(medInfoListener)
    }

    /**
     * A parser that parses medication info into medication event (reminder object)
     */
    private fun parseMedInfoIntoMedEvent(medInfo: MedInfo) {
        val medItemInfo = medInfo.amount.toString() + " " + medInfo.unit
        val dur = if (medInfo.duration == 0) 30 else medInfo.duration
        for (i in 0 until medInfo.times) {
            if (medInfo.days.isEmpty()) medInfo.days.add(getString(R.string.tv_every_day))
            when (medInfo.days[0]) {
                getString(R.string.tv_every_day) -> {
                    var startDate = SimpleDateFormat(
                        "MM/dd/yyyy",
                        Locale.getDefault()
                    ).parse(medInfo.startDate!!)
                    for (j in 0 until dur) {
                        val sDate =
                            SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(startDate!!)
                        val singleMedEvent =
                            MedEvent(medItemInfo, medInfo.medTimes[i], medInfo.medName!!, sDate)
                        events[sDate] = events[sDate].orEmpty().plus(singleMedEvent)
                        val c = Calendar.getInstance()
                        c.time = startDate
                        c.add(Calendar.DAY_OF_MONTH, 1)
                        startDate = c.time
                    }
                }
                getString(R.string.tv_every_other_day) -> {
                    var startDate = SimpleDateFormat(
                        "MM/dd/yyyy",
                        Locale.getDefault()
                    ).parse(medInfo.startDate!!)
                    for (j in 0 until (dur + 1) / 2) {
                        val sDate =
                            SimpleDateFormat("MM/dd/yyyy", Locale.getDefault()).format(startDate!!)
                        val singleMedEvent =
                            MedEvent(medItemInfo, medInfo.medTimes[i], medInfo.medName!!, sDate)
                        events[sDate] = events[sDate].orEmpty().plus(singleMedEvent)
                        val c = Calendar.getInstance()
                        c.time = startDate
                        c.add(Calendar.DAY_OF_MONTH, 2)
                        startDate = c.time
                    }
                }
                else -> Log.d("TEST", "Function not implemented for specific days")
            }
        }
    }
}