package com.example.app_n1.ui.aboutme

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.app_n1.BmrUpdate
import com.example.app_n1.databinding.FragmentAboutmeBinding

class AboutmeFragment : Fragment() {

    private var _binding: FragmentAboutmeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        _binding = FragmentAboutmeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Đặt sự kiện click cho một view, ví dụ một button
        binding.bmrCard.setOnClickListener {
            updateBmrLayout() // Gọi hàm khi click vào CardView 1
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun updateBmrLayout() {
        val intent = Intent(requireActivity(), BmrUpdate::class.java) // Sử dụng requireActivity() để lấy context
        startActivity(intent)
    }
}
