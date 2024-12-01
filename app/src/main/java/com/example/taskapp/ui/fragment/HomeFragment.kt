package com.example.taskapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.taskapp.R
import com.example.taskapp.databinding.FragmentHomeBinding
import com.example.taskapp.ui.adapter.ViewPagerAdapter
import com.example.taskapp.util.FirebaseHelper
import com.example.taskapp.util.showBottomSheet
import com.google.android.material.tabs.TabLayoutMediator

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private  val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initTabs()
        initListners()
    }

    private fun initListners() {
        binding.btnLogout.setOnClickListener{
            showBottomSheet(
                titleDialog = R.string.text_title_dialog_confirm_logout,
                titleButton = R.string.text_button_dialog_confirm,
                message = getString(R.string.text_message_dialog_confirm_logout),
                onClick = {
                    FirebaseHelper.getAuth().signOut()
                    findNavController().navigate(R.id.action_homeFragment_to_authenticate)
                }
            )
        }
    }

    private fun initTabs() {
        val pageAdapter = ViewPagerAdapter(requireActivity())
        binding.viewPager.adapter = pageAdapter

        pageAdapter.addFrament(TodoFragment(), R.string.status_task_todo )
        pageAdapter.addFrament(DoingFragment(),R.string.status_task_doing)
        pageAdapter.addFrament(DoneFragment(),R.string.status_task_done)

        binding.viewPager.offscreenPageLimit = pageAdapter.itemCount

        TabLayoutMediator(binding.tabs, binding.viewPager){ tab, position ->
            tab.text = getString(pageAdapter.getTitle(position))
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}