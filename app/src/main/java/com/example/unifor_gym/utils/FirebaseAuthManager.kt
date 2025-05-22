package com.example.unifor_gym.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.example.unifor_gym.models.UserProfile
import com.example.unifor_gym.models.UserRole

class FirebaseAuthManager {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun isUserLoggedIn(): Boolean = getCurrentUser() != null

    fun loginUser(
        email: String,
        password: String,
        onSuccess: (UserProfile) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    getUserProfile(user.uid, onSuccess, onFailure)
                } else {
                    onFailure(Exception("Login failed"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun registerUser(
        name: String,
        email: String,
        password: String,
        onSuccess: (UserProfile) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    // Determine role based on email domain
                    val role = if (email.endsWith("@unifor.br")) {
                        UserRole.ADMIN
                    } else {
                        UserRole.USER
                    }

                    val userProfile = UserProfile(
                        uid = user.uid,
                        name = name,
                        email = email,
                        role = role
                    )

                    // Save user profile to Firestore
                    saveUserProfile(userProfile, onSuccess, onFailure)
                } else {
                    onFailure(Exception("Registration failed"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun resetPassword(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    private fun saveUserProfile(
        userProfile: UserProfile,
        onSuccess: (UserProfile) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("users")
            .document(userProfile.uid)
            .set(userProfile)
            .addOnSuccessListener {
                onSuccess(userProfile)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    private fun getUserProfile(
        uid: String,
        onSuccess: (UserProfile) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        firestore.collection("users")
            .document(uid)
            .get()
            .addOnSuccessListener { document ->
                val userProfile = document.toObject(UserProfile::class.java)
                if (userProfile != null) {
                    onSuccess(userProfile)
                } else {
                    onFailure(Exception("User profile not found"))
                }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun getCurrentUserProfile(
        onSuccess: (UserProfile) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val currentUser = getCurrentUser()
        if (currentUser != null) {
            getUserProfile(currentUser.uid, onSuccess, onFailure)
        } else {
            onFailure(Exception("No user logged in"))
        }
    }

    fun signOut() {
        auth.signOut()
    }
}