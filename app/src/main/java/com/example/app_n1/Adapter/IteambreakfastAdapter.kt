package com.example.app_n1.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.app_n1.databinding.BreakfastItemBinding // Import View Binding cho item_card

class IteambreakfastAdapter(private val items: List<Item>) : RecyclerView.Adapter<IteambreakfastAdapter.ItemViewHolder>() {

    class ItemViewHolder(private val binding: BreakfastItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Item) {
            binding.textViewItem.text = item.name
            binding.textViewDescription.text = item.description
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = BreakfastItemBinding.inflate(inflater, parent, false) // Sử dụng View Binding
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}

data class Item(val name: String, val description: String)
