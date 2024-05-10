package com.example.falldetection.data.repository

import android.util.Log
import com.example.falldetection.data.model.FallHistoryItem
import com.example.falldetection.data.model.User
import com.example.falldetection.data.model.UserDevice
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DeviceRepositoryImpl(
    private val database: FirebaseFirestore,
) : DeviceRepository {
    override suspend fun getUserDeviceInfo(deviceUserId: String, callback: (UserDevice?) -> Unit) {
        val docRef: DocumentReference = database.collection("supervisor-user").document(deviceUserId)
        try {
            val document = docRef.get().await()
            if (document.exists()) {
                val userDevice = document.toObject(UserDevice::class.java)
                if (userDevice != null) {
                    val fullName = getDeviceFullName(userDevice.deviceId)
                    userDevice.fullName = fullName
                    callback(userDevice)
                } else {
                    Log.d("Firestore", "No such document")
                    callback(null)
                }
            } else {
                Log.d("Firestore", "Document does not exist")
                callback(null)
            }
        } catch (e: Exception) {
            Log.d("Firestore", "Error getting document", e)
            callback(null)
        }
    }

    override suspend fun getFallHistories(deviceId: String): List<FallHistoryItem> {
        val result = arrayListOf<FallHistoryItem>()
        try {
            val querySnapshot = database
                .collection("fallhistory")
                .whereEqualTo("deviceId", deviceId).get()
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

    override suspend fun getDeviceFullName(deviceId: String): String? {
        return try {
            // Thực hiện truy vấn lên Firestore và chờ kết quả
            val docRef = database.collection("device-user")
                .whereEqualTo("deviceId", deviceId)
                .get()
                .await()

            // Lấy document đầu tiên nếu có (vì deviceId là duy nhất)
            val documentSnapshot = docRef.documents.firstOrNull()

            // Kiểm tra nếu document tồn tại và trả về tên, nếu không thì trả về null
            if (documentSnapshot?.exists() == true) {
                documentSnapshot.getString("name")
            } else {
                null
            }
        } catch (e: Exception) {
            Log.d("Firestore", "Error getting document", e)
            null
        }
    }
}