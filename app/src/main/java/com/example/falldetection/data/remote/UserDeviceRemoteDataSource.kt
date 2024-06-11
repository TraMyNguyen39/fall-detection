package com.example.falldetection.data.datasource.remote

import com.example.falldetection.data.model.UserDevice
import com.example.falldetection.data.remote.RemoteDataSource
import com.example.falldetection.data.remote.UserRemoteDataSource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserDeviceRemoteDataSource(
    private val database: FirebaseFirestore,
    private val userRemoteDataSource: UserRemoteDataSource
) : RemoteDataSource.UserDeviceDataSource {
    override suspend fun getAllPatientOfUser(userEmail: String): List<UserDevice> {
        val result = arrayListOf<UserDevice>()
        try {
            val querySnapshot = database
                .collection("user-device")
                .whereEqualTo("userEmail", userEmail).get()
                .await()

            for (document in querySnapshot.documents) {
                val device = document.toObject(UserDevice::class.java)
                device?.let {
//                    device.id = document.id
                    val patient = userRemoteDataSource.getUserByEmail(device.patientEmail)
                    if (patient != null) {
                        device.birthDate = patient.birthDate
                        device.fullName = patient.fullName
                    }
                    result.add(it)
                }
            }
        } catch (e: Exception) {
            println("Error getting user: $e")
        }
        return result
    }
}