package com.example.falldetection.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.falldetection.data.model.UserDevice
import com.example.falldetection.data.repository.UserDevicesRepository
import kotlinx.coroutines.launch

class UserDevicesViewModel(
    private val repository: UserDevicesRepository
) : ViewModel() {
    private val _listDevices = MutableLiveData<List<UserDevice>>()
    val listDevices: LiveData<List<UserDevice>> = _listDevices

//    private val _device = MutableLiveData<UserDevice?>()
//    val device: LiveData<UserDevice?> = _device

    init {
        loadUserDevices()
    }
    fun loadUserDevices() {
        viewModelScope.launch {
            val list = repository.getAllSupervisedByUser()
            _listDevices.postValue(list)
        }
    }

//    fun updateDeviceDetail(device: UserDevice?) {
//        _device.postValue(device)
//    }
}
class TrackedDevicesViewModelFactory(
    private val repository: UserDevicesRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserDevicesViewModel::class.java)) {
            return UserDevicesViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}