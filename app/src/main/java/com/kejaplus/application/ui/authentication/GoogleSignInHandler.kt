package com.kejaplus.application.ui.authentication

import android.content.Context
import android.content.Intent
import android.provider.Settings.Global.getString
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.kejaplus.application.R
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class GoogleSignInHandler @Inject constructor(@ApplicationContext context: Context)  {
    val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()


    private val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)

    fun signInIntent() = googleSignInClient.signInIntent

    fun getIdToken(intent: Intent?): String? {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
            val account = task.getResult(ApiException::class.java)
            return account.idToken
        } catch (e: ApiException) {
            Timber.e("Google sign in failed: ${e.message}")
            null
        }
    }
}