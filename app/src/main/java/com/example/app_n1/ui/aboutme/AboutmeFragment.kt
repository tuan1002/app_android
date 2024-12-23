package com.example.app_n1.ui.aboutme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.app_n1.BmrUpdate
import com.example.app_n1.CaloChart
import com.example.app_n1.dao.getInfor
import com.example.app_n1.dao.logoutDao
import com.example.app_n1.databinding.FragmentAboutmeBinding


class AboutmeFragment : Fragment() {

    private var _binding: FragmentAboutmeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAboutmeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.bmrCard.setOnClickListener {
            updateBmrLayout()
        }

        binding.caloChart.setOnClickListener{
            CaloChart()
        }

        binding.logoutCard.setOnClickListener {
            logoutDao(requireContext())
        }

        showInfor()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateBmrLayout() {
        val intent = Intent(requireActivity(), BmrUpdate::class.java)
        startActivity(intent)
    }
    private fun CaloChart() {
        val intent = Intent(requireActivity(), CaloChart::class.java)
        startActivity(intent)
    }
    private fun showInfor() {
        val sharedPreferences = context?.getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPreferences?.getString("userId", null).toString()

        getInfor(userId, binding)
    }
}
