package com.kejaplus.application.ui.authentication

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.AuthResult
import com.kejaplus.Repository.AuthenticationRepository
import com.kejaplus.application.Model.Property
import com.kejaplus.application.Model.Users
import com.kejaplus.application.response.Resource
import kotlinx.coroutines.launch

class SignUpViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthenticationRepository(application)


    private val _signUpUserStatus = MutableLiveData<Resource<String>>()
    val signUpUserStatus: LiveData<Resource<String>> = _signUpUserStatus

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

}