package com.example.taskapp.data.model

import android.os.Parcelable
import com.example.taskapp.util.FirebaseHelper
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    var Id: String = "",
    var Description: String = "",
    var Status: Status = com.example.taskapp.data.model.Status.TODO
) : Parcelable {
    init {
        Id =  FirebaseHelper.getDatabase().push().key ?: ""
    }
}
