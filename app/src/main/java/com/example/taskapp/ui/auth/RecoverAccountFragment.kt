package com.example.taskapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.example.taskapp.R
import com.example.taskapp.databinding.FragmentRecoverAccountBinding
import com.example.taskapp.ui.fragment.BaseFragment
import com.example.taskapp.util.FirebaseHelper
import com.example.taskapp.util.initToolBar
import com.example.taskapp.util.showBottomSheet

class RecoverAccountFragment : BaseFragment() {
    private var _binding: FragmentRecoverAccountBinding? = null

    private  val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecoverAccountBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initToolBar(binding.toolbar)
        initListerens()
    }

    private  fun initListerens() {
        binding.btnRecover.setOnClickListener{
            validateData()
        }
    }

    private fun validateData(){
        val email = binding.editEmail.text.toString().trim()

        if(email.isNotEmpty()){
            hideKeyboard()
            binding.progressBar.isVisible = true
            recoverAccountUser(email)
        }else {
            showBottomSheet(message = getString(R.string.email_empty))
        }
    }

    private fun recoverAccountUser(email: String) {
        FirebaseHelper.getAuth().sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                binding.progressBar.isVisible = false

                if(task.isSuccessful){
                    showBottomSheet(
                        message = getString(R.string.text_message_recover_account_fragment)
                    )
                }else {
                    showBottomSheet(
                        message = task.exception?.message ?: "Erro ao enviar e-mail de recuperação!"
                    )
                }

            }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}