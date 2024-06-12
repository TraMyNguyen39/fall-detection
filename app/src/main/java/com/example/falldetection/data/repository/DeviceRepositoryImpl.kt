package com.example.falldetection.data.repository

import com.example.falldetection.data.model.FallHistoryItem
import com.example.falldetection.data.datasource.RemoteDataSource

class DeviceRepositoryImpl(
    private val dataSource: RemoteDataSource.DeviceDataSource,
) : Repository.DeviceRepository {
    override suspend fun getFallHistories(patientEmail: String): List<FallHistoryItem> {
        return dataSource.getFallHistories(patientEmail)
    }
}