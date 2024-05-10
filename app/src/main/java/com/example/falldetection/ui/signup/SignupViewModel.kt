package com.example.falldetection.ui.signup

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.falldetection.R
import com.example.falldetection.data.model.User
import com.example.falldetection.data.repository.Repository
import com.example.falldetection.utils.Role
import com.example.falldetection.utils.Utils
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.launch

class SignupViewModel(
    private val repository: Repository.UserRepository
) : ViewModel() {
    private val _signupFormState = MutableLiveData<SignupFormState>()
    val signupFormState: LiveData<SignupFormState> = _signupFormState

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    fun signup(
        email: String,
        password: String,
        confirmPassword: String,
        fullName: String,
        callback: (Int?) -> Unit
    ) {
        // check if confirmPassword == password
        if (password != confirmPassword) {
            callback(R.string.txt_confirm_pass_not_equal_pass)
            return
        }

        viewModelScope.launch {
            val user = User(email, password, fullName)
            val result = repository.signup(user)
            if (result.success) {
                callback(R.string.txt_check_your_email_signup)
            } else {
                callback(result.error)
            }
        }
    }

    fun signupFormChange(email: String, password: String, fullName: String) {
        if (email.isBlank()) {
            _signupFormState.postValue(SignupFormState(emailError = R.string.txt_error_email_blank))
        } else if (!Utils.isValidEmail(email)) {
            _signupFormState.postValue(SignupFormState(emailError = R.string.txt_error_email_invalid))
        } else if (fullName.isBlank()) {
            _signupFormState.postValue(SignupFormState(fullNameError = R.string.txt_error_name_blank))
        } else if (password.isBlank()) {
            _signupFormState.postValue(SignupFormState(passwordError = R.string.txt_error_password_blank))
        } else if (!Utils.isValidPassword(password)) {
            _signupFormState.postValue(SignupFormState(passwordError = R.string.txt_error_password_format))
        } else {
            _signupFormState.postValue(SignupFormState(isCorrect = true))
        }
    }
}

class SignupViewModelFactory(
    private val repository: Repository.UserRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignupViewModel::class.java)) {
            return SignupViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown SignupViewModel class")
    }
}