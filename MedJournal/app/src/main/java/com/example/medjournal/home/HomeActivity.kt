package com.example.medjournal.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.medjournal.R
import com.example.medjournal.registerlogin.RegisterActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.jakewharton.threetenabp.AndroidThreeTen


/** Base activity for home that controls the host of navigation
 */
class HomeActivity : AppCompatActivity() {

    /** TAG for logging purpose
     */
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

    /** Create view of navigation host, including bottom navigation and top tool bar
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        AndroidThreeTen.init(this)

        verifyUserIsLoggedIn()


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

        /** Set home activity as host of navigation controller
         */
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

    /** Verify with Firebase that user has indeed logged in
     */
    private fun verifyUserIsLoggedIn() {
        val uid = FirebaseAuth.getInstance().uid
        if (uid == null) {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
    }

    /** Option menu on tool bar to allow user to sign out
     */
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

    /** Instantiate option menu on tool bar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.flow_nav_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    /** Allow pressing back button to nagivate to previous activity
     */
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}