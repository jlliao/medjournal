package com.example.medjournal.medications


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.graphics.Color
import android.icu.lang.UCharacter
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController

import com.example.medjournal.R
import com.google.android.material.textfield.TextInputEditText
import kotlinx.android.synthetic.main.fragment_med_config.*
import java.lang.StringBuilder
import java.text.DateFormat.getTimeInstance
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
    var dosage: String = "1" // default dosage
    var unit: String = "pill" // default dosage unit
    var remindertime1: String = "08:00" // default time
    var remindertime2: String = "08:00" // default time
    var remindertime3: String = "08:00" // default time


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val adapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.medication_frequency,
            android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val dosageAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.medication_dosage,
            android.R.layout.simple_spinner_item
        )
        dosageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val medConfigView = inflater.inflate(R.layout.fragment_med_config, container, false)
        val spinner = medConfigView.findViewById<Spinner>(R.id.medication_reminder_time_spinner)
        val dosageSpinner =
            medConfigView.findViewById<Spinner>(R.id.medication_reminder_dosage_spinner)
        val dosageText = medConfigView.findViewById<TextView>(R.id.medication_reminder_dosage_text)
        reminderdate_selected1 = medConfigView.findViewById(R.id.tv_reminder_time_input1)
        reminderdate_selected2 = medConfigView.findViewById(R.id.tv_reminder_time_input2)
        reminderdate_selected3 = medConfigView.findViewById(R.id.tv_reminder_time_input3)
        spinner.adapter = adapter
        dosageSpinner.adapter = dosageAdapter

        // reminder time spinner
        spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                when (position) {
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

        dosageText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                dosage = p0.toString()
                if (dosage.length > 1 && dosage.startsWith("0")) {
                    dosage = dosage.substring(1)
                    dosageText.text = dosage
                }
                reminderdate_selected1?.text =
                    getString(takeString(dosage, unit), dosage, unit, remindertime1)
                reminderdate_selected2?.text =
                    getString(takeString(dosage, unit), dosage, unit, remindertime2)
                reminderdate_selected3?.text =
                    getString(takeString(dosage, unit), dosage, unit, remindertime3)

            }
        })

        dosage = dosageText?.text.toString()

        dosageSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                unit = parent?.getItemAtPosition(position).toString()
                reminderdate_selected1?.text =
                    getString(takeString(dosage, unit), dosage, unit, remindertime1)
                reminderdate_selected2?.text =
                    getString(takeString(dosage, unit), dosage, unit, remindertime2)
                reminderdate_selected3?.text =
                    getString(takeString(dosage, unit), dosage, unit, remindertime3)

            }

        }

        reminderdate_selected1!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val cal1 = Calendar.getInstance()
                val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    cal1.set(Calendar.HOUR_OF_DAY, hour)
                    cal1.set(Calendar.MINUTE, minute)
                    remindertime1 = SimpleDateFormat("HH:mm", Locale.US).format(cal1.time)
                    reminderdate_selected1?.text =
                        getString(takeString(dosage, unit), dosage, unit, remindertime1)
                }
                TimePickerDialog(
                    requireContext(),
                    timeSetListener,
                    cal1.get(Calendar.HOUR_OF_DAY),
                    cal1.get(Calendar.MINUTE),
                    true
                ).show()
            }

        })

        reminderdate_selected2!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val cal2 = Calendar.getInstance()
                val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    cal2.set(Calendar.HOUR_OF_DAY, hour)
                    cal2.set(Calendar.MINUTE, minute)
                    remindertime2 = SimpleDateFormat("HH:mm", Locale.US).format(cal2.time)
                    reminderdate_selected2?.text =
                        getString(takeString(dosage, unit), dosage, unit, remindertime3)
                }
                TimePickerDialog(
                    requireContext(),
                    timeSetListener,
                    cal2.get(Calendar.HOUR_OF_DAY),
                    cal2.get(Calendar.MINUTE),
                    true
                ).show()
            }

        })

        reminderdate_selected3!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                val cal3 = Calendar.getInstance()
                val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    cal3.set(Calendar.HOUR_OF_DAY, hour)
                    cal3.set(Calendar.MINUTE, minute)
                    remindertime3 = SimpleDateFormat("HH:mm", Locale.US).format(cal3.time)
                    reminderdate_selected3?.text =
                        getString(R.string.tv_take_pill, dosage, unit, remindertime3)
                }
                TimePickerDialog(
                    requireContext(),
                    timeSetListener,
                    cal3.get(Calendar.HOUR_OF_DAY),
                    cal3.get(Calendar.MINUTE),
                    true
                ).show()
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
                DatePickerDialog(
                    requireContext(),
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

        })

        val rb: RadioButton = medConfigView.findViewById(R.id.radio_number_of_days)
        val rb1: RadioButton = medConfigView.findViewById(R.id.radio_ongoing)

        val radio_group: RadioGroup = medConfigView.findViewById(R.id.radio_duration)
        radio_group.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = medConfigView.findViewById(checkedId)
            if (radio.text != "Ongoing treatment") {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Set number of days (from start date)")

                val view = layoutInflater.inflate(R.layout.popup_duration, container, false)

                val input = view.findViewById(R.id.et_duration) as TextInputEditText

                if (input.parent != null) {
                    (input.parent as ViewGroup).removeView(input)
                }

                builder.setView(input)

                builder.setPositiveButton(
                    "OK"
                ) { _, _ ->
                    var mText: String = input.text.toString()
                    if (mText.isEmpty()) mText = "14"
                    Toast.makeText(requireContext(), mText, Toast.LENGTH_SHORT).show()
                    rb.text = getString(R.string.radio_number_of_days_changed, mText)
                    rb.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.design_default_color_primary
                        )
                    )
                    rb.setOnClickListener {
                        if (rb.text != getString(R.string.radio_text_number_of_days)) {
                            val cur_duration = rb.text
                            rb1.isChecked = true
                            rb.isChecked = true
                            rb.text = cur_duration
                            rb.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.design_default_color_primary
                                )
                            )
                        }
                    }
                }
                builder.setNegativeButton(
                    "Cancel"
                ) { dialog, _ ->
                    dialog.cancel()
                    if (rb.text == getString(R.string.radio_text_number_of_days)) {
                        rb1.isChecked = true
                    }
                }

                builder.show()
            } else {
                rb.text = getString(R.string.radio_text_number_of_days)
                rb.setTextColor(Color.BLACK)
                rb.isChecked = false
            }
        }

        var selectedDays: ArrayList<String>

        val radio_group2: RadioGroup = medConfigView.findViewById(R.id.radio_days)
        val radio_every_day: RadioButton = medConfigView.findViewById(R.id.radio_every_day)
        val radio_specific: RadioButton = medConfigView.findViewById(R.id.radio_specific_days)
        radio_group2.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = medConfigView.findViewById(checkedId)
            Log.d("test", radio.text.toString())
            if (radio.text.toString()[0] == 'E') {
                radio_specific.text = getString(R.string.tv_specific_days)
                radio_specific.setTextColor(Color.BLACK)
            } else {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle("Select days of the week:")

                val view = layoutInflater.inflate(R.layout.popup_days_of_week, container, false)

                val chk1: CheckBox = view.findViewById(R.id.chk1)
                val chk2: CheckBox = view.findViewById(R.id.chk2)
                val chk3: CheckBox = view.findViewById(R.id.chk3)
                val chk4: CheckBox = view.findViewById(R.id.chk4)
                val chk5: CheckBox = view.findViewById(R.id.chk5)
                val chk6: CheckBox = view.findViewById(R.id.chk6)
                val chk7: CheckBox = view.findViewById(R.id.chk7)

                if (chk1.parent != null && chk2.parent != null && chk3.parent != null
                    && chk4.parent != null && chk5.parent != null && chk6.parent != null
                    && chk7.parent != null
                ) {
                    (chk1.parent as ViewGroup).removeView(chk1)
                    (chk2.parent as ViewGroup).removeView(chk2)
                    (chk3.parent as ViewGroup).removeView(chk3)
                    (chk4.parent as ViewGroup).removeView(chk4)
                    (chk5.parent as ViewGroup).removeView(chk5)
                    (chk6.parent as ViewGroup).removeView(chk6)
                    (chk7.parent as ViewGroup).removeView(chk7)
                }

                val linearlayout = LinearLayout(requireContext())
                linearlayout.orientation = LinearLayout.VERTICAL
                linearlayout.addView(chk1)
                linearlayout.addView(chk2)
                linearlayout.addView(chk3)
                linearlayout.addView(chk4)
                linearlayout.addView(chk5)
                linearlayout.addView(chk6)
                linearlayout.addView(chk7)

                builder.setView(linearlayout)

                selectedDays = ArrayList()
                builder.setPositiveButton(
                    "OK"
                ) { _, _ ->
                    if (chk1.isChecked) selectedDays.add(chk1.text.toString())
                    if (chk2.isChecked) selectedDays.add(chk2.text.toString())
                    if (chk3.isChecked) selectedDays.add(chk3.text.toString())
                    if (chk4.isChecked) selectedDays.add(chk4.text.toString())
                    if (chk5.isChecked) selectedDays.add(chk5.text.toString())
                    if (chk6.isChecked) selectedDays.add(chk6.text.toString())
                    if (chk7.isChecked) selectedDays.add(chk7.text.toString())

                    if (selectedDays.isEmpty()) {
                        radio_every_day.isChecked = true
                        Toast.makeText(
                            requireContext(), "Please select at least " +
                                    "one day of the week", Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val alldays = StringBuilder()
                        for (s in selectedDays) alldays.append(s + " ")
                        //Toast.makeText(requireContext(), alldays, Toast.LENGTH_SHORT).show()
                        radio_specific.text = alldays
                        radio_specific.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                R.color.design_default_color_primary
                            )
                        )
//                        radio_specific.setOnClickListener {
//                            radio_every_day.isChecked = true
//                            radio_specific.isChecked = true
//                        }


                    }
                }
                builder.setNegativeButton(
                    "Cancel"
                ) { dialog, _ ->
                    radio_every_day.isChecked = true
                    dialog.cancel()
                }

                builder.show()

            }
        }

        val doneButton: Button = medConfigView.findViewById(R.id.med_config_done_button)

        doneButton.setOnClickListener {
            medConfigView.findNavController()
                .navigate(R.id.action_medConfigFragment_to_homeActivity)
        }

        return medConfigView
    }

    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textview_date!!.text = sdf.format(cal.getTime())
    }

    private fun takeString(dosage: String, unit: String) =
            if (unit.length <= 2 || dosage.isEmpty() || dosage.toInt() < 2)
                R.string.tv_take_pill else
                R.string.tv_take_pill_plural
}
