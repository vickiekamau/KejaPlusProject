package com.kejaplus.application

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.kejaplus.application.databinding.ActivitySpashBinding
import com.kejaplus.application.ui.authentication.SignInActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySpashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivitySpashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        splashScreen.setKeepOnScreenCondition{true}

        startActivity(Intent(this,SignInActivity::class.java))
        finish()




        // This is used to hide the status bar and make
        // the splash screen as a full screen activity.
        /*window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        Handler().postDelayed({
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }, 2000) // 300*/
        /**if (checkForInternet(this)) {
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
        }*/


        }


    }
