package com.kejaplus.application.ui.authentication

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.*
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.kejaplus.Repository.AuthenticationRepository
import com.kejaplus.application.Model.Users
import com.kejaplus.application.response.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    //private val googleSignInHandler: GoogleSignInHandler,
    @ApplicationContext context: Context
) : ViewModel()
 {
    private val repository = AuthenticationRepository(context)



    private val _signInUserStatus = MutableLiveData<Resource<String>>()
    val signInUserStatus: LiveData<Resource<String>> = _signInUserStatus

    private val  _signInGoogleStatus = MutableLiveData<Resource<String>>()
    val signInGoogleStatus: LiveData<Resource<String>> = _signInGoogleStatus



    fun signInWithEmailAndPassword(email: String, password:String){
        viewModelScope.launch {
            _signInUserStatus.postValue(Resource.loading(null))
            try {
                val id: AuthResult = repository.login(email, password)
                val data = id.user?.email
                _signInUserStatus.postValue(Resource.success(data, ""))
            } catch (e: Exception){
                _signInUserStatus.postValue(Resource.error(null, e.message!!))
            }
        }
    }

    fun signInWithGoogle(credentials : AuthCredential){

        viewModelScope.launch {
            _signInGoogleStatus.postValue(Resource.loading(null))
            try {
                val users: AuthResult = repository.signInWithGoogle(credentials)
                val data = users.user?.displayName
                _signInGoogleStatus.postValue(Resource.success(data, ""))
            } catch (e: Exception){
                _signInUserStatus.postValue(Resource.error(null, e.message!!))
            }
        }

    }

    /**fun getSignInIntent() = googleSignInHandler.signInIntent()

     fun submitGoogleToken(intent: Intent?): Boolean {
         val idToken = googleSignInHandler.getIdToken(intent)
         Timber.i("Id token is $idToken")
         // Submit ID Token to API
         // to get access token
         return true
     }*/



}