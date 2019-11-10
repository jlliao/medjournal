package com.example.medjournal.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.medjournal.R
import com.example.medjournal.models.MedInfo
import com.example.medjournal.registerlogin.RegisterActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.delay
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class HomeActivity : AppCompatActivity() {

    private val medications: MutableList<MedInfo> = ArrayList()

    companion object {
        const val TAG = "HomeActivity"
    }

//    private var uid: String? = null
//    private var medName: String? = null
//    private var times: Int? = 1
//    private var amount: Int? = 0
//    private var unit: String? = null
//    private var timesArray: ArrayList<String>? = arrayListOf()
//    private var startDate: String? = null
//    private var duration: Int? = 14
//    private var days: ArrayList<String>? = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        AndroidThreeTen.init(this)

        verifyUserIsLoggedIn()

        getMedInfoFromFirebaseDatabase()


//        Toast.makeText(
//            this,
//            when (medications.isEmpty()) {
//                true -> "Stinson"
//                false -> medications[0].medName
//            }, Toast.LENGTH_LONG
//        ).show()
//
//        Timer().schedule(10000) {
//            runOnUiThread {
//                Toast.makeText(
//                    this@HomeActivity,
//                    when (medications.isEmpty()) {
//                        true -> "Stinson"
//                        false -> medications[0].medName
//                    },
//                    Toast.LENGTH_LONG
//                ).show()
//            }
//        }


        val navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController)
        findViewById<BottomNavigationView>(R.id.home_bottom_navigation)
            .setupWithNavController(navController)
//
//        val bundle = intent.extras
//        uid = bundle?.getString("uid")
//        medName = bundle?.getString("medName")
//        times = bundle?.getInt("times")
//        amount = bundle?.getInt("amount")
//        unit = bundle?.getString("unit")
//        timesArray = bundle?.getStringArrayList("medTimes")
//        startDate = bundle?.getString("startDate")
//        duration = bundle?.getInt("duration")
//        days = bundle?.getStringArrayList("days")
//        Toast.makeText(
//            this,
////            uid + " " + medName + " " + times.toString() + " " +
////                    amount.toString() + unit + " " + startDate + " " +
////                    duration,
//            medName.toString(),
//            Toast.LENGTH_SHORT
//        ).show()
    }

    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    private fun getMedInfoFromFirebaseDatabase() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().reference.child("medications").child(uid!!)

        val medInfoListener = object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "loadMedInfo:onCancelled", databaseError.toException())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                medications.clear()
                //medications = ArrayList()

                dataSnapshot.children.forEach {
                    val medication = it.getValue(MedInfo::class.java)
                    medications.add(medication!!)
//                    Log.d(TAG, medications[0].medName ?: "NULLL")

                    Log.d(TAG, "Update MedInfo Successfully")
                }
            }
        }

        ref.addListenerForSingleValueEvent(medInfoListener)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sign_out -> {
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, RegisterActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.flow_nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}