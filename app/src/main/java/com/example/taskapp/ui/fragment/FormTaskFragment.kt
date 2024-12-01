package com.example.taskapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.taskapp.R
import com.example.taskapp.data.model.Status
import com.example.taskapp.data.model.Task
import com.example.taskapp.databinding.FragmentFormTaskBinding
import com.example.taskapp.ui.TaskViewModel
import com.example.taskapp.util.FirebaseHelper
import com.example.taskapp.util.initToolBar
import com.example.taskapp.util.showBottomSheet


class FormTaskFragment : BaseFragment() {
    private var _binding: FragmentFormTaskBinding? = null

    private  val binding get() = _binding!!

    private lateinit var task: Task

    private var status: Status = Status.TODO

    private var newTask: Boolean = true

    private val args: FormTaskFragmentArgs by navArgs()

    private val viewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFormTaskBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolBar(binding.toolbar)
        initListerens()
        getArgs()
    }

    private fun getArgs() {
        args.task.let { it ->
            if(it != null){
                this.task = it
                configTask()
            }
        }
    }

    private  fun initListerens() {
        binding.btnSave.setOnClickListener{
            validateData()
        }

        binding.rgStatus.setOnCheckedChangeListener { _, id ->
            status = when (id) {
                R.id.rbTodo -> Status.TODO
                R.id.rbDoing -> Status.DOING
                else -> Status.DONE
            }
        }
    }

    private  fun configTask(){
        newTask = false
        status = task.Status
        binding.textToolbar.text = task.Description

        binding.editDescription.setText(task.Description)
        setStatus()
    }

    private fun setStatus() {
        binding.rgStatus.check(when(task.Status){
            Status.TODO -> R.id.rbTodo
            Status.DOING -> R.id.rbDoing
            else -> R.id.rbDone
        })
    }

    private fun validateData(){
        val description = binding.editDescription.text.toString().trim()

        if(description.isNotEmpty()){
            hideKeyboard()

            binding.progressBar.isVisible = true

            if(newTask) task = Task()

            task.Description = description
            task.Status = status

            saveTask()
        }else {
            showBottomSheet(message = getString(R.string.description_empty_from_task_fragment))
        }
    }

    private fun saveTask() {
        FirebaseHelper.getDatabase()
            .child("tasks")
            .child(FirebaseHelper.getIdUser())
            .child(task.Id)
            .setValue(task)
            .addOnCompleteListener { result ->
                if(result.isSuccessful){
                    showBottomSheet(
                        message = getString(R.string.text_save_success_from_task_fragment)
                    )

                    if(newTask){
                        findNavController().popBackStack()
                    }else {
                        //Atualizado o LiveDate
                        viewModel.setUpdateTask(task)
                        binding.progressBar.isVisible = false
                    }
                }else {
                    binding.progressBar.isVisible = false
                    showBottomSheet(
                        message = getString(R.string.error_generic)
                    )
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}