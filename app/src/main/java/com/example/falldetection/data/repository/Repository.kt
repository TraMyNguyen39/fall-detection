package com.example.falldetection.data.repository

import com.example.falldetection.data.model.FallHistoryItem
import com.example.falldetection.data.model.User
import com.example.falldetection.data.model.UserDevice
import com.example.falldetection.data.ResponseResult
import com.example.falldetection.data.model.ObserverRequest
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference

interface Repository {
    interface UserRepository {
        suspend fun login(email: String, password: String) : ResponseResult<User>
        suspend fun signup(user: User) : ResponseResult<Nothing>
        fun sendVerifyEmail(email: String) : Task<Void>?
        fun getCurrentAccount() : FirebaseUser?
        fun logout()
        fun removeToken(email: String, token: String)
        suspend fun sendPasswordResetEmail(email: String) : ResponseResult<Int>

        suspend fun getUserByEmail(email: String) : User?
        suspend fun updateUserInfo(user: User) : Boolean
        suspend fun registerBringDevice(user: User) : Boolean
        suspend fun cancelBringDevice(userEmail: String) : Boolean

//        suspend fun getUserById(uid: String, callback: (User?) -> Unit)
    }

    interface UserDeviceRepository {
        suspend fun getAllPatientOfUser(userEmail: String) : List<UserDevice>
        suspend fun checkObserver(userEmail: String, patientEmail: String): Boolean
        suspend fun deleteObserver(userEmail: String, patientEmail: String) : Boolean
    }

    interface DeviceRepository {
        suspend fun getFallHistories(patientEmail: String) : List<FallHistoryItem>
    }

    interface ObserverRequestRepository {
        suspend fun sendNewRequest(userEmail: String, patientEmail: String, reminderName: String) : String?
        suspend fun loadAllRequest(userEmail: String) : List<ObserverRequest>
        suspend fun acceptRequest(observerRequest: ObserverRequest) : Boolean
        suspend fun denyRequest(observerRequest: ObserverRequest) : Boolean
    }
}