package com.example.falldetection.viewmodel

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.falldetection.R
import com.example.falldetection.data.model.LoginFormState
import com.example.falldetection.data.model.User
import com.example.falldetection.data.repository.UserRepository
import com.google.firebase.FirebaseNetworkException
import kotlinx.coroutines.launch

class UserViewModel(private val repository: UserRepository) : ViewModel() {
    private val _loginFormState = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginFormState

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user
    fun login(email: String, password: String) {
        repository.login(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (repository.getCurrentAccount()?.isEmailVerified == true) {
                        viewModelScope.launch {
                            val user = repository.getUserByEmail(email)
                            _user.postValue(user)
                        }
                    } else {
                        _loginFormState.postValue(
                            LoginFormState(errorMessage = R.string.txt_require_validate_account)
                        )
                    }
                } else {
                    if (task.exception is FirebaseNetworkException) {
                        _loginFormState.postValue(
                            LoginFormState(errorMessage = R.string.txt_no_internet)
                        )
                    } else {
                        _loginFormState.postValue(
                            LoginFormState(errorMessage = R.string.txt_wrong_username_password)
                        )
                    }
                }
            }
    }

    fun loginFormChange(email: String, password: String) {
        if (email.isBlank()) {
            _loginFormState.postValue(LoginFormState(emailError = R.string.txt_error_email_blank))
        } else if (!isValidEmail(email)) {
            _loginFormState.postValue(LoginFormState(emailError = R.string.txt_error_email_invalid))
        } else if (password.isBlank()) {
//            _loginFormState.postValue(LoginFormState(passwordError = R.string.txt_error_password_format))
            _loginFormState.postValue(LoginFormState(passwordError = R.string.txt_error_password_blank))
        } else {
            _loginFormState.postValue(LoginFormState(isCorrect = true))
        }
    }

//    private fun isValidPassword(password: String): Boolean {
//        val regex = Regex("^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$")
//        return regex.matches(password)
//    }

    private fun isValidEmail(email: String): Boolean {
        return if (email.contains("@")) {
            Patterns.EMAIL_ADDRESS.matcher(email).matches()
        } else {
            false
        }
    }
}