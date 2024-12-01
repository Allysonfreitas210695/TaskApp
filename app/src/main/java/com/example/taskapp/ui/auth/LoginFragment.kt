package com.example.taskapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.taskapp.R
import com.example.taskapp.databinding.FragmentLoginBinding
import com.example.taskapp.ui.fragment.BaseFragment
import com.example.taskapp.util.FirebaseHelper
import com.example.taskapp.util.showBottomSheet

class LoginFragment : BaseFragment() {

    private var _binding: FragmentLoginBinding? = null

    private  val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initListeners()
    }

    private fun initListeners() {
        binding.btnLogin.setOnClickListener {
            binding.progressBar.isVisible = true
            validateData()
        }

        binding.btnRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnRecoverAccount.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_recoverAccountFragment)
        }
    }

    private fun validateData(){
        val email = binding.editEmail.text.toString().trim()
        val password = binding.editPassword.text.toString().trim()

        if(email.isNotEmpty()){
            if(password.isNotEmpty()){
                hideKeyboard() // escondedo teclado após fazer login
                binding.progressBar.isVisible = true
                loginUser(email, password)
            }else {
                binding.progressBar.isVisible = false
                showBottomSheet(message = getString(R.string.password_empty))
            }
        }else {
            binding.progressBar.isVisible = false
            showBottomSheet(message = getString(R.string.email_empty))
        }
    }

    private  fun loginUser(email: String, password: String) {

        if (!FirebaseHelper.isAuthenticate()) {
            FirebaseHelper.getAuth().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    binding.progressBar.isVisible = false
                    if (task.isSuccessful) {
                        findNavController().navigate(R.id.action_global_homeFragment)
                    } else {
                        showBottomSheet(message = task.exception?.message ?: "Erro no login")
                    }
                }
        }else {
            findNavController().navigate(R.id.action_global_homeFragment)
        }

    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}