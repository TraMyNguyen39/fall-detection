package com.example.falldetection.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.falldetection.R
import com.example.falldetection.data.model.User
import com.example.falldetection.data.repository.Repository
import com.example.falldetection.utils.Utils
import kotlinx.coroutines.launch

class LoginViewModel (
    private val repository: Repository.UserRepository
) : ViewModel() {
    private val _loginFormState = MutableLiveData<LoginFormState>()
    val loginFormState: LiveData<LoginFormState> = _loginFormState

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    fun login(email: String, password: String, callback: (Int?) -> Unit) {
        viewModelScope.launch {
            val result = repository.login(email, password)
            if (result.success) {
                _user.postValue(result.data)
            } else {
                _user.postValue(null)
                callback(result.error)
            }
        }
    }
    fun resetAccount() {
        _user.value = null
    }

    fun loginFormChange(email: String, password: String) {
        if (email.isBlank()) {
            _loginFormState.postValue(LoginFormState(emailError = R.string.txt_error_email_blank))
        } else if (!Utils.isValidEmail(email)) {
            _loginFormState.postValue(LoginFormState(emailError = R.string.txt_error_email_invalid))
        } else if (password.isBlank()) {
            _loginFormState.postValue(LoginFormState(passwordError = R.string.txt_error_password_blank))
        } else {
            _loginFormState.postValue(LoginFormState(isCorrect = true))
        }
    }
}

class LoginViewModelFactory(private val repository: Repository.UserRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            return LoginViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown LoginViewModel class")
    }
}