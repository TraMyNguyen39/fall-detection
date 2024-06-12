package com.example.falldetection.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.falldetection.data.model.FallHistoryItem
import com.example.falldetection.data.model.User
import com.example.falldetection.data.repository.Repository
import kotlinx.coroutines.launch

class DeviceHistoryViewModel(
    private val deviceRepository : Repository.DeviceRepository,
    private val userRepository: Repository.UserRepository,
    private val userDeviceRepository: Repository.UserDeviceRepository
) : ViewModel() {

    private val _listFallHistory= MutableLiveData<List<FallHistoryItem>>()
    val listFallHistory: LiveData<List<FallHistoryItem>> = _listFallHistory

    private val _device = MutableLiveData<User?>()
    val device: LiveData<User?> = _device

    fun loadDeviceInfo(patientEmail: String) {
        viewModelScope.launch {
            val deviceInfo = userRepository.getUserByEmail(patientEmail)
            _device.postValue(deviceInfo)
        }
    }

    fun deleteObserver(userEmail: String, patientEmail: String, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val isSuccessful = userDeviceRepository.deleteObserver(userEmail, patientEmail)
            callback(isSuccessful)
        }
    }

    fun loadFallHistory(patientEmail: String) {
        viewModelScope.launch {
            var list = deviceRepository.getFallHistories(patientEmail)
            list = list.sortedByDescending { it.time?.time }
            _listFallHistory.postValue(list)
        }
    }

//    fun updateDeviceDetail(device: UserDevice?) {
//        _device.postValue(device)
//    }
}

class DeviceHistoryViewModelFactory(
    private val repository : Repository.DeviceRepository,
    private val userRepository: Repository.UserRepository,
    private val userDeviceRepository: Repository.UserDeviceRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeviceHistoryViewModel::class.java)) {
            return DeviceHistoryViewModel(repository, userRepository, userDeviceRepository) as T
        }
        throw IllegalArgumentException("Unknown DeviceHistoryViewModel class")
    }
}