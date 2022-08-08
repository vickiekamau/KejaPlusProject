package com.kejaplus.application.ui.authentication

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.kejaplus.Repository.AuthenticationRepository
import com.kejaplus.application.Model.Users
import com.kejaplus.application.response.Resource
import kotlinx.coroutines.launch

class SignInViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthenticationRepository(application)


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
                val data = users.user?.email
                _signInGoogleStatus.postValue(Resource.success(data, ""))
            } catch (e: Exception){
                _signInUserStatus.postValue(Resource.error(null, e.message!!))
            }
        }

    }


}