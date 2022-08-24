package com.kejaplus.application.ui.authentication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.ui.AppBarConfiguration
import cn.pedant.SweetAlert.SweetAlertDialog
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
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
import com.kejaplus.utils.SweetAlerts
import kotlinx.coroutines.tasks.await
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList


class SignInActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivitySignInBinding
    private lateinit var sweetAlertDialog: SweetAlertDialog
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    //Declare the Facebook callbackManager
    private lateinit var callBackManager: CallbackManager
    var name = ""
    var picture = ""
    var email = ""

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
        binding.topContent.loginBtn.setOnClickListener(View.OnClickListener { view -> inputValidation() })

        // navigate to sign up text
        binding.topContent.signUpTextBtn.setOnClickListener(View.OnClickListener { view -> navigate() })

        binding.topContent.signUpGoogle.setOnClickListener(View.OnClickListener { view -> signInGoogle() })

        // facebook button
        binding.topContent.signUpFacebook.setOnClickListener(View.OnClickListener { view -> facebookSignIn() })

        signUpGoogle()



        //Now initialize the callbackManager
        callBackManager = CallbackManager.Factory.create()


        LoginManager.getInstance().registerCallback(callBackManager,
            object : FacebookCallback<LoginResult?> {

                override fun onCancel() {
                    error(this@SignInActivity, "Ooops", "Facebook Login Canceled ",
                        dismiss = { sweetAlertDialog.dismiss() }
                    )
                }

                override fun onError(exception: FacebookException) {
                    error(this@SignInActivity, "Ooops", "Error Will Login ",
                        dismiss = { sweetAlertDialog.dismiss() }
                    )
                }

                override fun onSuccess(result: LoginResult?) {
                    Log.d("TAG", "Success Login")

                     getUserProfile(result?.accessToken, result?.accessToken?.userId)


                }
            })

        //CHECK IF FACEBOOK LOGGED IN
        if (faceBookIsLoggedIn()) {
            // Show the Activity with the logged in user
            navigateMain()
        }


    }

    private fun facebookSignIn() {

        LoginManager.getInstance().logInWithReadPermissions(this,listOf("public_profile", "email"))


    }


    // check if values in edit text is empty
    private fun inputValidation() {
        val validator = InputValidator()
        if (validator.validateRequired(
                binding.topContent.emailLayout,
                binding.topContent.emailText
            ) &&
            validator.validateRequired(
                binding.topContent.passwordLayout,
                binding.topContent.passwordText
            )
        ) {
            signIn(
                Objects.requireNonNull(binding.topContent.emailText.text).toString(),
                Objects.requireNonNull(binding.topContent.passwordText.text).toString()
            );
        }
    }



    private fun signIn(email: String, password: String) {

        viewModel.signInWithEmailAndPassword(email, password)

        viewModel.signInUserStatus.observe(this, androidx.lifecycle.Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    success(
                        this, "Success", "Welcome ${it.data}",
                        dismiss = {
                            binding.topContent.emailText.text?.clear()
                            binding.topContent.passwordText.text?.clear()
                            navigateMain()
                        })


                }

                Status.LOADING -> {
                    loading(this, "Loading")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callBackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                Log.e("launcher", task.toString())

                handleResult(task)
            }

        }




    private fun handleResult(task: Task<GoogleSignInAccount>) {
        if (task.isSuccessful) {
            val account: GoogleSignInAccount? = task.result
            if (account != null) {
                updateUI(account)
                Log.e("MainActivity", account.email.toString())
            }
        } else {
            error(this, "Ooops", task.exception.toString(),
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
                        this, "Success", "Welcome ${it.data}",
                        dismiss = {
                            navigateMain()
                        }
                    )
                }

                Status.LOADING -> {
                    loading(this, "Loading")
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


    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
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

    private fun loading(context: Context, msg: String) {

        SweetAlerts.loading(
            context = context,
            msg = msg
        )
    }
   

    @SuppressLint("LongLogTag")
    fun getUserProfile(token: AccessToken?, userId: String?){

        val parameters = Bundle()
        parameters.putString("fields", "name,  email")
        GraphRequest(token, "/$userId/", parameters, HttpMethod.GET, GraphRequest.Callback { response ->
                val jsonObject = response.jsonObject ?: return@Callback

                // Facebook Access Token
                // You can see Access Token only in Debug mode.
                // You can't see it in Logcat using Log.d, Facebook did that to avoid leaking user's access token.
                if (BuildConfig.DEBUG) {
                    FacebookSdk.setIsDebugEnabled(true)
                    FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)
                }
                // Facebook Name
                if (jsonObject.has("name")) {
                    val facebookName = jsonObject.getString("name")
                    Log.i("Facebook Name: ", facebookName)
                    name = facebookName
                } else {
                    Log.i("Facebook Name: ", "Not exists")
                }



                // Facebook Email
                if (jsonObject.has("email")) {
                    val facebookEmail = jsonObject.getString("email")
                    Log.i("Facebook Email: ", facebookEmail)
                    email = facebookEmail
                } else {
                    Log.i("Facebook Email: ", "Not exists")
                }

            loading(
                this@SignInActivity, "Signing")
            success(
                this@SignInActivity, "Welcome", "$name",
                dismiss = {
                    navigateMain()
                })
            }).executeAsync()



    }

    private fun  faceBookIsLoggedIn(): Boolean {
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        return isLoggedIn
    }

    // navigate to sign up fragment
    private fun navigate() {
        val intent = Intent(this, SignUpActivity::class.java)
        startActivity(intent)
        finish()
    }

    // navigate to sign up fragment
    private fun navigateMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}


