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

class KcalBottomSheet : BottomSheetDialogFragment() {

    private var currentValue = 500 // Giá trị hiện tại

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate layout cho KcalBottomSheet
        val view = inflater.inflate(R.layout.activity_kcal_botton_sheet, container, false)

        val numberEditText = view.findViewById<EditText>(R.id.numberDisplay)

        view.findViewById<ImageView>(R.id.decreaseButton).setOnClickListener {
            if (currentValue > 500) {
                currentValue--
                numberEditText.setText(currentValue.toString())
            }
        }

        view.findViewById<ImageView>(R.id.increaseButton).setOnClickListener {
            currentValue++
            numberEditText.setText(currentValue.toString())
        }

        view.findViewById<Button>(R.id.closeButton).setOnClickListener {
            dismiss() // Đóng Bottom Sheet khi nhấn nút
        }

        return inflater.inflate(R.layout.activity_kcal_botton_sheet, container, false)
    }
}
