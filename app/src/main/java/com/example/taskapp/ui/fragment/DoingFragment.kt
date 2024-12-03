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
import androidx.recyclerview.widget.RecyclerView
import com.example.taskapp.R
import com.example.taskapp.data.model.Status
import com.example.taskapp.data.model.Task
import com.example.taskapp.databinding.FragmentDoingBinding
import com.example.taskapp.ui.adapter.TaskAdapter
import com.example.taskapp.ui.viewModel.TaskViewModel
import com.example.taskapp.util.StateView
import com.example.taskapp.util.showBottomSheet


class DoingFragment : Fragment() {

    private var _binding: FragmentDoingBinding? = null

    private  val binding get() = _binding!!

    private val viewModel: TaskViewModel by activityViewModels()

    private  lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoingBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        viewModel.getTasks()
    }

    override fun onStart() {
        super.onStart()
        // observando mudanças do livedate
        observeViewModel()
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

    private fun observeViewModel() {
        viewModel.taskList.observe(viewLifecycleOwner) { stateView ->
            when(stateView) {
                is StateView.OnLoading -> {
                    binding.progressBar.isVisible = true
                }
                is StateView.OnSuccess -> {
                    val taskList = stateView.data?.filter { it.Status == Status.DOING } ?: emptyList()

                    binding.progressBar.isVisible = false
                    listEmpty(taskList)
                    taskAdapter.submitList(taskList)
                }
                is StateView.OnError -> {
                    binding.progressBar.isVisible = false
                    showBottomSheet(
                        message = getString(R.string.error_generic)
                    )
                }
            }
        }

        viewModel.taskInsert.observe(viewLifecycleOwner) { stateView ->
            when(stateView) {
                is StateView.OnLoading -> {
                    binding.progressBar.isVisible = true
                }
                is StateView.OnSuccess -> {
                    binding.progressBar.isVisible = false

                    if (stateView.data?.Status == Status.DOING) {
                        //Armazena a lista atual do adapter
                        val oldList = taskAdapter.currentList

                        //Gera uma nova lista a parti da lista antiga já com tarefa atualizada
                        val newList = oldList.toMutableList().apply {
                            add(0, stateView.data)
                        }

                        //Envia a lista atualizada para o adapter
                        taskAdapter.submitList(newList)

                        //Colocando na primeira posição
                        setPositionRecyclerView()

                        listEmpty(newList)
                    }
                }
                is StateView.OnError -> {
                    binding.progressBar.isVisible = false

                    showBottomSheet(
                        message = getString(R.string.error_generic)
                    )
                }
            }
        }

        viewModel.taskUpdate.observe(viewLifecycleOwner) { stateView ->
            when(stateView) {
                is StateView.OnLoading -> {
                    binding.progressBar.isVisible = true
                }
                is StateView.OnSuccess -> {
                    binding.progressBar.isVisible = false

                    //Armazena a lista atual do adapter
                    val oldList = taskAdapter.currentList

                    //Gera uma nova lista a parti da lista antiga já com tarefa atualizada
                    val newList = oldList.toMutableList().apply {
                        if (!oldList.contains(stateView.data) && stateView.data?.Status == Status.DOING) {
                            add(0, stateView.data)
                            setPositionRecyclerView()
                        }

                        if (stateView.data?.Status == Status.DOING) {
                            find { it.Id == stateView.data.Id }?.Description = stateView.data.Description ?: ""
                        } else {
                            remove(stateView.data)
                        }
                    }

                    //Armazena a oosição da tarefa a ser atualizada na lista
                    val position = newList.indexOfFirst { it.Id == stateView.data?.Id }

                    //Envia a lista atualizada para o adapter
                    taskAdapter.submitList(newList)

                    //Atualiza a tarefa pela posição do adapter
                    taskAdapter.notifyItemChanged(position)

                    listEmpty(newList)
                }
                is StateView.OnError -> {
                    binding.progressBar.isVisible = false
                    showBottomSheet(
                        message = getString(R.string.error_generic)
                    )
                }
            }
        }

        viewModel.taskRemove.observe(viewLifecycleOwner) { stateView ->
            when(stateView) {
                is StateView.OnLoading -> {
                    binding.progressBar.isVisible = true
                }
                is StateView.OnSuccess -> {
                    binding.progressBar.isVisible = false

                    //Atualizando lista
                    val oldList = taskAdapter.currentList
                    val newList = oldList.toMutableList().apply {
                        remove(stateView.data)
                    }
                    taskAdapter.submitList(newList)
                    listEmpty(newList)
                }
                is StateView.OnError -> {
                    binding.progressBar.isVisible = false
                    showBottomSheet(
                        message = getString(R.string.error_generic)
                    )
                }
            }

        }
    }

    private fun optionSelected(task: Task, option: Int){
        when(option){
            TaskAdapter.SELECT_BACK -> {
                task.Status = Status.TODO
                viewModel.updateTask(task)
            }
            TaskAdapter.SELECT_REMOVE -> {
                showBottomSheet(
                    titleDialog = R.string.text_title_dialog_delete,
                    message = getString(R.string.text_message_dialog_delete),
                    titleButton = R.string.text_button_dialog_confirm,
                    onClick = {
                        viewModel.deleteTask(task)
                        showBottomSheet(
                            message = getString(R.string.text_delete_success_task)
                        )
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
                task.Status = Status.DONE
                viewModel.updateTask(task)
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

    private fun setPositionRecyclerView() {
        taskAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                super.onItemRangeChanged(positionStart, itemCount)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                super.onItemRangeRemoved(positionStart, itemCount)
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)

                binding.rvTasks.scrollToPosition(0)
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                super.onItemRangeChanged(positionStart, itemCount, payload)
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}