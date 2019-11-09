package com.example.medjournal.measurements

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import com.example.medjournal.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.medjournal.models.MeasurementData
import com.google.firebase.database.DatabaseReference

class AddMeasurementActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_measurement)

        val dropdown: Spinner = findViewById(R.id.chooseTypeOfMeasurementSpinner)

        // Create an ArrayAdapter using the string array and a default spinner layout
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
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        database = FirebaseDatabase.getInstance().reference
    }

    fun submitMeasurement(view: View) {
        val measurementType : String = (findViewById<Spinner>(R.id.chooseTypeOfMeasurementSpinner).selectedItem.toString())
        val valEditField : EditText = findViewById(R.id.editMeasurementValue)

        val measVal : Float = valEditField.text.toString().toFloat()
        val uid = FirebaseAuth.getInstance().uid ?: "UserX"
        val newMeasurement = MeasurementData(uid, measurementType, measVal)

        val myRef = database.child("measurements").child(uid).child(newMeasurement.datetimeEntered)

        myRef.setValue(newMeasurement).addOnSuccessListener {
            val myToast = Toast.makeText(this, "submitted at " + newMeasurement.datetimeEntered.toString(), Toast.LENGTH_SHORT)
            myToast.show()
        }
            .addOnFailureListener{
                Log.e("SubmitMeasurement", "Failed to add to Firebase at ${newMeasurement.datetimeEntered} " +
                        "by user ${newMeasurement.userName}, type of measurement: ${newMeasurement.typeOfMeasurement} with value: ${newMeasurement.measuredVal}")
                val myToast = Toast.makeText(this, "Failed to submit the measurement. Bug Reported", Toast.LENGTH_SHORT)
                myToast.show()
        }
    }
}
