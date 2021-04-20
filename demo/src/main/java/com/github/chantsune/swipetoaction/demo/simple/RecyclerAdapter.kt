package com.github.chantsune.swipetoaction.demo.simple

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.chantsune.swipetoaction.demo.databinding.ViewSimpleSwipeLayoutItemBinding
import com.github.chantsune.swipetoaction.views.SwipeLayout

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    class ViewHolder(val binding: ViewSimpleSwipeLayoutItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val strings = MutableList(30) { it.toString() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            ViewSimpleSwipeLayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.recyclerItemTv.text = "Item # ${strings[position]}"

        when (position % 3) {
            0 -> {
                holder.binding.swipeLayout.canFullSwipeLeftToRight = false
                holder.binding.swipeLayout.canFullSwipeRightToLeft = false
            }
            1 -> {
                holder.binding.swipeLayout.canFullSwipeLeftToRight = true
                holder.binding.swipeLayout.canFullSwipeRightToLeft = false
            }
            else -> {
                holder.binding.swipeLayout.canFullSwipeLeftToRight = false
                holder.binding.swipeLayout.canFullSwipeRightToLeft = true
            }
        }

        holder.binding.swipeLayout.setItemState(SwipeLayout.ITEM_STATE_COLLAPSED, false)
        holder.binding.swipeLayout.setOnClickListener { view ->
            Toast.makeText(
                view.context,
                "Clicked at ${strings[holder.adapterPosition]}",
                Toast.LENGTH_SHORT
            ).show()
        }
        holder.binding.swipeLayout.setOnLongClickListener { view ->
            Toast.makeText(
                view.context,
                "Long Clicked at ${strings[holder.adapterPosition]}",
                Toast.LENGTH_SHORT
            ).show()
            true
        }
        holder.binding.swipeLayout.setOnSwipeItemClickListener { view, left, index ->
            if (left) {
                holder.binding.swipeLayout.also { swipeLayout ->
                    if (swipeLayout.isEnabledAtIndex(true, index)) {
                        swipeLayout.setAlphaAtIndex(true, index, 0.5f)
                        swipeLayout.setEnableAtIndex(true, index, false)
                        swipeLayout.collapseAll(true)
                    } else {
                        swipeLayout.setAlphaAtIndex(true, index, 1f)
                        swipeLayout.setEnableAtIndex(true, index, true)
                    }
                }
            } else {
                when (index) {
                    0 -> {
                        Toast.makeText(holder.itemView.context, "Reload", Toast.LENGTH_SHORT)
                            .show()
                    }
                    1 -> {
                        Toast.makeText(holder.itemView.context, "Settings", Toast.LENGTH_SHORT)
                            .show()
                    }
                    2 -> {
                        val pos = holder.adapterPosition
                        strings.removeAt(pos)
                        notifyItemRemoved(pos)
                        Toast.makeText(holder.itemView.context, "Trash", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = strings.size
}
