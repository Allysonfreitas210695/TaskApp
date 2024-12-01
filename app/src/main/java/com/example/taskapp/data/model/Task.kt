package com.example.taskapp.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    val Id: String,
    val Description: String,
    val Status: Status = com.example.taskapp.data.model.Status.TODO
) : Parcelable
