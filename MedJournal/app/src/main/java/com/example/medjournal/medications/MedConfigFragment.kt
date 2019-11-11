package com.example.medjournal.medications


import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController

import com.example.medjournal.R
import com.example.medjournal.models.MedInfo
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_med_config.*
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * The fragment for medication configuration.
 */
class MedConfigFragment : Fragment() {

    private var textViewDate: TextView? = null
    var cal: Calendar = Calendar.getInstance()
    var reminderDateSelected1: TextView? = null
    var reminderDateSelected2: TextView? = null
    var reminderDateSelected3: TextView? = null
    var dosage: String = "1" // default dosage
    var unit: String = "pill" // default dosage unit
    var reminderTime1: String = "08:00" // default time
    var reminderTime2: String = "08:00" // default time
    var reminderTime3: String = "08:00" // default time

    companion object {
        const val TAG = "MedicationActivity"
    }


    /**
        Creates the view for this fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.title_more_details)

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
        reminderDateSelected1 = medConfigView.findViewById(R.id.tv_reminder_time_input1)
        reminderDateSelected2 = medConfigView.findViewById(R.id.tv_reminder_time_input2)
        reminderDateSelected3 = medConfigView.findViewById(R.id.tv_reminder_time_input3)
        spinner.adapter = adapter
        dosageSpinner.adapter = dosageAdapter


        /**
         * Handles user input about how many times the medication is taken. If the users needs
         * to take the medicine more than once a day, additional input fields will be
         * displayed.
         */
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
                        reminderDateSelected1?.visibility = View.VISIBLE
                        reminderDateSelected2?.visibility = View.GONE
                        reminderDateSelected3?.visibility = View.GONE
                    }
                    1 -> {
                        reminderDateSelected1?.visibility = View.VISIBLE
                        reminderDateSelected2?.visibility = View.VISIBLE
                        reminderDateSelected3?.visibility = View.GONE
                    }
                    2 -> {
                        reminderDateSelected1?.visibility = View.VISIBLE
                        reminderDateSelected2?.visibility = View.VISIBLE
                        reminderDateSelected3?.visibility = View.VISIBLE
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
                if (dosage.isNotEmpty() && dosage.startsWith("0")) {
                    dosage = dosage.substring(1)
                    dosageText.text = dosage
                }
                reminderDateSelected1?.text =
                    getString(takeString(dosage, unit), dosage, unit, reminderTime1)
                reminderDateSelected2?.text =
                    getString(takeString(dosage, unit), dosage, unit, reminderTime2)
                reminderDateSelected3?.text =
                    getString(takeString(dosage, unit), dosage, unit, reminderTime3)

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
                //Log.d("Test", unit)
                reminderDateSelected1?.text =
                    getString(takeString(dosage, unit), dosage, unit, reminderTime1)
                reminderDateSelected2?.text =
                    getString(takeString(dosage, unit), dosage, unit, reminderTime2)
                reminderDateSelected3?.text =
                    getString(takeString(dosage, unit), dosage, unit, reminderTime3)

            }

        }

        /**
         * Handles user input about the time that the medication is taken. If the users needs
         * to take the medicine more than once a day, additional input fields will be
         * displayed.
         */
        reminderDateSelected1!!.setOnClickListener {
            val cal1 = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal1.set(Calendar.HOUR_OF_DAY, hour)
                cal1.set(Calendar.MINUTE, minute)
                reminderTime1 = SimpleDateFormat("HH:mm", Locale.US).format(cal1.time)
                reminderDateSelected1?.text =
                    getString(takeString(dosage, unit), dosage, unit, reminderTime1)
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                cal1.get(Calendar.HOUR_OF_DAY),
                cal1.get(Calendar.MINUTE),
                true
            ).show()
        }

        reminderDateSelected2!!.setOnClickListener {
            val cal2 = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal2.set(Calendar.HOUR_OF_DAY, hour)
                cal2.set(Calendar.MINUTE, minute)
                reminderTime2 = SimpleDateFormat("HH:mm", Locale.US).format(cal2.time)
                reminderDateSelected2?.text =
                    getString(takeString(dosage, unit), dosage, unit, reminderTime2)
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                cal2.get(Calendar.HOUR_OF_DAY),
                cal2.get(Calendar.MINUTE),
                true
            ).show()
        }

        reminderDateSelected3!!.setOnClickListener {
            val cal3 = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                cal3.set(Calendar.HOUR_OF_DAY, hour)
                cal3.set(Calendar.MINUTE, minute)
                reminderTime3 = SimpleDateFormat("HH:mm", Locale.US).format(cal3.time)
                reminderDateSelected3?.text =
                    getString(R.string.tv_take_pill, dosage, unit, reminderTime3)
            }
            TimePickerDialog(
                requireContext(),
                timeSetListener,
                cal3.get(Calendar.HOUR_OF_DAY),
                cal3.get(Calendar.MINUTE),
                true
            ).show()
        }

        /**
         * Handles user input about the start date of the treatment.
         */
        textViewDate = medConfigView.findViewById(R.id.tv_schedule_start_date_input)

        textViewDate!!.text = "--/--/----"

        updateDateInView()

        // create an OnDateSetListener
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }

        textViewDate!!.setOnClickListener(object : View.OnClickListener {
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

        /**
         * Handles the user's choice of how long the medication will last. If the medication is an
         * ongoing treatment, then no further action is required, otherwise a dialog window will
         * show up and ask the user to input the exact days of the treatment.
         */
        val rb: RadioButton = medConfigView.findViewById(R.id.radio_number_of_days)
        val rb1: RadioButton = medConfigView.findViewById(R.id.radio_ongoing)
        var curDuration = ""

        val radioGroup: RadioGroup = medConfigView.findViewById(R.id.radio_duration)
        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = medConfigView.findViewById(checkedId)
            if (radio.text != getString(R.string.radio_text_ongoing)) {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle(getString(R.string.title_set_number_of_days))

                val view = layoutInflater.inflate(R.layout.popup_duration, container, false)

                val input = view.findViewById(R.id.et_duration) as TextInputEditText

                if (input.parent != null) {
                    (input.parent as ViewGroup).removeView(input)
                }

                val recallText = curDuration.split(" ")

                //Toast.makeText(requireContext(), recallText[0], Toast.LENGTH_SHORT).show()
                if (curDuration.isNotEmpty() && !curDuration.startsWith(getString(R.string.trick_duration))) input.setText(
                    recallText[0]
                )
                var mText: String = input.text.toString()
                input.addTextChangedListener(object : TextWatcher {
                    override fun afterTextChanged(s: Editable?) {}

                    override fun beforeTextChanged(
                        s: CharSequence?,
                        start: Int,
                        count: Int,
                        after: Int
                    ) {
                    }

                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        mText = input.text.toString()
                        if (mText.isNotEmpty() && mText.startsWith("0")) input.setText(
                            mText.substring(1)
                        )
                    }

                })

                builder.setView(input)

                builder.setPositiveButton(
                    "OK"
                ) { _, _ ->

                    if (mText.isEmpty()) mText = "14"
                    rb.text = getString(R.string.radio_number_of_days_changed, mText)
                    curDuration = rb.text.toString()
                    rb.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.design_default_color_primary
                        ))

                    val imm =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(view.windowToken, 0)


                    rb.setOnClickListener {
                        if (rb.text != getString(R.string.radio_text_number_of_days)) {
                            curDuration = rb.text.toString()
                            rb1.isChecked = true
                            rb.isChecked = true
                            rb.text = curDuration
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
                    getString(R.string.btn_cancel)
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

        /**
         * Handles the user's choice of how frequent the medication should be taken.
         * If the medication is taken every day or every other day, then no further action
         * is required, otherwise a dialog window will show up and ask the user to select
         * which days of the week should the medicine be taken.
         */
        var selectedDays = arrayListOf<String>(getString(R.string.tv_every_day))

        val radioGroup2: RadioGroup = medConfigView.findViewById(R.id.radio_days)
        val radioEveryDay: RadioButton = medConfigView.findViewById(R.id.radio_every_day)
        val radioSpecific: RadioButton = medConfigView.findViewById(R.id.radio_specific_days)
        radioGroup2.setOnCheckedChangeListener { _, checkedId ->
            val radio: RadioButton = medConfigView.findViewById(checkedId)
            //Log.d("RB1", radio.text.toString())
            if (radio.text.toString()[0] == getString(R.string.trick_days).single()) {
                radioSpecific.text = getString(R.string.tv_specific_days)
                radioSpecific.setTextColor(Color.BLACK)
                selectedDays.clear()
                selectedDays.add(radio.text.toString())
            } else {
                val builder = AlertDialog.Builder(requireContext())
                builder.setTitle(getString(R.string.title_select_days))

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

                val linearLayout = LinearLayout(requireContext())
                linearLayout.orientation = LinearLayout.VERTICAL
                linearLayout.addView(chk1)
                linearLayout.addView(chk2)
                linearLayout.addView(chk3)
                linearLayout.addView(chk4)
                linearLayout.addView(chk5)
                linearLayout.addView(chk6)
                linearLayout.addView(chk7)

                builder.setView(linearLayout)

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
                        radioEveryDay.isChecked = true
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.toast_at_least_one_day),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        val allDays = StringBuilder()
                        for (s in selectedDays) allDays.append("$s ")
                        //Toast.makeText(requireContext(), allDays, Toast.LENGTH_SHORT).show()
                        radioSpecific.text = allDays
                        radioSpecific.setTextColor(
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
                    getString(R.string.btn_cancel)
                ) { dialog, _ ->
                    radioEveryDay.isChecked = true
                    dialog.cancel()
                }

                builder.show()

            }
        }

        /**
         * Clicking on the done button will wrap all useful information into an object and store
         * it in Firebase.
         */
        val doneButton: Button = medConfigView.findViewById(R.id.med_config_done_button)

        doneButton.setOnClickListener {

            val uid = FirebaseAuth.getInstance().uid ?: ""
            val medName = arguments?.getString("medName")
            val times = getTimes(spinner.selectedItem.toString())
            val amount = medication_reminder_dosage_text.text.toString().toInt()
            val startDate = textViewDate?.text.toString()
            val timesArray = when (times) {
                1 -> arrayListOf(reminderTime1)
                2 -> arrayListOf(reminderTime1, reminderTime2)
                else -> arrayListOf(reminderTime1, reminderTime2, reminderTime3)
            }
            val duration = getDuration(rb, rb.isChecked)

            val medication = MedInfo(
                uid,
                medName ?: "null",
                times,
                amount,
                unit,
                timesArray,
                startDate,
                duration,
                selectedDays
            )

            saveMedInfoToFirebaseDatabase(medication, medConfigView)

//            val bundle = bundleOf(
//                "uid" to "sample_uid",
//                "medName" to arguments?.getString("medName"),
//                "times" to getTimes(spinner.selectedItem.toString()), // 1 2 or 3
//                "amount" to medication_reminder_dosage_text.text.toString().toInt(),
//                "unit" to unit,
//                "startDate" to textViewDate?.text.toString(),
//                "duration" to getDuration(rb, rb.isChecked)
//            )
//            //bundle.putSerializable("times", Date(1))
//            //bundle.putSerializable("startDate", Date(2))
//            bundle.putStringArrayList(
//                "medTimes",
//                arrayListOf(reminderTime1, reminderTime2, reminderTime3)
//            )
//            bundle.putStringArrayList("days", selectedDays)
//
//            medConfigView.findNavController()
//                .navigate(R.id.action_medConfigFragment_to_homeActivity, bundle)
        }

        return medConfigView
    }

    /**
     * Updates the date displayed in the view.
     */
    private fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        textViewDate!!.text = sdf.format(cal.time)
    }

    /**
     * Convert the dosage and the unit to a string that can be displayed
     * with the correct grammar. In particular, this function combines the dosage and
     * the unit, and handles singular forms and plural forms apporpriately.
     *
     * @param dosage the dosage to take
     * @param unit the unit for the medicine
     *
     */
    private fun takeString(dosage: String, unit: String) =
        if (unit.length <= 2 || dosage.isEmpty() || dosage.toInt() < 2)
            R.string.tv_take_pill else
            R.string.tv_take_pill_plural

    /**
     * From the user's selection of how many times a day they should have medication,
     * return the corresponding number from the string; 1 denotes once daily, 2 denotes
     * twice daily, and 3 denotes three times a day.
     *
     * @param spinnerText a string that denotes how many times a day the patient needs to
     * take medication
     */
    private fun getTimes(spinnerText: String) =
        when (spinnerText) {
            (resources.getStringArray(R.array.medication_frequency))[0] -> 1
            (resources.getStringArray(R.array.medication_frequency))[1] -> 2
            else -> 3
        }

    /**
     * From the text of the radio button, and whether it is selected,
     * determine the exact duration. The function returns 0 if the treatment
     * is an ongoing treatment, otherwise the number of days that compose of
     * the duration is returned.
     *
     * @param rb the radio button for duration
     * @param inputBool true if the radio button rb is checked, otherwise false
     */
    private fun getDuration(rb: RadioButton, inputBool: Boolean) =
        when (inputBool) {
            false -> 0
            else -> (rb.text.split(" "))[0].toInt()
        }

    /**
     * Save the information of the treatment to firbase.
     *
     * @param medication the medication object that stores the information of the treatment
     * @param view the current view of the application
     */
    private fun saveMedInfoToFirebaseDatabase(medication: MedInfo, view: View) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref =
            FirebaseDatabase.getInstance().getReference("/medications/$uid/${medication.medName}")

        ref.setValue(medication)
            .addOnSuccessListener {
                Log.d(TAG, "Finally we saved the user medication info to Firebase Database")

                view.findNavController()
                    .navigate(R.id.action_medConfigFragment_to_homeActivity)

            }
            .addOnFailureListener {
                Log.d(TAG, "Failed to set medication value to database: ${it.message}")
                Toast.makeText(requireContext(), "Bad Internet Connection", Toast.LENGTH_SHORT)
                    .show()
            }
    }
}
