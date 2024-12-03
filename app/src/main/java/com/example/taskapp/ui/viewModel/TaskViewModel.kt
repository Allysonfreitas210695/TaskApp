package com.example.taskapp.ui.viewModel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.taskapp.data.model.Task
import com.example.taskapp.util.FirebaseHelper
import com.example.taskapp.util.StateView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class TaskViewModel : ViewModel() {

    private val _taskList = MutableLiveData<StateView<List<Task>>>()
    val taskList: LiveData<StateView<List<Task>>> = _taskList

    private val _taskInsert = MutableLiveData<StateView<Task>>()
    val taskInsert: LiveData<StateView<Task>> = _taskInsert

    private val _taskUpdate = MutableLiveData<StateView<Task>>()
    val taskUpdate: LiveData<StateView<Task>> = _taskUpdate

    private val _taskRemove = MutableLiveData<StateView<Task>>()
    val taskRemove: LiveData<StateView<Task>> = _taskRemove

    fun getTasks() {
        _taskList.postValue(StateView.OnLoading())

        FirebaseHelper.getDatabase()
            .child("tasks")
            .child(FirebaseHelper.getIdUser())
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val taskList = snapshot.children.mapNotNull { it.getValue(Task::class.java) }
                        _taskList.postValue(StateView.OnSuccess(taskList.reversed()))
                    } catch (e: Exception) {
                        _taskList.postValue(StateView.OnError(e.message ?: "Error processing tasks"))
                        Log.e("TaskViewModel", "Error mapping tasks", e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    _taskList.postValue(StateView.OnError(error.message))
                    Log.e("TaskViewModel", "Database error: ${error.message}", error.toException())
                }
            })
    }

    fun insertTask(task: Task) {
        _taskInsert.postValue(StateView.OnLoading())

        FirebaseHelper.getDatabase()
            .child("tasks")
            .child(FirebaseHelper.getIdUser())
            .child(task.Id)
            .setValue(task)
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    _taskInsert.postValue(StateView.OnSuccess(task))
                } else {
                    _taskInsert.postValue(StateView.OnError(result.exception?.message ?: "Insert failed"))
                }
            }
            .addOnFailureListener { ex ->
                _taskInsert.postValue(StateView.OnError(ex.message ?: "Unknown error"))
                Log.e("TaskViewModel", "Insert task failed", ex)
            }
    }

    fun updateTask(task: Task) {
        _taskUpdate.postValue(StateView.OnLoading())

        val map = mapOf(
            "description" to task.Description,
            "status" to task.Status
        )

        FirebaseHelper.getDatabase()
            .child("tasks")
            .child(FirebaseHelper.getIdUser())
            .child(task.Id)
            .updateChildren(map)
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    _taskUpdate.postValue(StateView.OnSuccess(task))
                } else {
                    _taskUpdate.postValue(StateView.OnError(result.exception?.message ?: "Update failed"))
                }
            }
            .addOnFailureListener { ex ->
                _taskUpdate.postValue(StateView.OnError(ex.message ?: "Unknown error"))
                Log.e("TaskViewModel", "Update task failed", ex)
            }
    }

    fun deleteTask(task: Task) {
        _taskRemove.postValue(StateView.OnLoading())

        FirebaseHelper.getDatabase()
            .child("tasks")
            .child(FirebaseHelper.getIdUser())
            .child(task.Id)
            .removeValue()
            .addOnCompleteListener { result ->
                if (result.isSuccessful) {
                    _taskRemove.postValue(StateView.OnSuccess(task))
                } else {
                    _taskRemove.postValue(StateView.OnError(result.exception?.message ?: "Delete failed"))
                }
            }
            .addOnFailureListener { ex ->
                _taskRemove.postValue(StateView.OnError(ex.message ?: "Unknown error"))
                Log.e("TaskViewModel", "Delete task failed", ex)
            }
    }
}
