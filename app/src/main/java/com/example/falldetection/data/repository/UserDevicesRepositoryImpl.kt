package com.example.falldetection.data.repository

import com.example.falldetection.data.datasource.RemoteDataSource
import com.example.falldetection.data.model.UserDevice

class UserDevicesRepositoryImpl(
    private val remoteDataSource: RemoteDataSource.UserDeviceDataSource
) : Repository.UserDeviceRepository {
    override suspend fun getAllPatientOfUser(userEmail: String): List<UserDevice> {
        return remoteDataSource.getAllPatientOfUser(userEmail)
    }
    override suspend fun checkObserver(userEmail: String, patientEmail: String): Boolean {
        return remoteDataSource.checkObserver(userEmail, patientEmail)
    }

    override suspend fun deleteObserver(userEmail: String, patientEmail: String): Boolean {
        return remoteDataSource.deleteObserver(userEmail, patientEmail)
    }
}