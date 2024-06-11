package com.example.falldetection.data.remote

import com.example.falldetection.data.model.User
import com.example.falldetection.data.model.UserDevice
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

interface RemoteDataSource {
    interface UserDataSource {
        fun login(email: String, password: String) : Task<AuthResult>
        fun sendVerifyEmail(email: String) : Task<Void>?
        fun signup(user: User) : Task<AuthResult>
        fun getCurrentAccount() : FirebaseUser?
        fun logout()
        fun sendPasswordResetEmail(email: String) : Task<Void>?

        suspend fun getUserByEmail(email: String) : User?
//        suspend fun getUserById(uid: String, callback: (User?) -> Unit)

        fun addNewUserFireStore(user: User) : Task<Void>?
    }


    interface UserDeviceDataSource {
        suspend fun getAllPatientOfUser(userEmail: String) : List<UserDevice>
    }
}