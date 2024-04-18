package com.example.falldetection.data.repository

import com.example.falldetection.data.model.UserDevice

interface UserDevicesRepository {
    suspend fun getAllSupervisedByUser() : List<UserDevice>
}