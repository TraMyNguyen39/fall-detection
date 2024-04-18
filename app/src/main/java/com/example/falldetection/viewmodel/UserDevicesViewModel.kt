package com.example.falldetection.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.falldetection.data.model.UserDevice
import com.example.falldetection.data.repository.UserDevicesRepository
import kotlinx.coroutines.launch

class UserDevicesViewModel(
    private val repository: UserDevicesRepository
) : ViewModel() {
    private val _listDevices = MutableLiveData<List<UserDevice>>()
    val listDevices: LiveData<List<UserDevice>> = _listDevices

    private val _device = MutableLiveData<UserDevice>()
    val device: LiveData<UserDevice> = _device

    init {
        loadUserDevices()
    }
    fun loadUserDevices() {
        viewModelScope.launch {
            val list = repository.getAllSupervisedByUser()
            _listDevices.postValue(list)
        }
    }
}