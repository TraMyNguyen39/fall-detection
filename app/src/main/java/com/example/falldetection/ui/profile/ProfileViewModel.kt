package com.example.falldetection.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.falldetection.R
import com.example.falldetection.data.model.User
import com.example.falldetection.data.repository.Repository
import com.example.falldetection.utils.Gender
import com.example.falldetection.utils.Utils
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: Repository.UserRepository
) : ViewModel() {
    private val _profile = MutableLiveData<User?>()
    val profile: LiveData<User?> = _profile

    private val _profileFormState = MutableLiveData<ProfileFormState>()
    val profileFormState: LiveData<ProfileFormState> = _profileFormState

    private val _updateOrRegisterPatientMess = MutableLiveData<Int?>()
    val updateOrRegisterPatientMess: LiveData<Int?> = _updateOrRegisterPatientMess

    fun loadProfile(email: String) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(email)
            user?.let { _profile.postValue(user) }
        }
    }

    fun signout(email: String) {
        repository.removeToken(email, Utils.token!!)
        repository.logout()
    }
    fun updateProfile(
        email: String,
        fullName: String,
        birthDate: String,
        phoneNumber: String,
        gender: Gender
    ) {
        viewModelScope.launch {
            val user = User(
                email = email,
                fullName = fullName,
                birthDate = Utils.stringToDate(birthDate),
                phoneNumber = phoneNumber,
                gender = gender.ordinal
            )
            val result = repository.updateUserInfo(user)
            if (result) {
                _updateOrRegisterPatientMess.postValue(R.string.txt_update_profile_success)
            } else {
                _updateOrRegisterPatientMess.postValue(R.string.txt_unknown_error)
            }
        }
    }
    fun registerBringDevice (
        email: String,
        fullName: String,
        birthDate: String,
        phoneNumber: String,
        gender: Gender
    ) {
        if (birthDate.isBlank() || phoneNumber.isBlank()) {
            _profileFormState.postValue(ProfileFormState(errorMessage = R.string.txt_error_blank))
            return
        }

        viewModelScope.launch {
            val user = User(
                email = email,
                fullName = fullName,
                birthDate = Utils.stringToDate(birthDate),
                phoneNumber = phoneNumber,
                gender = gender.ordinal
            )

            val isSuccessful = repository.registerBringDevice(user)
            if (isSuccessful) {
                _updateOrRegisterPatientMess.postValue(R.string.txt_register_patient_success)
            } else {
                _updateOrRegisterPatientMess.postValue(R.string.txt_unknown_error)
            }
        }
    }

    fun cancelBringDevice(userEmail: String) {
        viewModelScope.launch {
            val isSuccessful = repository.cancelBringDevice(userEmail)
            if (isSuccessful) {
                _updateOrRegisterPatientMess.postValue(R.string.txt_cancel_patient_success)
            } else {
                _updateOrRegisterPatientMess.postValue(R.string.txt_unknown_error)
            }
        }
    }
    fun resetState() {
        _updateOrRegisterPatientMess.postValue(null)
    }

    fun checkFormState(fullName: String, birthDate: String, phoneNumber: String) : Boolean {
        if (fullName.isBlank()) {
            _profileFormState.postValue(ProfileFormState(errorMessage = R.string.txt_error_name_blank))
            return false
        }
        if (birthDate.isNotBlank()) {
            if (!Utils.isValidDate(birthDate)) {
                _profileFormState.postValue(ProfileFormState(errorMessage = R.string.txt_date_error))
                return false
            }
        }
        if (phoneNumber.isNotBlank()) {
            if (!Utils.isValidPhoneNumber(phoneNumber)) {
                _profileFormState.postValue(ProfileFormState(errorMessage = R.string.txt_error_phone_number_error))
                return false
            }
        }

        _profileFormState.postValue(ProfileFormState(isCorrect = true))
        return true
    }
}

class ProfileViewModelFactory(
    private val repository: Repository.UserRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ProfileViewModel::class.java)) {
            return ProfileViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ProfileViewModel class")
    }
}