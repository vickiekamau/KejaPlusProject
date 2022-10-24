package com.kejaplus.application.ui.authentication

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels

import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.ui.AppBarConfiguration
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.kejaplus.application.R
import com.kejaplus.application.Support.InputValidator
import com.kejaplus.application.databinding.ActivitySignupBinding
import com.kejaplus.application.response.Status
import com.kejaplus.application.ui.mainui.MainActivity
import com.kejaplus.utils.SweetAlerts
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

import java.util.*

@AndroidEntryPoint
class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private lateinit var  sweetAlertDialog: SweetAlertDialog
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

        binding.topContent.signUpGoogle.setOnClickListener (View.OnClickListener { view-> signUpGLauncher() })

        signUpGoogle()
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
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
    }

    private fun signUpGLauncher() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
        Timber.e("GOOGLE SIGN UP")
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                Timber.e("launcher ${task.toString()}")
                handleResult(task)
            }
            else {
                //val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                //handleResult(task)
                Timber.e("launcher error  ${Activity.RESULT_OK.toString()}")
            }

        }




    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
                Timber.e("MainActivity ${account.email.toString()}")

            }
        } else {
            error(this, "Ooops", task.exception.toString(),
                dismiss = { sweetAlertDialog.dismiss() }
            )
            Timber.e("Error ${task.exception.toString()}")
        }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        viewModel.signUpWithGoogle(credentials)

        viewModel.signUpGoogleStatus.observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    success(
                        this, "Success", "Welcome ${it.data}",
                        dismiss = {
                            navigateMain()
                        }
                    )
                }

                Status.LOADING -> {
                    loading(this, "Loading")
                    //navigateMain()
                    //inputValidation()
                }
                Status.ERROR -> {
                    error(this, "Ooops", it.message.toString(),
                        dismiss = { sweetAlertDialog.dismiss() }
                    )
                }

                else -> {

                }
            }
        })


    }


    private fun success(context: Context, title: String, msg: String, dismiss: (() -> Unit)) {
        SweetAlerts.success(
            context = context,
            title = title,
            msg = msg,
            dismiss = dismiss
        )
    }

    private fun error(context: Context, title: String, msg: String, dismiss: (() -> Unit)) {
        SweetAlerts.error(
            context = context,
            title = title,
            msg = msg,
            dismiss = dismiss
        )
    }

    private fun loading(context: Context, msg: String) {

        SweetAlerts.loading(
            context = context,
            msg = msg
        )
    }

    private fun navigateMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}

