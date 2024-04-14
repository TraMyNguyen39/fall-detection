package com.example.falldetection.data.repository

import com.example.falldetection.data.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference

interface UserRepository {
    fun login(email: String, password: String) : Task<AuthResult>
    fun sendVerifyEmail(email: String) : Task<Void>?
    fun signup(email: String, password: String) : Task<AuthResult>
    fun getCurrentAccount() : FirebaseUser?
    fun logout()
    fun sendPasswordResetEmail(email: String) : Task<Void>?

    suspend fun getUserByEmail(email: String) : User?

    fun addNewUser(email: String) : Task<DocumentReference>
}