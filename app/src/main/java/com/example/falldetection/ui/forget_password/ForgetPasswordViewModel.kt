package com.example.falldetection.ui.forget_password

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.falldetection.R
import com.example.falldetection.data.repository.Repository
import com.example.falldetection.utils.Utils
import kotlinx.coroutines.launch

class ForgetPasswordViewModel (
  private val repository: Repository.UserRepository
) : ViewModel() {
    private val _forgetPassFormState = MutableLiveData<Int?>()
    val forgetPassFormState: LiveData<Int?> = _forgetPassFormState

    private val _forgetPassMessage = MutableLiveData<Int?>()
    val forgetPassMessage: LiveData<Int?> = _forgetPassMessage

    fun forgetPasswordFormChange(email: String) {
        if (email.isBlank()) {
            _forgetPassFormState.postValue(R.string.txt_error_email_blank)
        } else if (!Utils.isValidEmail(email)) {
            _forgetPassFormState.postValue(R.string.txt_error_email_invalid)
        } else {
            _forgetPassFormState.postValue(null)
        }
    }

    fun sendResetPassword(email: String) {
        viewModelScope.launch {
            val result = repository.sendPasswordResetEmail(email)
            if (result.success) {
                _forgetPassMessage.postValue(result.data)
            } else {
                _forgetPassMessage.postValue(result.error)
            }
        }
    }

    fun resetMessage() {
        _forgetPassMessage.postValue(null)
    }
}

class ForgetPasswordViewModelFactory(
    private val repository: Repository.UserRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgetPasswordViewModel::class.java)) {
            return ForgetPasswordViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ForgetPasswordViewModel class")
    }
}