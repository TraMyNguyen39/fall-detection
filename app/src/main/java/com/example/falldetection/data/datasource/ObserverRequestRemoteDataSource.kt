package com.example.falldetection.data.datasource.remote

import android.util.Log
import com.example.falldetection.data.ResponseResult
import com.example.falldetection.data.datasource.RemoteDataSource
import com.example.falldetection.data.model.ObserverRequest
import com.example.falldetection.data.model.UserDevice
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

class ObserverRequestRemoteDataSource(
    private val database: FirebaseFirestore
) : RemoteDataSource.ObserverRequestDataSource {
    override suspend fun sendNewRequest(request: ObserverRequest): String? {
        val baseUrl = "https://addobserverrequest-72zfhbsola-uc.a.run.app"
        val retrofit = createRetrofitService(baseUrl).create(ObserverRequestService::class.java)
        val result = retrofit.sendObserverRequest(request)
        return if (result.isSuccessful) {
            if (!result.body()!!.success) {
                result.body()!!.error.toString()
            } else {
                null
            }
        } else {
            result.message()
        }
    }

    override suspend fun loadAllRequest(userEmail: String): List<ObserverRequest> {
        val result = arrayListOf<ObserverRequest>()
        try {
            val querySnapshot = database
                .collection("observer-request")
                .whereEqualTo("patientEmail", userEmail)
                .get()
                .await()

            for (document in querySnapshot.documents) {
                val request = document.toObject(ObserverRequest::class.java)
                request?.let {
                    result.add(it)
                }
            }
        } catch (e: Exception) {
            Log.e("Request", e.toString())
        }
        return result
    }

    override suspend fun acceptRequest(observerRequest: ObserverRequest): Boolean {
        return try {
            val userDevice = UserDevice(
                observerRequest.patientEmail,
                observerRequest.supervisorEmail,
                observerRequest.reminderName,
                null,
                null,
                null
            )
            database
                .collection("user-device")
                .document("${observerRequest.supervisorEmail}_${observerRequest.patientEmail}")
                .set(userDevice)
                .await()

            database
                .collection("observer-request")
                .document("${observerRequest.supervisorEmail}_${observerRequest.patientEmail}")
                .delete()
                .await()

            true
        } catch (e: Exception) {
            println("Error getting user: $e")
            false
        }
    }

    override suspend fun denyRequest(observerRequest: ObserverRequest): Boolean {
        return try {
            database
                .collection("observer-request")
                .document("${observerRequest.supervisorEmail}_${observerRequest.patientEmail}")
                .delete()
                .await()
            true
        } catch (e: Exception) {
            println("Error getting user: $e")
            false
        }
    }

    private fun createRetrofitService(baseUrl: String): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

interface ObserverRequestService {
    @POST("/")
    suspend fun sendObserverRequest(@Body observerRequest: ObserverRequest) : Response<ResponseResult<Nothing>>
}