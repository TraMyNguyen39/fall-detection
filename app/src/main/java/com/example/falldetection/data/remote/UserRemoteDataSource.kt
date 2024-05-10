package com.example.falldetection.data.remote

import com.example.falldetection.data.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRemoteDataSource(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore
) : RemoteDataSource.UserDataSource {
    override fun login(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    override fun sendVerifyEmail(email: String): Task<Void>? {
        return getCurrentAccount()?.sendEmailVerification()
    }

    override fun signup(user: User): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(user.email, user.password!!)
    }

    override fun getCurrentAccount(): FirebaseUser? {
        return auth.currentUser
    }

    override fun logout() {
        auth.signOut()
    }

    override fun sendPasswordResetEmail(email: String): Task<Void> {
        return auth.sendPasswordResetEmail(email)
    }

    override suspend fun getUserByEmail(email: String): User? {
        return try {
            val document = database.collection("user").document(email).get().await()
            if (document.exists()) {
                document.toObject(User::class.java)
            } else {
                null // return null if no user found
            }
        } catch (e: Exception) {
            println("Error getting user: $e")
            null
        }
    }

//    override suspend fun getUserById(uid: String, callback: (User?) -> Unit) {
//        val docRef: DocumentReference = database.collection("supervisor").document(uid)
//        // Truy vấn dữ liệu người dùng
//        docRef.get().addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                val document = task.result
//                if (document.exists()) {
//                    val user = document.toObject(User::class.java)
//                    if (user != null) {
////                        user.uid = uid
//                        callback(user)
//                    } else {
//                        callback(null)
//                        Log.d("Firestore", "No such document")
//                    }
//                } else {
//                    Log.d("Firestore", "get failed with ", task.exception)
//                    callback(null)
//                }
//            }
//        }
//    }

    override fun addNewUserFireStore(user: User): Task<Void>? {
        return try {
            val docRef = database.collection("user").document(user.email)
            docRef.set(user)
        } catch (e: Exception) {
            println("Error getting user: $e")
            null
        }
    }
}