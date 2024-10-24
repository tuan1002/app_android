package com.example.app_n1.bottomSheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import com.example.app_n1.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AgeBottomSheet : BottomSheetDialogFragment() {
    private var currentValue = 20

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.activity_age_bottom_sheet, container, false)

        val numberEditText = view.findViewById<EditText>(R.id.numberDisplayAge)
        numberEditText.setText(currentValue.toString()) // Hiển thị giá trị ban đầu

        view.findViewById<ImageView>(R.id.decreaseButtonAge).setOnClickListener {
            if (currentValue > 10) { // Kiểm tra không cho phép nhỏ hơn 10
                currentValue--
                numberEditText.setText(currentValue.toString())
            }
        }

        view.findViewById<ImageView>(R.id.increaseButtonAge).setOnClickListener {
            if (currentValue < 100) {
                currentValue++
                numberEditText.setText(currentValue.toString())
            }
        }

        view.findViewById<Button>(R.id.closeButtonAge).setOnClickListener {
            dismiss()
        }

        return view
    }
}
