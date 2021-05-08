package com.github.chantsune.swipetoaction.demo.custom

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.chantsune.swipetoaction.demo.R
import com.github.chantsune.swipetoaction.demo.databinding.ViewCustomSwipeLayoutItemBinding
import com.github.chantsune.swipetoaction.views.SwipeLayout
import com.github.chantsune.swipetoaction.views.SwipeLayout.OnSwipeItemClickListener

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private val strings = MutableList(30) { it.toString() }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            ViewCustomSwipeLayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.recyclerItemTv.text = "Item # ${strings[position]}"
        holder.binding.swipeLayout.setItemState(SwipeLayout.ITEM_STATE_COLLAPSED, false)
        val item = holder.binding.swipeLayout.newSwipeItem(false).apply {
            setCustomView(R.layout.right_swipe_item)
        }
        holder.binding.swipeLayout.addSwipeItem(item)
    }

    override fun getItemCount(): Int = strings.size

    inner class ViewHolder(val binding: ViewCustomSwipeLayoutItemBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener, OnLongClickListener, OnSwipeItemClickListener {

        init {
            binding.swipeLayout.setOnClickListener(this)
            binding.swipeLayout.setOnLongClickListener(this)
            binding.swipeLayout.setOnSwipeItemClickListener(this)
        }

        override fun onClick(view: View) {
            Toast.makeText(
                view.context,
                "Clicked at ${strings[absoluteAdapterPosition]}",
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onLongClick(view: View): Boolean {
            Toast.makeText(
                view.context,
                "Long Clicked at ${strings[absoluteAdapterPosition]}",
                Toast.LENGTH_SHORT
            ).show()
            return true
        }

        override fun onSwipeItemClick(swipeItem: SwipeLayout.SwipeItem) {
            if (swipeItem.left) {
                if (swipeItem.view.isEnabled) {
                    swipeItem.view.alpha = 0.5f
                    swipeItem.view.isEnabled = false
                    binding.swipeLayout.collapseAll(true)
                } else {
                    swipeItem.view.alpha = 1f
                    swipeItem.view.isEnabled = true
                }
            } else {
                when (swipeItem.position) {
                    0 -> {
                        Toast.makeText(itemView.context, "Reload", Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        Toast.makeText(itemView.context, "Settings", Toast.LENGTH_SHORT).show()
                    }
                    2 -> {
                        val pos = absoluteAdapterPosition
                        strings.removeAt(pos)
                        notifyItemRemoved(pos)
                        Toast.makeText(itemView.context, "Trash", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
