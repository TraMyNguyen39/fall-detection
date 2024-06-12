package com.example.falldetection.data.repository

import com.example.falldetection.data.datasource.RemoteDataSource
import com.example.falldetection.data.model.ObserverRequest
import java.util.Date

class ObserverRequestRepositoryImpl(
    private val dataSource: RemoteDataSource.ObserverRequestDataSource,
) : Repository.ObserverRequestRepository {
    override suspend fun sendNewRequest(
        userEmail: String,
        patientEmail: String,
        reminderName: String
    ): String? {
            val currentTime = Date()
            val request = ObserverRequest(userEmail, patientEmail, currentTime, reminderName)
            return dataSource.sendNewRequest(request)
    }

    override suspend fun loadAllRequest(userEmail: String): List<ObserverRequest> {
        return dataSource.loadAllRequest(userEmail)
    }

    override suspend fun acceptRequest(observerRequest: ObserverRequest): Boolean {
        return dataSource.acceptRequest(observerRequest)
    }

    override suspend fun denyRequest(observerRequest: ObserverRequest): Boolean {
        return dataSource.denyRequest(observerRequest)
    }
}