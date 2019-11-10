package com.example.medjournal.home


import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.example.medjournal.R
import com.example.medjournal.adapters.MedSummaryAdapter
import com.example.medjournal.models.MedInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.extensions.LayoutContainer


/**
 * A simple [Fragment] subclass.
 */
class MedSummaryFragment : Fragment() {

    private val eventsAdapter = MedSummaryAdapter {

    }

    private val medications = mutableListOf<MedInfo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.test_username)

        val medView = inflater.inflate(R.layout.fragment_med_summary, container, false)

        getMedInfoFromFirebaseDatabase()

        val medRv = medView.findViewById<RecyclerView>(R.id.med_summary_rv)
        medRv.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        medRv.adapter = eventsAdapter
        medRv.addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))


        return medView
    }

    private fun getMedInfoFromFirebaseDatabase() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().reference.child("medications").child(uid!!)

        val medInfoListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(HomeActivity.TAG, "loadMedInfo:onCancelled", databaseError.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                medications.clear()

                dataSnapshot.children.forEach {
                    val medication = it.getValue(MedInfo::class.java)
                    medications.add(medication!!)
//                    Log.d(TAG, medications[0].medName ?: "NULLL")

                }
                eventsAdapter.events.clear()
                eventsAdapter.events.addAll(medications)
                eventsAdapter.notifyDataSetChanged()
                Log.d(HomeActivity.TAG, "Update MedInfo Successfully")

            }
        }

        ref.addListenerForSingleValueEvent(medInfoListener)
    }


}
