package com.example.falldetection.ui.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.falldetection.R
import com.example.falldetection.data.repository.Repository
import com.example.falldetection.utils.Utils
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val observerRequestRepository: Repository.ObserverRequestRepository,
    private val userDeviceRepository: Repository.UserDeviceRepository
) : ViewModel() {
    private val _registerFormState = MutableLiveData<RegisterFormState>()
    val registerFormState: LiveData<RegisterFormState> = _registerFormState

    private val _registerMessage = MutableLiveData<String?>()
    val registerMessage: LiveData<String?> = _registerMessage

    fun registerFormChange(email: String, reminderName: String) {
        if (email.isBlank()) {
            _registerFormState.postValue(RegisterFormState(emailError = R.string.txt_error_email_blank))
        } else if (!Utils.isValidEmail(email)) {
            _registerFormState.postValue(RegisterFormState(emailError = R.string.txt_error_email_invalid))
        } else if (reminderName.isBlank()) {
            _registerFormState.postValue(RegisterFormState(reminderNameError = R.string.txt_error_reminder_name_blank))
        } else {
            _registerFormState.postValue(RegisterFormState(isCorrect = true))
        }
    }

    fun registerObserver(userEmail: String, patientEmail: String, reminderName: String) {
        if (userEmail == patientEmail) {
            _registerMessage.postValue("Không thể đăng ký theo dõi chính mình!")
            return
        }

        viewModelScope.launch {
            val isExistConnect = userDeviceRepository.checkObserver(userEmail, patientEmail)
            if (!isExistConnect) {
                val resultMessage =
                    observerRequestRepository.sendNewRequest(userEmail, patientEmail, reminderName)
                _registerMessage.postValue(resultMessage)
            } else {
                _registerMessage.postValue("Bạn đã theo dõi người dùng này")
            }
        }
    }
}

class RegisterViewModelFactory(
    private val observerRequestRepository: Repository.ObserverRequestRepository,
    private val userDeviceRepository: Repository.UserDeviceRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RegisterViewModel::class.java)) {
            return RegisterViewModel(observerRequestRepository, userDeviceRepository) as T
        }
        throw IllegalArgumentException("Unknown RegisterViewModel class")
    }
}