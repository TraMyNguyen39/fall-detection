package com.example.falldetection.data.datasource.remote

import com.example.falldetection.data.model.UserDevice
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


    override suspend fun checkObserver(userEmail: String, patientEmail: String): Boolean {
        try {
            val querySnapshot = database
                .collection("user-device")
                .whereEqualTo("userEmail", userEmail)
                .whereEqualTo("patientEmail", patientEmail)
                .get()
                .await()

            for (document in querySnapshot.documents) {
                return true
            }
        } catch (e: Exception) {
            println("Error getting user: $e")
            return true
        }
        return false
    }

    override suspend fun deleteObserver(userEmail: String, patientEmail: String): Boolean {
        return try {
            database
                .collection("user-device")
                .document("${userEmail}_${patientEmail}")
                .delete()
                .await()
            true
        } catch (e: Exception) {
            println("Error getting user: $e")
            false
        }
    }
}