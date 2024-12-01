package com.example.taskapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.taskapp.data.model.Status
import com.example.taskapp.data.model.Task
import com.example.taskapp.databinding.FragmentDoingBinding
import com.example.taskapp.ui.adapter.TaskAdapter


class DoingFragment : Fragment() {

    private var _binding: FragmentDoingBinding? = null
    private  val binding get() = _binding!!

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

        initRecyclerView(getTasks())
    }

    private fun initRecyclerView(taskList: List<Task>) {
        taskAdapter = TaskAdapter(requireContext(), taskList){ task, option ->
            optionSelected(task, option)
        }
        binding.rvTasks.layoutManager = LinearLayoutManager(requireContext())
        binding.rvTasks.setHasFixedSize(true)

        binding.rvTasks.adapter = taskAdapter
    }

    private fun getTasks() = listOf<Task>(
        Task("0", "Criar nova tarefa do app", Status.DOING),
        Task("1", "Validar informações na tela de login", Status.DOING),
        Task("2", "Adicionar nova funcionalidade no app", Status.DOING)
    )

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