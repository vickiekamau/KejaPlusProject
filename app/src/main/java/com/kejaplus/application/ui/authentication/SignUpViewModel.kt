package com.kejaplus.application.ui.authentication

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.kejaplus.Repository.AuthenticationRepository
import com.kejaplus.application.Model.Property
import com.kejaplus.application.Model.Users
import com.kejaplus.application.response.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    //private val googleSignInHandler: GoogleSignInHandler,
    @ApplicationContext context: Context
) : ViewModel() {

    private val repository = AuthenticationRepository(context)


    private val _signUpUserStatus = MutableLiveData<Resource<String>>()
    val signUpUserStatus: LiveData<Resource<String>> = _signUpUserStatus

    private val  _signUpGoogleStatus = MutableLiveData<Resource<String>>()
    val signUpGoogleStatus: LiveData<Resource<String>> = _signUpGoogleStatus

    fun signUpCredentials(users: Users) {

        viewModelScope.launch {
            _signUpUserStatus.postValue(Resource.loading(null))
            try {
                val data = repository.createUser(users)
                _signUpUserStatus.postValue(Resource.success(data, ""))

            } catch (exception: Exception) {
                _signUpUserStatus.postValue(Resource.error(null, exception.message!!))
            }
        }
    }

    fun signUpWithEmailAndPassword(email: String, password:String){
        viewModelScope.launch {
            _signUpUserStatus.postValue(Resource.loading(null))
            try {
                val id: AuthResult = repository.create(email, password)
                val data = id.toString()
                _signUpUserStatus.postValue(Resource.success(data, ""))
            } catch (e: Exception){
                _signUpUserStatus.postValue(Resource.error(null, e.message!!))
            }
        }
    }

    fun signUpWithGoogle(credentials : AuthCredential){

        viewModelScope.launch {
            _signUpGoogleStatus.postValue(Resource.loading(null))
            try {
                val users: AuthResult = repository.signInWithGoogle(credentials)
                val data = users.user?.displayName
                _signUpGoogleStatus.postValue(Resource.success(data, ""))
            } catch (e: Exception){
                _signUpUserStatus.postValue(Resource.error(null, e.message!!))
            }
        }

    }

}