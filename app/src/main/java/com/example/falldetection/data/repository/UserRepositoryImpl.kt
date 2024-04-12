package com.example.falldetection.data.repository

import com.example.falldetection.data.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

class UserRepositoryImpl(
    private val auth: FirebaseAuth,
    private val database: FirebaseFirestore
) : UserRepository {
    override fun login(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    override fun sendVerifyEmail(email: String): Task<Void>? {
        return getCurrentAccount()?.sendEmailVerification()
    }

    override fun signup(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }

    override fun getCurrentAccount(): FirebaseUser? {
        return auth.currentUser
    }

    override fun logout() {
        auth.signOut()
    }
    override suspend fun getUserByEmail(email: String): User? {
        return try {
            val querySnapshot = database.collection("supervisor")
                .whereEqualTo("email", email)
                .get()
                .await() // assuming you have imported kotlinx.coroutines.tasks.await

            for (document in querySnapshot.documents) {
                val user = document.toObject(User::class.java)
                if (user != null) {
                    return user
                }
            }
            null // return null if no user found
        } catch (e: Exception) {
            println("Error getting user: $e")
            null
        }
    }

}