package com.kejaplus.application

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.ui.AppBarConfiguration
import cn.pedant.SweetAlert.SweetAlertDialog
import com.kejaplus.application.databinding.ActivitySpashBinding

class SpashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySpashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // This is used to hide the status bar and make
        // the splash screen as a full screen activity.
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        if (checkForInternet(this)) {
            // we used the postDelayed(Runnable, time) method
            // to send a message with a delayed time.
            Handler().postDelayed({
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }, 2000) // 300
        } else {
            //Toast.makeText(this, "Network Unavailable: please check your data connection", Toast.LENGTH_SHORT).show()
            val sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE)
            sweetAlertDialog.setTitleText("Internet Error").contentText = "Enable internet connection to continue!"
            sweetAlertDialog.setCancelable(false)
            sweetAlertDialog?.setOnDismissListener { dialog: DialogInterface? ->
                finish()

            }
            sweetAlertDialog.setConfirmText("Ok").show()
        }


        }

    private fun checkForInternet(context: Context): Boolean {

        // register activity with the connectivity manager service
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // if the android version is equal to M
        // or greater we need to use the
        // NetworkCapabilities to check what type of
        // network has the internet connection
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // Returns a Network object corresponding to
            // the currently active default data network.
            val network = connectivityManager.activeNetwork ?: return false

            // Representation of the capabilities of an active network.
            val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false

            return when {
                // Indicates this network uses a Wi-Fi transport,
                // or WiFi has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true

                // Indicates this network uses a Cellular transport. or
                // Cellular has network connectivity
                activeNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true

                // else return false
                else -> false
            }
        } else {
            // if the android version is below M
            @Suppress("DEPRECATION") val networkInfo =
                connectivityManager.activeNetworkInfo ?: return false
            @Suppress("DEPRECATION")
            return networkInfo.isConnected
        }
    }
    }
