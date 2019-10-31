package com.example.medjournal.medications


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.medjournal.R
import com.example.medjournal.adapters.RecylerAdapter
import com.google.android.material.textfield.TextInputEditText
import java.util.*
import java.util.Arrays.asList
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class MedSelectFragment : Fragment() {

    private lateinit var linearLayoutManager: LinearLayoutManager

    interface OnItemClickListener {
        fun onItemClicked(position: Int, view: View)
    }

    private fun RecyclerView.addOnItemClickListener(onClickListener: OnItemClickListener) {
        this.addOnChildAttachStateChangeListener(object :
            RecyclerView.OnChildAttachStateChangeListener {

            override fun onChildViewDetachedFromWindow(view: View) {
                view.setOnClickListener(null)
            }

            override fun onChildViewAttachedToWindow(view: View) {
                view.setOnClickListener {
                    val holder = getChildViewHolder(view)
                    onClickListener.onItemClicked(holder.adapterPosition, view)
                }
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val medSelectView = inflater.inflate(R.layout.fragment_med_select, container, false)

        val nextBtn = medSelectView.findViewById<Button>(R.id.med_select_next_button)
        nextBtn.setOnClickListener {
            medSelectView.findNavController()
                .navigate(R.id.action_medSelectFragment_to_medConfigFragment)
        }

        val medications =
            ArrayList<String>(listOf(*resources.getStringArray(R.array.medicine_names)))

        val recyclerView = medSelectView.findViewById<RecyclerView>(R.id.rv_med_name)
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = RecylerAdapter(medications, context!!)
        recyclerView.layoutParams.height = 800
        recyclerView.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                //Toast.makeText(activity, medications[position], Toast.LENGTH_SHORT).show()
                medSelectView.findViewById<TextInputEditText>(R.id.et_med_name)
                    .setText(medications[position])
            }
        })

        return medSelectView
    }


}
