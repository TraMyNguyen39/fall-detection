package com.example.falldetection

import android.app.Application
import com.example.falldetection.data.repository.UserRepositoryImpl
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MyApplication : Application() {
    private val mAuth by lazy { Firebase.auth }
    private val mDatabase by lazy { Firebase.firestore }
    val userRepository by lazy { UserRepositoryImpl(mAuth, mDatabase) }
}
