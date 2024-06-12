package com.example.falldetection.data.datasource.remote

import com.example.falldetection.data.model.FallHistoryItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DeviceRemoteDataSource(
    private val database: FirebaseFirestore
) : RemoteDataSource.DeviceDataSource {
    override suspend fun getFallHistories(patientEmail: String): List<FallHistoryItem> {
        val result = arrayListOf<FallHistoryItem>()
        try {
            val querySnapshot = database
                .collection("fall-history")
                .whereEqualTo("patientEmail", patientEmail).get()
                .await()

            for (document in querySnapshot.documents) {
                val device = document.toObject(FallHistoryItem::class.java)
                device?.let {
                    result.add(it)
                }
            }
        } catch (e: Exception) {
            println("Error getting user: $e")
        }
        return result
    }
}