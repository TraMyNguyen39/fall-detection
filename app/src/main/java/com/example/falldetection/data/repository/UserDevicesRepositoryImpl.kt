package com.example.falldetection.data.repository

import com.example.falldetection.data.model.UserDevice
import com.example.falldetection.data.remote.RemoteDataSource

class UserDevicesRepositoryImpl(
    private val remoteDataSource: RemoteDataSource.UserDeviceDataSource
) : Repository.UserDeviceRepository {
    override suspend fun getAllPatientOfUser(userEmail: String): List<UserDevice> {
        return remoteDataSource.getAllPatientOfUser(userEmail)
    }
    override suspend fun deleteObserver(userEmail: String, patientEmail: String): Boolean {
        return remoteDataSource.deleteObserver(userEmail, patientEmail)
    }
}