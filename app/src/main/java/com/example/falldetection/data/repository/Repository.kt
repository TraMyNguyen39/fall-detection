package com.example.falldetection.data.repository

import com.example.falldetection.data.model.User
import com.example.falldetection.data.model.UserDevice
import com.example.falldetection.data.remote.ResponseResult
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

interface Repository {
    interface UserRepository {
        suspend fun login(email: String, password: String) : ResponseResult<User>
        suspend fun signup(user: User) : ResponseResult<Nothing>
        fun sendVerifyEmail(email: String) : Task<Void>?
        fun getCurrentAccount() : FirebaseUser?
        fun logout()
        suspend fun sendPasswordResetEmail(email: String) : ResponseResult<Int>

        suspend fun getUserByEmail(email: String) : User?
//        suspend fun getUserById(uid: String, callback: (User?) -> Unit)
    }

    interface UserDeviceRepository {
        suspend fun getAllPatientOfUser(userEmail: String) : List<UserDevice>
    }
}