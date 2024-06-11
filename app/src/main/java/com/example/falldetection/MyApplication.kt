package com.example.falldetection

import android.app.Application
import com.example.falldetection.data.datasource.remote.UserDeviceRemoteDataSource
import com.example.falldetection.data.remote.UserRemoteDataSource
import com.example.falldetection.data.repository.UserDevicesRepositoryImpl
import com.example.falldetection.data.repository.UserRepositoryImpl
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyApplication : Application() {
    private val mAuth by lazy { Firebase.auth }
    private val mDatabase by lazy { Firebase.firestore }

    private val mUserRemoteDataSource by lazy { UserRemoteDataSource(mAuth, mDatabase) }
    private val mUserDeviceRemoteDataSource by lazy {
        UserDeviceRemoteDataSource(
            mDatabase,
            mUserRemoteDataSource
        )
    }
    val userRepository by lazy { UserRepositoryImpl(mUserRemoteDataSource) }
    val userDeviceRepository by lazy { UserDevicesRepositoryImpl(mUserDeviceRemoteDataSource) }
}
