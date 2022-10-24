package com.kejaplus.application.ui.mainui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.preference.PreferenceManager
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.kejaplus.application.R
import com.kejaplus.application.databinding.ActivityMainBinding
import com.kejaplus.application.ui.authentication.SignInActivity
import com.kejaplus.utils.SweetAlerts


class  MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var auth: FirebaseAuth
    private val mainViewModel: MainViewModel by viewModels()

    //Notification badge
    private var notificationBadge: TextView? = null
    // Notification Intent Extras
    private var isIntentFromNotification = false


    companion object{
        val CHANNEL_ID = "channel_id_1"

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        navController = navHostFragment.navController
        val navView: BottomNavigationView = binding.navView

        //Toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(true)
        //binding.subTitle.text = resources.getString(R.string.app_name)

        // check if intent is notification
        //isIntentFromNotification = intent.getBooleanExtra(IntentExtras.NOTIFICATION, false)



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
         appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_addProperty, R.id.navigation_settings
            )
        )

        navView.setupWithNavController(navController)

        //appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()
        setupActionBarWithNavController(navController,appBarConfiguration)

        /**val sharedPreferences = getSharedPreferences("SwitchPreferenceCompat", MODE_PRIVATE)
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)

        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)*/
        

        mainViewModel.notificationCount.observe(this) { count ->
            setNotificationCount(count)
        }


    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu items for use in the action bar
        menuInflater.inflate(R.menu.top_bar_menu, menu)
        val notification = menu!!.findItem(R.id.notification).actionView
        notificationBadge = notification?.findViewById(R.id.txtCount)
        notification?.setOnClickListener {
            val alerts = menu.findItem(R.id.notification)
            onOptionsItemSelected(alerts)
        }
        mainViewModel.notificationCount.observe(this){
            setNotificationCount(it)
        }
        menu.apply {
            findItem(R.id.main_menu_mark_all_as_read).isVisible = false
            findItem(R.id.main_menu_clear_all_notification).isVisible = false
        }

        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem):Boolean{
        when (item.itemId) {
            R.id.logout ->
                // User chose the Logout Button
                showConfirmation(this@MainActivity, "Logout", "Do You Want to Logout", "No",
                    confirm = {
                        //Firebase.auth.signOut()
                        GoogleSignIn.getClient(
                            this,
                            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                        ).signOut()
                        signOutFromApp()
                        startActivity(Intent(this@MainActivity, SignInActivity::class.java))
                    })


            R.id.notification ->
                navController.navigate(R.id.notificationFragment)

            }
        return super.onOptionsItemSelected(item)
        }




    /**override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        if (key == "dark_mode") {
            val pref = sharedPreferences?.getString(key, "1")

            when (pref?.toInt()) {
                1 -> {
                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
                    )
                }
                2 -> {
                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                    )
                }
                3 -> {
                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                    )
                }
                4 -> {
                    AppCompatDelegate.setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY
                    )
                }

            }
        }
    }

    override fun onResume() {
        super.onResume()
        PreferenceManager.getDefaultSharedPreferences(this)
            .registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        PreferenceManager.getDefaultSharedPreferences(this)
            .unregisterOnSharedPreferenceChangeListener(this)
    }*/


    private fun showConfirmation(context: Context, title: String, msg: String,cancelT:String, confirm: () -> Unit) {
        SweetAlerts.confirm(
            context = context,
            title = title,
            msg = msg,
            cancelText = cancelT,
            confirm = confirm
        )
    }

    private fun signOutFromApp() {
        auth.signOut()
        //FACEBOOK LOG OUT
        LoginManager.getInstance().logOut()
        startActivity(Intent(this, SignInActivity::class.java))
        finish() //finish settigs view
    }
    private fun setNotificationCount(count: Int = 0) {
        if (notificationBadge != null) {
            notificationBadge!!.visibility = if (count < 1) View.GONE else View.VISIBLE
            notificationBadge!!.text = if (count > 9) "9+" else count.toString()
        }
    }

}