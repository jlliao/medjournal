package com.example.medjournal.medications


import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.example.medjournal.R
import kotlinx.android.synthetic.main.fragment_med_config.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class MedConfigFragment : Fragment() {

    var textview_date: TextView? = null
    var cal = Calendar.getInstance()
    var reminderdate_selected1: TextView? = null
    var reminderdate_selected2: TextView? = null
    var reminderdate_selected3: TextView? = null
    var dosage: Int = 1 // default dosage
    var remindertime1: String = "08:00" // default time
    var remindertime2: String = "08:00" // default time
    var remindertime3: String = "08:00" // default time


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.medication_frequency, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val dosageAdapter = ArrayAdapter.createFromResource(requireContext(), R.array.medication_dosage, android.R.layout.simple_spinner_item)
        dosageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val medConfigView = inflater.inflate(R.layout.fragment_med_config, container, false)
        val spinner = medConfigView.findViewById<Spinner>(R.id.medication_reminder_time_spinner)
        val dosageSpinner = medConfigView.findViewById<Spinner>(R.id.medication_reminder_dosage_spinner)
        reminderdate_selected1 = medConfigView.findViewById(R.id.tv_reminder_time_input1)
        reminderdate_selected2 = medConfigView.findViewById(R.id.tv_reminder_time_input2)
        reminderdate_selected3 = medConfigView.findViewById(R.id.tv_reminder_time_input3)
        spinner.adapter = adapter
        dosageSpinner.adapter = dosageAdapter

        // reminder time spinner
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position) {
                    0 -> {
                        reminderdate_selected1?.visibility = View.VISIBLE
                        reminderdate_selected2?.visibility = View.GONE
                        reminderdate_selected3?.visibility = View.GONE
                    }
                    1 -> {
                        reminderdate_selected1?.visibility = View.VISIBLE
                        reminderdate_selected2?.visibility = View.VISIBLE
                        reminderdate_selected3?.visibility = View.GONE
                    }
                    2 -> {
                        reminderdate_selected1?.visibility = View.VISIBLE
                        reminderdate_selected2?.visibility = View.VISIBLE
                        reminderdate_selected3?.visibility = View.VISIBLE
                    }
                }

            }

        }

        dosageSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                dosage = position + 1
                reminderdate_selected1?.text = "Take " + dosage.toString() + " at " + remindertime1
                reminderdate_selected2?.text = "Take " + dosage.toString() + " at " + remindertime2
                reminderdate_selected3?.text = "Take " + dosage.toString() + " at " + remindertime3

            }

        }

        reminderdate_selected1!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val cal1 = Calendar.getInstance()
                val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    cal1.set(Calendar.HOUR_OF_DAY, hour)
                    cal1.set(Calendar.MINUTE, minute)
                    remindertime1 = SimpleDateFormat("HH:mm").format(cal1.time)
                    reminderdate_selected1?.text = "Take " + dosage.toString() + " at " + remindertime1
                }
                TimePickerDialog(requireContext(), timeSetListener, cal1.get(Calendar.HOUR_OF_DAY), cal1.get(Calendar.MINUTE), true).show()
            }

        })

        reminderdate_selected2!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val cal2 = Calendar.getInstance()
                val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    cal2.set(Calendar.HOUR_OF_DAY, hour)
                    cal2.set(Calendar.MINUTE, minute)
                    remindertime2 = SimpleDateFormat("HH:mm").format(cal2.time)
                    reminderdate_selected2?.text = "Take " + dosage.toString() + " at " + remindertime2
                }
                TimePickerDialog(requireContext(), timeSetListener, cal2.get(Calendar.HOUR_OF_DAY), cal2.get(Calendar.MINUTE), true).show()
            }

        })

        reminderdate_selected3!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val cal3 = Calendar.getInstance()
                val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    cal3.set(Calendar.HOUR_OF_DAY, hour)
                    cal3.set(Calendar.MINUTE, minute)
                    remindertime3 = SimpleDateFormat("HH:mm").format(cal3.time)
                    reminderdate_selected3?.text = "Take " + dosage.toString() + " at " + remindertime3
                }
                TimePickerDialog(requireContext(), timeSetListener, cal3.get(Calendar.HOUR_OF_DAY), cal3.get(Calendar.MINUTE), true).show()
            }

        })

        textview_date = medConfigView.findViewById(R.id.tv_schedule_start_date_input)

        textview_date!!.text = "--/--/----"

        updateDateInView()

            // create an OnDateSetListener
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(
                view: DatePicker, year: Int, monthOfYear: Int,
                dayOfMonth: Int
            ) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        textview_date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(requireContext(),
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }

        })

        return medConfigView
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textview_date!!.text = sdf.format(cal.getTime())
    }

}
