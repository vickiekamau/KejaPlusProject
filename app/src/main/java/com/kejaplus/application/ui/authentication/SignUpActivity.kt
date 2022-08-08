package com.kejaplus.application.ui.authentication

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.ui.AppBarConfiguration
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.kejaplus.application.MainActivity
import com.kejaplus.application.R
import com.kejaplus.application.Support.InputValidator
import com.kejaplus.application.databinding.ActivitySignupBinding
import com.kejaplus.application.response.Status
import com.kejaplus.application.ui.AddProperty.AddPropertyViewModel
import com.kejaplus.application.ui.authentication.SignInActivity.Companion.RC_SIGN_IN
import java.util.*


class SignUpActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySignupBinding
    private lateinit var  sweetAlertDialog: SweetAlertDialog
    private lateinit var authStateListener: FirebaseAuth.AuthStateListener
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val viewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)

        binding.topContent.signupBtn.setOnClickListener(View.OnClickListener { view ->  inputValidation() })

        binding.topContent.loginInTextBtn.setOnClickListener(View.OnClickListener { view -> navigate() })

        binding.topContent.signUpGoogle.setOnClickListener { View.OnClickListener { View-> signIn() } }
    }

    private fun inputValidation() {
        val validator = InputValidator()
        if(validator.validateRequired(binding.topContent.nameLayout,binding.topContent.nameText) &&
            validator.validateRequired(binding.topContent.emailLayout,binding.topContent.emailText) &&
            validator.validateRequired(binding.topContent.passwordLayout,binding.topContent.passwordText)
        ){
            signUp(
                Objects.requireNonNull(binding.topContent.nameText.text).toString(),
                Objects.requireNonNull(binding.topContent.emailText.text).toString(),
                Objects.requireNonNull(binding.topContent.passwordText.text).toString()
            );
        }
    }


    private fun navigate(){
        val intent = Intent(this, SignInActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun signUp(name: String, email: String, password: String) {


        sweetAlertDialog.progressHelper.barColor = Color.parseColor("#41c300")
        sweetAlertDialog.titleText = "Loading..."
        sweetAlertDialog.setCancelable(false)
        sweetAlertDialog.show()

        viewModel.signUpWithEmailAndPassword(email,password)

        sweetAlertDialog.progressHelper.barColor = Color.parseColor("#41c300")
        sweetAlertDialog.titleText = "Loading..."
        sweetAlertDialog.setCancelable(false)
        sweetAlertDialog.show()

        viewModel.signUpUserStatus.observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE)
                    sweetAlertDialog.titleText = "Success"
                    sweetAlertDialog.contentText = it.message
                    sweetAlertDialog.setOnDismissListener { dialog: DialogInterface? ->

                        binding.topContent.nameText.text?.clear()
                        binding.topContent.emailText.text?.clear()
                        binding.topContent.passwordText.text?.clear()

                        startActivity(Intent(this, SignInActivity::class.java))
                        finish()
                    }



                }

                Status.LOADING -> {
                    sweetAlertDialog.progressHelper.barColor = ContextCompat.getColor(this, R.color.backColor)
                    sweetAlertDialog.titleText = "Loading..."
                    sweetAlertDialog.setCancelable(true)
                    sweetAlertDialog.show()
                    //inputValidation()
                }
                Status.ERROR -> {
                    sweetAlertDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE)
                    sweetAlertDialog.titleText = "Oops"
                    sweetAlertDialog.contentText = it.message.toString()
                    sweetAlertDialog.setOnDismissListener(null)

                }

                else -> {

                }
            }
        })

    }
    private fun signUpGoogle() {
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(com.kejaplus.application.R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    companion object {
        const val RC_SIGN_IN = 1001
        const val EXTRA_NAME = "EXTRA NAME"
    }


}

