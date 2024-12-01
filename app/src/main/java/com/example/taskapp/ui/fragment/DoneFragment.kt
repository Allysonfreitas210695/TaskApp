package com.example.taskapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskapp.R
import com.example.taskapp.data.model.Status
import com.example.taskapp.data.model.Task
import com.example.taskapp.databinding.FragmentDoneBinding
import com.example.taskapp.databinding.FragmentHomeBinding
import com.example.taskapp.ui.adapter.TaskAdapter


class DoneFragment : Fragment() {

    private var _binding: FragmentDoneBinding? = null
    private  val binding get() = _binding!!

    private  lateinit var taskAdapter: TaskAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDoneBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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

    private fun getTasks() {
        val taskList = listOf<Task>(
            Task("0", "Criar nova tarefa do app", Status.DONE),
        )
        taskAdapter.submitList(taskList)
    }

    private fun optionSelected(task: Task, option: Int){
        when(option){
            TaskAdapter.SELECT_BACK -> {
                Toast.makeText(requireContext(), "Voltando ${task.Description}", Toast.LENGTH_SHORT).show()
            }
            TaskAdapter.SELECT_REMOVE -> {
                Toast.makeText(requireContext(), "Removendo ${task.Description}", Toast.LENGTH_SHORT).show()
            }
            TaskAdapter.SELECT_EDIT -> {
                Toast.makeText(requireContext(), "Editando ${task.Description}", Toast.LENGTH_SHORT).show()
            }
            TaskAdapter.SELECT_DETAILS -> {
                Toast.makeText(requireContext(), "Detalhes ${task.Description}", Toast.LENGTH_SHORT).show()
            }
            TaskAdapter.SELECT_NEXT -> {
                Toast.makeText(requireContext(), "Próximo ${task.Description}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}