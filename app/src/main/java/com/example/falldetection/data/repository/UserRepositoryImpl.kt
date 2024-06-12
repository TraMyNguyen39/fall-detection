package com.example.falldetection.data.repository

import android.util.Log
import com.example.falldetection.R
import com.example.falldetection.data.ResponseResult
import com.example.falldetection.data.model.User
import com.example.falldetection.data.datasource.RemoteDataSource
import com.example.falldetection.utils.Utils
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class UserRepositoryImpl(
    private val remoteDataSource: RemoteDataSource.UserDataSource
) : Repository.UserRepository {

    // Các bước: Đăng nhập trên Firebase Authentication
    // -> Kiểm tra email xác thực chưa? Nếu chưa, gửi lại email
    // -> Lấy thông tin trên Firestore
    override suspend fun login(email: String, password: String): ResponseResult<User> {
        return try {
            val result = CompletableDeferred<ResponseResult<User>>()
            remoteDataSource.login(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (remoteDataSource.getCurrentAccount()?.isEmailVerified == true) {
                        CoroutineScope(Dispatchers.IO).launch {
                            val user = remoteDataSource.updateTokensAndGetAccount(email, Utils.token!!)
                            if (user != null) {
                                user.email = email
                                result.complete(ResponseResult(true, user, null))
                            } else {
                                logout()
                                result.complete(
                                    ResponseResult(
                                        false, null, R.string.txt_account_is_not_available
                                    )
                                )
                            }
                        }
                    } else {
                        remoteDataSource.sendVerifyEmail(email)
                        logout()
                        result.complete(
                            ResponseResult(
                                false, null, R.string.txt_check_your_email
                            )
                        )
                    }
                } else {
                    if (task.exception is FirebaseNetworkException) {
                        result.complete(ResponseResult(false, null, R.string.txt_no_internet))
                    } else {
                        result.complete(
                            ResponseResult(
                                false, null, R.string.txt_wrong_username_password
                            )
                        )
                    }
                }
            }
            result.await()
        } catch (e: Exception) {
            // Handle the exception here
            logout()
            ResponseResult(false, null, R.string.txt_unknown_error)
        }
    }

    override fun sendVerifyEmail(email: String): Task<Void>? {
        return remoteDataSource.sendVerifyEmail(email)
    }

    // Các bước: Đăng ký trên Firebase Authentication -> Lưu thông tin trên Firestore -> Gửi email
    override suspend fun signup(user: User): ResponseResult<Nothing> {
        return try {
            val result = CompletableDeferred<ResponseResult<Nothing>>()
            remoteDataSource.signup(user).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // delete password for security. Then insert to Firestore
                    user.password = null
                    remoteDataSource.addNewUserFireStore(user)
                        ?.addOnCompleteListener { addUserTask ->
                            if (addUserTask.isSuccessful) {
                                // after insert into firestore, send a verification email for user
                                remoteDataSource.getCurrentAccount()?.sendEmailVerification()
                                    ?.addOnCompleteListener { emailTask ->
                                        logout()
                                        if (emailTask.isSuccessful) {
                                            result.complete(
                                                ResponseResult(
                                                    true, null, null
                                                )
                                            )
                                        } else {
                                            result.complete(
                                                ResponseResult(
                                                    false, null, R.string.txt_cant_send_email
                                                )
                                            )
                                        }
                                    }
                            } else {
                                logout()
                                result.complete(
                                    ResponseResult(
                                        false, null, R.string.txt_signup_failed
                                    )
                                )
                            }
                        }
                } else {
                    logout()
                    when (task.exception) {
                        is FirebaseNetworkException -> {
                            result.complete(
                                ResponseResult(
                                    false, null, R.string.txt_no_internet
                                )
                            )
                        }

                        is FirebaseAuthUserCollisionException -> {
                            result.complete(
                                ResponseResult(
                                    false, null, R.string.txt_account_is_available
                                )
                            )
                        }

                        else -> {
                            result.complete(
                                ResponseResult(
                                    false, null, R.string.txt_signup_failed
                                )
                            )
                        }
                    }
                }
            }
            result.await()
        } catch (e: Exception) {
            logout()
            ResponseResult(false, null, R.string.txt_unknown_error)
        }
    }

    // cần hay không?
    override fun getCurrentAccount(): FirebaseUser? {
        return remoteDataSource.getCurrentAccount()
    }

    override fun logout() {
        remoteDataSource.logout()
    }

    override fun removeToken(email: String, token: String) {
        try {
            remoteDataSource.removeToken(email, Utils.token!!)
        } catch (e: Exception) {
            Log.e("removeToken", e.toString())
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): ResponseResult<Int> {
        val result = CompletableDeferred<ResponseResult<Int>>()
        remoteDataSource.sendPasswordResetEmail(email)?.addOnCompleteListener {
            if (it.isSuccessful) {
                result.complete(
                    ResponseResult(true, R.string.txt_check_your_email_forget_pass, null)
                )
            } else {
                if (it.exception is FirebaseNetworkException) {
                    ResponseResult(false, null, R.string.txt_no_internet)
                } else {
                    ResponseResult(false, null, R.string.txt_cant_send_email)
                }
            }
        }
        return result.await()
    }

    // cần hay không?
    override suspend fun getUserByEmail(email: String): User? {
        return remoteDataSource.getUserByEmail(email)
    }

    override suspend fun updateUserInfo(user: User): Boolean {
        return try {
            remoteDataSource.updateUserInfo(user)!!.await()
            true
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun registerBringDevice(user: User): Boolean {
        return try {
            val updateInfo = updateUserInfo(user)
            if (updateInfo) {
                remoteDataSource.registerBringDevice(user)!!.await()
                true
            } else {
                false
            }

        } catch (e: Exception) {
            false
        }
    }

    override suspend fun cancelBringDevice(userEmail: String): Boolean {
        return try {
            remoteDataSource.cancelBringDevice(userEmail)!!.await()
            true
        } catch (e: Exception) {
            false
        }
    }
//
//    // cần hay khoong
//    override suspend fun getUserById(uid: String, callback: (User?) -> Unit) {
//        return remoteDataSource.getUserById(uid, callback)
//    }

    // add user cần account?
//    override fun addNewUser(email: String): Task<Void>? {
//        return remoteDataSource.addNewUserFireStore(email)
//    }

}