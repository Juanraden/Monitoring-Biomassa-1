package com.kedaireka.monitoring_biomassa.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kedaireka.monitoring_biomassa.R
import com.kedaireka.monitoring_biomassa.repository.LoginRepository
import com.kedaireka.monitoring_biomassa.data.network.LoginResult
import dagger.hilt.android.lifecycle.HiltViewModel
import com.kedaireka.monitoring_biomassa.data.network.Result
import com.kedaireka.monitoring_biomassa.data.network.LoggedInUserView
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginResult = MutableLiveData<LoginResult>()
    val loginResult: LiveData<LoginResult> = _loginResult

    fun login(username: String, password: String) {
        // can be launched in a separate asynchronous job
//        viewModelScope.launch {
//            try {
//                val result = loginRepository.loginUser(username, password)
//
//                if (result is Result.Success) {
//                    _loginResult.value =
//                        LoginResult(success = LoggedInUserView(displayName = result.data.username))
//                } else {
//                    _loginResult.value = LoginResult(error = R.string.invalid_input)
//                }
//            } catch (e: Exception){
//                _loginResult.value = LoginResult(error = R.string.login_failed)
//            }
//        }
        _loginResult.value =
            LoginResult(success = LoggedInUserView(displayName = "Robertus Agung"))
    }

//    init {
//        val checkLoggedIn = loginRepository.isUserLoggedIn()
//
//        if (checkLoggedIn is Result.Success) {
//            _loginResult.value =
//                LoginResult(success = LoggedInUserView(displayName = checkLoggedIn.data.username))
//        }
//    }
}