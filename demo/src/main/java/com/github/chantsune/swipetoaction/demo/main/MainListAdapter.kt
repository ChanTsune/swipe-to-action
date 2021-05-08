package com.github.chantsune.swipetoaction.demo.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.chantsune.swipetoaction.demo.databinding.ViewListItemBinding

abstract class MainListAdapter(private val items: List<String>) :
    RecyclerView.Adapter<MainListAdapter.VH>() {
    class VH(val binding: ViewListItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding =
            ViewListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.root.setOnClickListener {
            onItemClick(item, position)
        }
        holder.binding.text.text = item
    }

    override fun getItemCount(): Int = items.size

    abstract fun onItemClick(item: String, position: Int)
}
