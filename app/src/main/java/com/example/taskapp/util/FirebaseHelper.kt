package com.example.taskapp.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class FirebaseHelper {
    companion object {
        fun getDatabase() = Firebase.database.reference

        fun getAuth() = FirebaseAuth.getInstance()

        fun getIdUser() = getAuth().currentUser?.uid ?: ""

        fun isAuthenticate() = getAuth().currentUser != null
    }
}