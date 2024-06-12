package com.example.falldetection.data.datasource.remote

import com.example.falldetection.data.model.User
import com.example.falldetection.data.remote.RemoteDataSource
import com.example.falldetection.utils.Role
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
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

    @Suppress("unchecked_cast")
    override suspend fun updateTokensAndGetAccount(email: String, token: String): User? {
        return try {
            val userDoc = database.collection("user").document(email)
            val document = userDoc.get().await()

            if (document.exists()) {
                val currentTokens = document.get("tokens") as? List<String> ?: listOf()
                val updatedTokens = currentTokens + token
                userDoc.set(mapOf("tokens" to updatedTokens), SetOptions.merge()).await()
                // trả về user
                document.toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            println("Error updating user tokens: $e")
            null
        }
    }


    @Suppress("unchecked_cast")
    override fun removeToken(email: String, token: String) {
        val userDoc = database.collection("user").document(email)
        userDoc.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val currentTokens = document.get("tokens") as? List<String> ?: listOf()
                val updatedTokens = currentTokens - token
                userDoc.set(mapOf("tokens" to updatedTokens), SetOptions.merge())
            }
        }
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

    override fun addNewUserFireStore(user: User): Task<Void>? {
        return try {
            val docRef = database.collection("user").document(user.email)
            docRef.set(user)
        } catch (e: Exception) {
            println("Error getting user: $e")
            null
        }
    }

    override fun updateUserInfo(user: User): Task<Void>? {
        return try {
            val docRef = database.collection("user").document(user.email)
            docRef.set(user)
        } catch (e: Exception) {
            println("Error getting user: $e")
            null
        }
    }

    override fun registerBringDevice(user: User): Task<Void>? {
        return try {
            val docRef = database.collection("register-patient").document(user.email)
            docRef.set(user)
        } catch (e: Exception) {
            println("Error getting user: $e")
            null
        }
    }

    override fun cancelBringDevice(userEmail: String): Task<Void>? {
        return try {
            val docRef = database.collection("user").document(userEmail)
            docRef.update("role", Role.SUPERVISOR.ordinal)
        } catch (e: Exception) {
            println("Error getting user: $e")
            null
        }
    }
}