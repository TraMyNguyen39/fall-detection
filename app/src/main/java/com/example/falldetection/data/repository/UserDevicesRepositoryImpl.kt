package com.example.falldetection.data.repository

import com.example.falldetection.data.model.User
import com.example.falldetection.data.model.UserDevice
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserDevicesRepositoryImpl(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore
) : UserDevicesRepository {
    override suspend fun getAllSupervisedByUser(): List<UserDevice> {
        val result = arrayListOf<UserDevice>()
        try {
            val uid = auth.currentUser?.uid
            val querySnapshot = database
                .collection("supervisor-user")
                .whereEqualTo("userId", uid).get()
                .await()

            for (document in querySnapshot.documents) {
                val device = document.toObject(UserDevice::class.java)
                device?.let {
                    device.id = document.id
                    result.add(it)
                }
            }
        } catch (e: Exception) {
            println("Error getting user: $e")
        }
        return result
    }
}