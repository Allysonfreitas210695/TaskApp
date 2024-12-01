package com.example.taskapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskapp.R
import com.example.taskapp.data.model.Status
import com.example.taskapp.data.model.Task
import com.example.taskapp.databinding.FragmentTodoBinding
import com.example.taskapp.ui.adapter.TaskAdapter


class TodoFragment : Fragment() {
    private var _binding: FragmentTodoBinding? = null
    private  val binding get() = _binding!!

    private  lateinit var taskAdapter: TaskAdapter

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
            findNavController().navigate(R.id.action_homeFragment_to_formTaskFragment)
        }
    }

    private fun getTasks() {
        val taskList = listOf<Task>(
            Task("0", "Criar nova tarefa do app", Status.TODO),
            Task("1", "Validar informações na tela de login", Status.TODO),
            Task("2", "Adicionar nova funcionalidade no app", Status.TODO),
            Task("3", "Salvar token no localmente", Status.TODO),
            Task("4", "Criar funcionalidade de logout no app", Status.TODO),
            Task("5", "Criar funcionalidade de logout no app", Status.TODO),
            Task("6", "Salvar token no localmente", Status.TODO)
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