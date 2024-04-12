package com.example.falldetection.data.repository

import com.example.falldetection.data.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

interface UserRepository {
    fun login(email: String, password: String) : Task<AuthResult>

    fun sendVerifyEmail(email: String) : Task<Void>?
    fun signup(email: String, password: String) : Task<AuthResult>
    fun getCurrentAccount() : FirebaseUser?
    fun logout()

    suspend fun getUserByEmail(email: String) : User?
}