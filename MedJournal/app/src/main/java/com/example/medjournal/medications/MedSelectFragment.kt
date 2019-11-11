package com.example.medjournal.medications


import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.medjournal.R
import com.example.medjournal.adapters.RecyclerAdapter
import com.google.android.material.textfield.TextInputEditText
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 */
class MedSelectFragment : Fragment() {

    private lateinit var linearLayoutManager: LinearLayoutManager

    /**
     * Enables the recycler view to update real-time with the edit text.
     */
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

    /**
     * Creates the view for the select medicine fragment.
     */
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        (activity as AppCompatActivity).supportActionBar?.title =
            getString(R.string.title_add_medicine)

        var textLength: Int

        // Inflate the layout for this fragment
        val medSelectView = inflater.inflate(R.layout.fragment_med_select, container, false)

        /**
         * Clicking on the next NEXT button will send the medication name to the medication
         * configuration fragment.
         */
        val nextBtn = medSelectView.findViewById<Button>(R.id.med_select_next_button)
        nextBtn.setOnClickListener {
            if (medSelectView.findViewById<TextInputEditText>(R.id.et_med_name).text.toString() == "") {
                Toast.makeText(
                    requireContext(), getString(R.string.toast_enter_medname),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                val medName =
                    medSelectView.findViewById<TextInputEditText>(R.id.et_med_name).text.toString()

                val bundle = bundleOf("medName" to medName)
                medSelectView.findNavController()
                    .navigate(R.id.action_medSelectFragment_to_medConfigFragment, bundle)
            }
        }

        val medications =
            ArrayList<String>(listOf(*resources.getStringArray(R.array.medicine_names)))

        val medicationsSort: ArrayList<String> = ArrayList()
        medicationsSort.addAll(medications)

        /**
         * Sets up the recycler view that displays the list of medication.
         */
        val recyclerView = medSelectView.findViewById<RecyclerView>(R.id.rv_med_name)
        linearLayoutManager = LinearLayoutManager(activity)
        recyclerView.layoutManager = linearLayoutManager
        recyclerView.adapter = RecyclerAdapter(medications, context!!)
        recyclerView.layoutParams.height = 800
        recyclerView.addOnItemClickListener(object : OnItemClickListener {
            override fun onItemClicked(position: Int, view: View) {
                //Toast.makeText(activity, medications[position], Toast.LENGTH_SHORT).show()
                if (medicationsSort[position] != getString(R.string.tv_no_matching)) {
                    medSelectView.findViewById<TextInputEditText>(R.id.et_med_name)
                        .setText(medicationsSort[position])
                }

                val imm =
                    context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(recyclerView.windowToken, 0)

            }
        })

        /**
         * Typing on the edit text prompts the recycler view to change according to the text
         * that the user typed.
         */
        val etSearch: EditText = medSelectView.findViewById(R.id.et_med_name)

        etSearch.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            /**
             * When the edit text changes, the recycler view matches each of its item with the
             * edit text, and only keep the matching items.
             */
            @SuppressLint("DefaultLocale")
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                textLength = etSearch.text.length
                medicationsSort.clear()
                for (i in medications.indices) {
                    if (textLength <= medications[i].length) {
                        if (medications[i].trim().toLowerCase().contains(
                                etSearch.text.toString().toLowerCase().trim { it <= ' ' })
                        ) {
                            medicationsSort.add(medications[i])
                        }
                    }
                }

                if (medicationsSort.size == 0) {
                    medicationsSort.add(getString(R.string.tv_no_matching))
                }
                recyclerView!!.adapter = RecyclerAdapter(medicationsSort, context!!)
                recyclerView.layoutManager = linearLayoutManager
                recyclerView.layoutParams.height = 800


            }
        })



        return medSelectView
    }


}
