package com.example.taskapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskapp.R
import com.example.taskapp.data.model.Status
import com.example.taskapp.data.model.Task
import com.example.taskapp.databinding.FragmentTodoBinding
import com.example.taskapp.ui.TaskViewModel
import com.example.taskapp.ui.adapter.TaskAdapter
import com.example.taskapp.util.FirebaseHelper
import com.example.taskapp.util.showBottomSheet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener


class TodoFragment : Fragment() {
    private var _binding: FragmentTodoBinding? = null

    private  val binding get() = _binding!!

    private  lateinit var taskAdapter: TaskAdapter

    private val viewModel: TaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTodoBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initListeners()

        initRecyclerView()

        getTasks()
    }

    private fun initRecyclerView() {
        taskAdapter = TaskAdapter(requireContext()){ task, option ->
            optionSelected(task, option)
        }

        with(binding.rvTasks){
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = taskAdapter
        }
    }

    private fun initListeners() {
        binding.fabAdd.setOnClickListener {
            val action = HomeFragmentDirections
                .actionHomeFragmentToFormTaskFragment(null)

            findNavController().navigate(action)
        }

        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.taskUpdate.observe(viewLifecycleOwner){ updateTask ->
            if(updateTask.Status == Status.TODO){
                //Armazena a lista atual do adapter
                val oldList = taskAdapter.currentList

                //Gera uma nova lista a parti da lista antiga já com tarefa atualizada
                val newList = oldList.toMutableList().apply {
                    find { it.Id == updateTask.Id }?.Description = updateTask.Description
                }

                //Armazena a oosição da tarefa a ser atualizada na lista
                val position = newList.indexOfFirst { it.Id == updateTask.Id }

                //Envia a lista atualizada para o adapter
                taskAdapter.submitList(newList)

                //Atualiza a tarefa pela posição do adapter
                taskAdapter.notifyItemChanged(position)
            }
        }
    }

    private fun optionSelected(task: Task, option: Int){
        when(option){
            TaskAdapter.SELECT_BACK -> {
                Toast.makeText(requireContext(), "Voltando ${task.Description}", Toast.LENGTH_SHORT).show()
            }
            TaskAdapter.SELECT_REMOVE -> {
                showBottomSheet(
                    titleDialog = R.string.text_title_dialog_delete,
                    message = getString(R.string.text_message_dialog_delete),
                    titleButton = R.string.text_button_dialog_confirm,
                    onClick = {
                        deleteTask(task)
                    }
                )
            }
            TaskAdapter.SELECT_EDIT -> {
                val action = HomeFragmentDirections
                    .actionHomeFragmentToFormTaskFragment(task)

                findNavController().navigate(action)
            }
            TaskAdapter.SELECT_DETAILS -> {
                Toast.makeText(requireContext(), "Detalhes ${task.Description}", Toast.LENGTH_SHORT).show()
            }
            TaskAdapter.SELECT_NEXT -> {
                task.Status = Status.DOING
                updateTask(task)
            }
        }
    }

    private fun getTasks() {
        FirebaseHelper.getDatabase()
            .child("tasks")
            .child(FirebaseHelper.getIdUser())
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val taskList = mutableListOf<Task>()
                    for (ds in snapshot.children){
                        val task = ds.getValue(Task::class.java) as Task
                        if(task.Status == Status.TODO) {
                            taskList.add(task)
                        }
                    }

                    binding.progressBar.isVisible = false
                    listEmpty(taskList)

                    taskList.reverse()

                    taskAdapter.submitList(taskList)
                }

                override fun onCancelled(error: DatabaseError) {
                    if(FirebaseHelper.isAuthenticate()) {
                        showBottomSheet(
                            message = getString(R.string.error_generic)
                        )
                    }
                }

            })
    }

    private  fun deleteTask(task: Task){
        FirebaseHelper.getDatabase()
            .child("tasks")
            .child(FirebaseHelper.getIdUser())
            .child(task.Id)
            .removeValue().addOnCompleteListener { result ->
                if(result.isSuccessful){
                    showBottomSheet(
                        message = getString(R.string.text_delete_success_task)
                    )
                }else {
                    showBottomSheet(
                        message = getString(R.string.error_generic)
                    )
                }
            }
    }

    private fun updateTask(task: Task) {
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
                }else {
                    binding.progressBar.isVisible = false
                    showBottomSheet(
                        message = getString(R.string.error_generic)
                    )
                }
            }
    }

    private fun listEmpty(taskList: List<Task>){
        binding.textInfo.text = if(taskList.isEmpty()) {
            getString(R.string.text_list_task_empty)
        }else {
                ""
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}