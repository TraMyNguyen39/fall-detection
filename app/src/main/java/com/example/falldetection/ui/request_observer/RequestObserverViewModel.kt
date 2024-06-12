package com.example.falldetection.ui.request_observer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.falldetection.data.model.ObserverRequest
import com.example.falldetection.data.repository.Repository
import kotlinx.coroutines.launch

class RequestObserverViewModel (
    private val repository: Repository.ObserverRequestRepository
) : ViewModel() {
    private val _listRequest = MutableLiveData<List<ObserverRequest>>()
    val listRequest: LiveData<List<ObserverRequest>> = _listRequest

    private val _requestMessage = MutableLiveData<String>()
    val requestMessage: LiveData<String> = _requestMessage

    fun loadAllRequest(userEmail: String) {
        viewModelScope.launch {
            val list = repository.loadAllRequest(userEmail)
            _listRequest.postValue(list)
        }
    }

    fun acceptRequest(request: ObserverRequest) {
        viewModelScope.launch {
            val result = repository.acceptRequest(request)
            if (result) {
                _requestMessage.postValue("Bạn đã cho phép ${request.supervisorEmail} theo dõi!")
            } else {
                _requestMessage.postValue("Đã có lỗi xảy ra!")
            }
        }
    }

    fun denyRequest(request: ObserverRequest) {
        viewModelScope.launch {
            val result = repository.denyRequest(request)
            if (result) {
                _requestMessage.postValue("Bạn đã hủy yêu cầu theo dõi của ${request.supervisorEmail}!")
            } else {
                _requestMessage.postValue("Đã có lỗi xảy ra!")
            }
        }
    }
}

class RequestObserverViewModelFactory(
    private val repository: Repository.ObserverRequestRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RequestObserverViewModel::class.java)) {
            return RequestObserverViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown RequestObserverViewModel class")
    }
}