package com.example.medjournal.medications


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner

import com.example.medjournal.R
import kotlinx.android.synthetic.main.fragment_med_config.*

/**
 * A simple [Fragment] subclass.
 */
class MedConfigFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val adapter = ArrayAdapter.createFromResource(requireContext(), R.array.medication_frequency, android.R.layout.simple_spinner_item)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        val medConfigView = inflater.inflate(R.layout.fragment_med_config, container, false)

        val spinner = medConfigView.findViewById<Spinner>(R.id.medication_reminder_time_spinner)

        spinner.adapter = adapter

        return medConfigView
    }


}
