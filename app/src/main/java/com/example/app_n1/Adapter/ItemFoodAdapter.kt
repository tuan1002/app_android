package com.example.app_n1.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.app_n1.databinding.ItemFoodBinding
import com.example.app_n1.models.Food

class itemFoodAdapter(private val foodList: MutableList<Food>,
                      var onItemClickListener: ((Food) -> Unit)? = null
    ) : RecyclerView.Adapter<itemFoodAdapter.FoodViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val foodItem = foodList[position]
        holder.bind(foodItem)
    }

    override fun getItemCount(): Int = foodList.size

    inner class FoodViewHolder(private val binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(foodItem: Food) {
            binding.foodName.text = foodItem.name
            binding.foodCalories.text = "Calories: ${foodItem.calories}"
            binding.foodProtein.text = "Protein: ${foodItem.protein}g"
            binding.foodFat.text = "Fat: ${foodItem.fat}g"
            binding.foodCarbs.text = "Carbs: ${foodItem.carbs}g"

            binding.root.setOnClickListener {
                onItemClickListener?.invoke(foodItem)
            }
        }
    }
}
