package com.example.medjournal.measurements

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.example.medjournal.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.medjournal.models.MeasurementData
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_add_measurement.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * The activity for adding new measurements
 */
class AddMeasurementActivity : AppCompatActivity() {
    var button_date: Button? = null
    var button_time: Button? = null

    var cal = Calendar.getInstance()
    private lateinit var database: DatabaseReference

    /**
     * Creates the default view for this activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_measurement)

        supportActionBar?.title = getString(R.string.tv_add_measurement)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        button_date = this.datePicker_btn
        button_time = this.timePicker_btn

        // create an OnDateSetListener to listen to changes in DatePicker
        val dateSetListener = object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int,
                                   dayOfMonth: Int) {
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateDateInView()
            }
        }

        // when the button is clicked, show DatePickerDialog that is set with OnDateSetListener
        button_date!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                DatePickerDialog(this@AddMeasurementActivity,
                    dateSetListener,
                    // set DatePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        })

        // create an OnTimeSetListener to listen to changes in TimePicker
        val timeSetListener = object : TimePickerDialog.OnTimeSetListener {
            override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
                cal.set(Calendar.HOUR_OF_DAY, hourOfDay)
                cal.set(Calendar.MINUTE, minute)
                updateTimeInView()
            }
        }

        button_time!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(view: View) {
                TimePickerDialog(this@AddMeasurementActivity,
                    timeSetListener,
                    // set TimePickerDialog to point to today's date when it loads up
                    cal.get(Calendar.HOUR_OF_DAY),
                    cal.get(Calendar.MINUTE), true).show()
            }
        })

        // Set up the TypeOfMeasurement Spinner
        val dropdown: Spinner = findViewById(R.id.chooseTypeOfMeasurementSpinner)

        /**
         * Creates an ArrayAdapter for the TypeOfMeasurement Spinner to update the view depending upon user input
          */
        ArrayAdapter.createFromResource(
            this,
            R.array.measurement_types_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            dropdown.adapter = adapter
        }
        database = FirebaseDatabase.getInstance().reference
    }
    /**
     * Overrides a method to enable navigation up the app hierarchy.
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     * Takes user input for a new measurement and pushes a new measurement object into the Firebase Realtime Database
     * @param view the current state of the UI
     */
    fun submitMeasurement(view: View) {
        val measurementType: String =
            (findViewById<Spinner>(R.id.chooseTypeOfMeasurementSpinner).selectedItem.toString())
        val valEditField: EditText = findViewById(R.id.editMeasurementValue)

        val measVal: Float = valEditField.text.toString().toFloat()
        val uid = FirebaseAuth.getInstance().uid ?: "UserX"

        // Validate the datetime input. Check if the entry is not in the future :))
        val currentTimeCalendar : Calendar = java.util.Calendar.getInstance()
        if (currentTimeCalendar.before(cal)) {
            val myToast = Toast.makeText(
                this,
                "Please enter a valid date & time. Current entry is in the future.",
                Toast.LENGTH_SHORT
            )
            myToast.show()
            return
        } else {
            val newMeasurement = MeasurementData(uid, cal, measurementType, measVal)

            val myRef = database.child("measurements").child(uid).child(newMeasurement.datetimeEntered)

            myRef.setValue(newMeasurement).addOnSuccessListener {
                val myToast = Toast.makeText(
                    this,
                    "submitted at " + newMeasurement.datetimeEntered.toString(),
                    Toast.LENGTH_SHORT
                )
                myToast.show()

                val intent = Intent(view.context, MeasurementVizActivity::class.java)
                intent.putExtra("measurementType", newMeasurement.typeOfMeasurement)
                startActivity(intent)
            }
                // If transaction unsuccessful, inform the user by raising a Toast
                .addOnFailureListener {
                    Log.e(
                        "SubmitMeasurement",
                        "Failed to add to Firebase at ${newMeasurement.datetimeEntered} " +
                                "by user ${newMeasurement.userName}, type of measurement: ${newMeasurement.typeOfMeasurement} with value: ${newMeasurement.measuredVal}"
                    )
                    val myToast = Toast.makeText(
                        this,
                        "Failed to submit the measurement. Bug Reported",
                        Toast.LENGTH_SHORT
                    )
                    myToast.show()
                }
        }
    }

    /** Updates the text value of the datepicker button to the selected date (saved in Cal object)
     */
    fun updateDateInView() {
        val myFormat = "MM/dd/yyyy" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        button_date!!.text = sdf.format(cal.getTime())
    }

    /** Updates the text value of the timepicker button to the selected time(saved in Cal object)
     */
    private fun updateTimeInView() {
        val myFormat = "HH:mm" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        button_time!!.text = sdf.format(cal.getTime())
    }
}
