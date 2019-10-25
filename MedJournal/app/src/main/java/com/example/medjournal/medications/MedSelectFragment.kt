package com.example.medjournal.medications


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.findNavController

import com.example.medjournal.R

/**
 * A simple [Fragment] subclass.
 */
class MedSelectFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val medSelectView = inflater.inflate(R.layout.fragment_med_select, container, false)

        val nextBtn = medSelectView.findViewById<Button>(R.id.med_select_next_button)
        nextBtn.setOnClickListener {
            medSelectView.findNavController().navigate(R.id.action_medSelectFragment_to_medConfigFragment)
        }

        return medSelectView
    }


}
