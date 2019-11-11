package com.example.medjournal.home


import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.medjournal.R
import com.example.medjournal.adapters.MeasurementMenuRvAdapter
import com.example.medjournal.measurements.AddMeasurementActivity
import com.example.medjournal.measurements.MeasurementVizActivity

/**
 * The fragment for measurement menu. It lists types of all measurements the user has added
 * with links to the visualizations for each Measurement type and includes a link
 * to the AddMeasurementActivity, which allows to add a new measurement entry.
 */
class MeasurementsFragment : Fragment() {
    private lateinit var linearLayoutManager: LinearLayoutManager

    /**
     * An interface listing methods for a listener catching events of clicks on RecyclerView items
     */
    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }

    /**
     * A method for adding a event listeners which respond to clicks on RecyclerView items
     */
    private fun RecyclerView.addOnItemClickListener(onClickListener: OnItemClickListener) {
        this.addOnChildAttachStateChangeListener(object :
            RecyclerView.OnChildAttachStateChangeListener {

            /** Unbinds a listener from the view
             */
            override fun onChildViewDetachedFromWindow(view: View) {
                view.setOnClickListener(null)
            }

            /** Binds a listener from the view
             */
            override fun onChildViewAttachedToWindow(view: View) {
                view.setOnClickListener {
                    val holder = getChildViewHolder(view)
                    onClickListener.onItemClicked(holder.adapterPosition, view)
                }
            }
        })
    }
    /**
    Creates the default view for this fragment. Sets up the RecyclerView of Measurement types (menu)
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.measurement_menu_name)
        val measurementMenuView = inflater.inflate(R.layout.fragment_measurements, container, false)

        val measurements =
            ArrayList<String>(listOf(*resources.getStringArray(R.array.measurement_types_array)))

        val recyclerView = measurementMenuView.findViewById<RecyclerView>(R.id.measurement_menu_rv)
        linearLayoutManager = LinearLayoutManager(activity)
        // Set up the RecyclerView of Measurement types (menu)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = MeasurementMenuRvAdapter(measurements, context!!)
        recyclerView.layoutParams.height = 800
        recyclerView.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                if (position >= 0 && position < measurements.size) {
                    val intent = Intent(view.context, MeasurementVizActivity::class.java)
                    intent.putExtra("measurementType", measurements[position])
                    startActivity(intent)
                }
            }
        })

        val addNewMeasurementBtn =
            measurementMenuView.findViewById<Button>(R.id.measurement_menu_add_measurement_btn)

        // Add a listener for button press tracking. If button pressed, take to AddMeasurement activity/
        addNewMeasurementBtn.setOnClickListener {
            val intent = Intent(context, AddMeasurementActivity::class.java)
            startActivity(intent)
        }


        return measurementMenuView
    }

}
