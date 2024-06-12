package com.example.falldetection.data.datasource.remote

import com.example.falldetection.data.ResponseResult
import com.example.falldetection.data.model.ObserverRequest
import com.google.firebase.firestore.FirebaseFirestore
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