package com.example.falldetection.data.datasource.remote

import com.example.falldetection.data.model.FallHistoryItem
import com.example.falldetection.data.model.ObserverRequest
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
        suspend fun updateTokensAndGetAccount(email: String, token: String) : User?
        fun removeToken(email: String, token: String)
        fun getCurrentAccount() : FirebaseUser?
        fun logout()
        fun sendPasswordResetEmail(email: String) : Task<Void>?
        suspend fun getUserByEmail(email: String) : User?
        fun addNewUserFireStore(user: User) : Task<Void>?
        fun updateUserInfo(user: User) : Task<Void>?
        fun updateAvt(email: String, fileName: String) : Task<Void>?
        fun registerBringDevice(user: User) : Task<Void>?
        fun cancelBringDevice(userEmail: String) : Task<Void>?
    }

    interface UserDeviceDataSource {
        suspend fun getAllPatientOfUser(userEmail: String) : List<UserDevice>
        suspend fun checkObserver(userEmail: String, patientEmail: String): Boolean
        suspend fun deleteObserver(userEmail: String, patientEmail: String) : Boolean
    }

    interface DeviceDataSource {
        suspend fun getFallHistories(patientEmail: String) : List<FallHistoryItem>
    }

    interface ObserverRequestDataSource {
        suspend fun sendNewRequest(request: ObserverRequest) : String?
        suspend fun loadAllRequest(userEmail: String) : List<ObserverRequest>
        suspend fun acceptRequest(observerRequest: ObserverRequest) : Boolean
        suspend fun denyRequest(observerRequest: ObserverRequest) : Boolean
    }
}