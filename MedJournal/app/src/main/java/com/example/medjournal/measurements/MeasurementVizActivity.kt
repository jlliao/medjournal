package com.example.medjournal.measurements

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.medjournal.R
import com.example.medjournal.adapters.MeasurementHistoryRvAdapter
import com.example.medjournal.models.MeasurementData
import com.example.medjournal.utils.ChartValueDateFormatter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

/**
 * The activity for visualizing the measurement data.
 * Takes care of fetching the data from the database, visualizes it in a line graph,
 * lists most recent measurements in a RecyclerView
 */
class MeasurementVizActivity : AppCompatActivity() {
    lateinit var measurementType: String // the measurement type for which the visualization is made
    private lateinit var database: DatabaseReference
    private val measurementObjects = ArrayList<MeasurementData>()
    lateinit var yVals: ArrayList<Entry>
    private lateinit var tempLineChart: LineChart

    private lateinit var fromGraphLimitTv : TextView
    private lateinit var periods : Array<out String?>
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager

    companion object {
        const val TAG = "MeasurementVizActivity"
    }

    /**
     * Creates the default view for this activity
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_measurement_viz)

        supportActionBar!!.title = getString(R.string.title_measurement_visualization)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        measurementType = intent.getStringExtra(getString(R.string.intent_parameter_for_measurement_viz_type))!!
        findViewById<TextView>(R.id.measurement_activity_header).text = String.format(resources.getString(R.string.tv_your_health_statistics), measurementType)

        val dropdown1: Spinner = findViewById(R.id.period_for_graph_spinner)

        // Create an ArrayAdapter to control the data period range
        ArrayAdapter.createFromResource(
            this,
            R.array.period_choices_for_measurement_graphs,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            dropdown1.adapter = adapter
        }

        fromGraphLimitTv = findViewById(R.id.earliest_measurement_graphed_tv)
        periods = resources.getStringArray(R.array.period_choices_for_measurement_graphs)
        // Set up the line chart
        yVals = ArrayList()
        val set1 = LineDataSet(yVals, "temp")

        set1.color = Color.BLUE
        set1.setCircleColor(Color.BLUE)
        set1.lineWidth = 2f
        set1.circleRadius = 3f
        set1.setDrawCircleHole(false)
        set1.valueTextSize = 0f
        set1.setDrawFilled(false)

        val data = LineData(set1)

        tempLineChart = findViewById(R.id.measurement_lineChart)
        tempLineChart.data = data
        val xAxis = tempLineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        tempLineChart.description.isEnabled = false
        tempLineChart.legend.isEnabled = false
        tempLineChart.setPinchZoom(true)
        tempLineChart.xAxis.enableGridDashedLine(5f, 5f, 0f)
        tempLineChart.axisRight.enableGridDashedLine(5f, 5f, 0f)
        tempLineChart.axisLeft.enableGridDashedLine(5f, 5f, 0f)
        xAxis.valueFormatter = ChartValueDateFormatter("y.MM.dd")

        tempLineChart.notifyDataSetChanged()

        // Set up the onItemSelectedListener which listens to changes in Spinner's item and updates graphs boundaries and its textviews.
        dropdown1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            // When the view is initialized
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing, graph will be initialized separately
            }

            // When the user selects a different timeframe for the graph, update the legend and graph zoom.
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val cal = Calendar.getInstance()
                val period : String = periods[position] as String
                when(period) {
                    "Last 24 hours" -> cal.add(Calendar.HOUR_OF_DAY, -24)
                    "Last 3 days" -> cal.add(Calendar.DAY_OF_YEAR, -3)
                    "Last week" -> cal.add(Calendar.DAY_OF_YEAR, -7)
                    "Last month" -> cal.add(Calendar.DAY_OF_YEAR, -30)
                    "Last 3 months" -> cal.add(Calendar.DAY_OF_YEAR, -90)
                    "Last year" -> cal.add(Calendar.DAY_OF_YEAR, -365)
                }
                // Update the left end of the visible part of the chart
                tempLineChart.xAxis.setAxisMinimum(cal.timeInMillis.toFloat());

                // Update the textview in the legend
                val myFormat = "MM/dd/yyyy" // mention the format you need
                val sdf = SimpleDateFormat(myFormat, Locale.US)
                fromGraphLimitTv.text = getString(R.string.measurement_viz_start_limit, sdf.format(cal.getTime()))
            }
        }

        // Get a reference to our Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // -------------------------------- GRAPH SETUP ENDS -----------------------------------

        // -------------------------- MEASUREMENT HISTORY RV SETUP -----------------------------

        recyclerView = findViewById(R.id.measurement_history_rv)
        linearLayoutManager = LinearLayoutManager(this@MeasurementVizActivity)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter =
            MeasurementHistoryRvAdapter(measurementObjects, this@MeasurementVizActivity)
        recyclerView.layoutParams.height = 800
    }

    /**
     * Overrides a method to enable navigation up the app hierarchy.
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    /**
     *  Updates the view every time activity is started or restarted
     *  Includes a callback to update the line graph and recent measurement recyclerview
     */
    override fun onStart() {
        super.onStart()
        setUpDataCallbacks()
    }

    /**
     * Sets up callbacks to Firebase database node for the logged-in user
     * and defines the logic for model updates executed when the data changes in the Firebase node
     */
    private fun setUpDataCallbacks() {
        val uid = FirebaseAuth.getInstance().uid ?: "UserX"
        val myRef = database.child("measurements").child(uid)

        /**
         * Create a DataListener for graph values in date range from the start point (selected in spinner)
         * to current datetime
         */
        val measurementDataListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadMeasurementData:onCancelled", databaseError.toException())
            }

            /**
             * Specifies how the view data structures should be updated upon a change
             * in Firebase node for this user
             */
            override fun onDataChange(snapshot: DataSnapshot) {
                measurementObjects.clear()
//                Log.e("Firebase+MPChart:", "list has : ${snapshot.childrenCount} elements")
                for (ds in snapshot.children) {
                    val temp: MeasurementData = ds.getValue(MeasurementData::class.java)!!
                    // Only add measurements which have the same MeasurementType as this visualization requires
                    if (temp.typeOfMeasurement == measurementType) {
                        val format = java.text.SimpleDateFormat(
                            "EE MMM dd HH:mm:ss z yyyy",
                            Locale.ENGLISH
                        )
                        val d = format.parse(temp.datetimeEntered)!!
                        yVals.add(Entry(d.time.toFloat(), temp.measuredVal))
                    }
                }
                tempLineChart.notifyDataSetChanged()
                tempLineChart.data = LineData(LineDataSet(yVals, "temp"))
                tempLineChart.invalidate()
                tempLineChart.refreshDrawableState()
            }
        }
        myRef.addValueEventListener(measurementDataListener)

        /**
         * Create a DataListener for most recent measurement entries' recyclerview
         */
        val last10query : Query = myRef.orderByKey().limitToLast(5)
        val recentMeasurementsDataListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadMeasurementData:onCancelled", databaseError.toException())
            }

            /**
             * Specifies how the view data structures should be updated upon a change
             * in Firebase node for this user
             */
            override fun onDataChange(snapshot: DataSnapshot) {
                measurementObjects.clear()
//                Log.e("Firebase+MPChart:", "list has : ${snapshot.childrenCount} elements")
                for (ds in snapshot.getChildren().reversed()) {
                    val temp: MeasurementData = ds.getValue(MeasurementData::class.java)!!
                    // Only add measurements which have the same MeasurementType as this visualization requires
                    if (temp.typeOfMeasurement == measurementType) {
                        measurementObjects.add(temp)
                    }
                }
            }
        }
        last10query.addValueEventListener(recentMeasurementsDataListener)
    }
}
