package com.example.falldetection.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.falldetection.data.model.FallHistoryItem
import com.example.falldetection.data.model.UserDevice
import com.example.falldetection.data.repository.DeviceRepository
import kotlinx.coroutines.launch

class DeviceHistoryViewModel(
    private val repository: DeviceRepository
) : ViewModel() {

    private val _listFallHistory= MutableLiveData<List<FallHistoryItem>>()
    val listFallHistory: LiveData<List<FallHistoryItem>> = _listFallHistory

    private val _device = MutableLiveData<UserDevice?>()
    val device: LiveData<UserDevice?> = _device

    fun loadDeviceInfo(userDeviceId: String) {
        viewModelScope.launch {
            repository.getUserDeviceInfo(userDeviceId) { userDevice ->
                _device.postValue(userDevice)
            }
        }
    }

    fun loadFallHistory(deviceId: String) {
        viewModelScope.launch {
            val list = repository.getFallHistories(deviceId)
            _listFallHistory.postValue(list)
        }
    }

//    fun updateDeviceDetail(device: UserDevice?) {
//        _device.postValue(device)
//    }
}

class DeviceHistoryViewModelFactory(
    private val repository: DeviceRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DeviceHistoryViewModel::class.java)) {
            return DeviceHistoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}