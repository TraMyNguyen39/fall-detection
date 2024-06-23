package com.example.falldetection.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.falldetection.data.model.UserDevice
import com.example.falldetection.data.repository.Repository
import com.example.falldetection.utils.Role
import com.example.falldetection.utils.Utils
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class TrackedDevicesViewModel(
    private val repository: Repository.UserDeviceRepository,
    private val userRepository: Repository.UserRepository
) : ViewModel() {
    private val _listDevices = MutableLiveData<List<UserDevice>>()
    val listDevices: LiveData<List<UserDevice>> = _listDevices
    private val _searchResults = MutableLiveData<List<UserDevice>>()
    val searchResults: LiveData<List<UserDevice>> = _searchResults

    private var searchJob: Job? = null

    private suspend fun loadRole(userEmail: String) {
        Utils.role = userRepository.getUserByEmail(userEmail)?.role ?: Role.SUPERVISOR.ordinal
    }

    fun loadUserDevices(userEmail: String) {
        val list = arrayListOf<UserDevice>()
        viewModelScope.launch {
            loadRole(userEmail)
            // Neu nguoi dung mang thiet bi, them thiet bi vao danh sach
            if (Utils.role == Role.DEVICE.ordinal) {
                val currentAccount = UserDevice(
                    userEmail,
                    userEmail,
                    "Bạn",
                    null,
                    "IMG_20240519_092706_737.jpg",
                    null
                )
                list.add(currentAccount)
            }

            val listPatient = repository.getAllPatientOfUser(userEmail)
            list.addAll(listPatient)
            _listDevices.postValue(list)
        }
    }
    fun searchDebounced(searchText: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(500) // wait for 500ms of inactivity
            if (searchText.isBlank()) {
                _searchResults.postValue(_listDevices.value)
            } else {
                val list = ArrayList<UserDevice>()
                for (item in _listDevices.value!!) {
                    if (item.reminderName.contains(searchText, ignoreCase = true)) {
                        list.add(item)
                    }
                }
                _searchResults.postValue(list)
            }
        }
    }
}
class TrackedDevicesViewModelFactory(
    private val repository: Repository.UserDeviceRepository,
    private val userRepository: Repository.UserRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TrackedDevicesViewModel::class.java)) {
            return TrackedDevicesViewModel(repository, userRepository) as T
        }
        throw IllegalArgumentException("Unknown UserDevicesViewModel class")
    }
}