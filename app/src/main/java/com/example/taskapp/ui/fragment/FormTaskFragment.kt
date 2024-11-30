package com.example.taskapp.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.taskapp.R
import com.example.taskapp.databinding.FragmentFormTaskBinding
import com.example.taskapp.databinding.FragmentHomeBinding
import com.example.taskapp.util.initToolBar
import com.example.taskapp.util.showBottomSheet


class FormTaskFragment : Fragment() {
    private var _binding: FragmentFormTaskBinding? = null
    private  val binding get() = _binding!!

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

        initListerens()
        initToolBar(binding.toolbar)
    }

    private  fun initListerens() {
        binding.btnSave.setOnClickListener{
            validateData()
        }
    }

    private fun validateData(){
        val description = binding.editDescription.text.toString().trim()

        if(description.isNotEmpty()){
            Toast.makeText(requireContext(), "Tudo Certo!!!", Toast.LENGTH_SHORT).show()
        }else {
            showBottomSheet(message = R.string.description_empty_from_task_fragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}