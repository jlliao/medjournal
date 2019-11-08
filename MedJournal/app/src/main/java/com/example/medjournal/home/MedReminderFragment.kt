package com.example.medjournal.home


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.findNavController

import com.example.medjournal.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener
import kotlinx.android.synthetic.main.fragment_med_reminder.*
import org.threeten.bp.format.DateTimeFormatter

/**
 * A simple [Fragment] subclass.
 */
class MedReminderFragment : Fragment(), OnDateSelectedListener {

    private lateinit var widget: MaterialCalendarView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val medView = inflater.inflate(R.layout.fragment_med_reminder, container, false)
        widget = medView.findViewById(R.id.calendarView)
        widget.setOnDateChangedListener(this)

        val addMedBtn = medView.findViewById<FloatingActionButton>(R.id.add_med_button)
        addMedBtn.setOnClickListener {
            medView.findNavController().navigate(R.id.action_home_to_med)
        }



        return medView
    }

    override fun onDateSelected(
        widget: MaterialCalendarView,
        date: CalendarDay,
        selected: Boolean
    ) {
        //if (selected) Log.d("Calendar", "Current Date: ${date.date}") else "No date selected"
        if (selected) {
            val text = DateTimeFormatter.ofPattern("EEE, d MMM yyyy").format(date.date)
            Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "No Selection", Toast.LENGTH_SHORT).show()
        }
    }




}
