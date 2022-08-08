package com.kejaplus.application.ui.authentication

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.ui.AppBarConfiguration
import cn.pedant.SweetAlert.SweetAlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kejaplus.application.MainActivity
import com.kejaplus.application.R
import com.kejaplus.application.Support.InputValidator
import com.kejaplus.application.databinding.ActivitySignInBinding
import com.kejaplus.application.response.Status
import com.kejaplus.application.ui.authentication.SignUpActivity.Companion.RC_SIGN_IN
import com.kejaplus.utils.SweetAlerts
import java.util.*


class SignInActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySignInBinding
    private lateinit var  sweetAlertDialog: SweetAlertDialog
    private lateinit var auth:FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private val viewModel: SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //set up the action bar
        setSupportActionBar(binding.toolbar)

        auth = Firebase.auth

        // initializing the sweet alert dialog
        sweetAlertDialog = SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE)

        // login button
        binding.topContent.loginBtn.setOnClickListener(View.OnClickListener { view ->  inputValidation() })

        // navigate to sign up text
        binding.topContent.signUpTextBtn.setOnClickListener(View.OnClickListener { view -> navigate() })

        binding.topContent.signUpGoogle.setOnClickListener ( View.OnClickListener { view -> signInGoogle() } )

        signUpGoogle()


    }



    // check if values in edit text is empty
    private fun inputValidation() {
        val validator = InputValidator()
        if(validator.validateRequired(binding.topContent.emailLayout,binding.topContent.emailText) &&
            validator.validateRequired(binding.topContent.passwordLayout,binding.topContent.passwordText)
        ){
            signIn(Objects.requireNonNull(binding.topContent.emailText.text).toString(),
                Objects.requireNonNull(binding.topContent.passwordText.text).toString()
            );
        }
    }

       // navigate to sign up fragment
    private fun navigate(){
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun signIn(email: String, password: String) {

        viewModel.signInWithEmailAndPassword(email,password)

        viewModel.signInUserStatus.observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    success(
                        this,"Success","Welcome ${it.data}",
                        dismiss = {
                            binding.topContent.emailText.text?.clear()
                            binding.topContent.passwordText.text?.clear()
                            startActivity(Intent(this, MainActivity::class.java))
                            finish() })




                }

                Status.LOADING -> {
                    loading(this,"Loading")
                    //inputValidation()
                }
                Status.ERROR -> {

                    error(this,"Ooops",it.message.toString(),
                         dismiss = { sweetAlertDialog.dismiss() }
                        )
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

    private fun signInGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        launcher.launch(signInIntent)
    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        result ->
             if (result.resultCode == Activity.RESULT_OK){
                 val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                 Log.e("launcher", task.toString())
                 handleResult(task)
             }
    }

    private fun handleResult(task: Task<GoogleSignInAccount>) {
              if(task.isSuccessful){
                val account: GoogleSignInAccount? = task.result
                if(account != null){
                    updateUI(account)
                    Log.e("MainActivity", account.email.toString())
                }
              }
             else {
                  error(this,"Ooops",task.exception.toString(),
                      dismiss = { sweetAlertDialog.dismiss() }
                  )
              }
    }

    private fun updateUI(account: GoogleSignInAccount) {
        val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
        viewModel.signInWithGoogle(credentials)

        viewModel.signInGoogleStatus.observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    success(
                        this,"Success","Welcome ${it.data}",
                        dismiss = {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish() }
                    )
                }

                Status.LOADING -> {
                    loading(this,"Loading")
                    //inputValidation()
                }
                Status.ERROR -> {
                    error(this,"Ooops",it.message.toString(),
                        dismiss = { sweetAlertDialog.dismiss() }
                    )
                }

                else -> {

                }
            }
        })


    }




    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser!= null ) {
            val user = currentUser.email
            Log.d("Current User Email", user.toString())
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
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
    private fun loading(context: Context,msg: String){

        SweetAlerts.loading(
            context = context,
            msg = msg)
    }



}