package com.example.falldetection.data.repository

import com.example.falldetection.data.model.FallHistoryItem
import com.example.falldetection.data.model.UserDevice

interface DeviceRepository {
    suspend fun getUserDeviceInfo(deviceUserId: String, callback: ( UserDevice?) -> Unit)
    suspend fun getFallHistories(deviceId: String) : List<FallHistoryItem>
    suspend fun getDeviceFullName(deviceId: String) : String?
}