package com.github.chantsune.swipetoaction.demo

import android.view.LayoutInflater
import android.view.View
import android.view.View.OnLongClickListener
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.chantsune.swipetoaction.demo.databinding.RecyclerViewItemBinding
import com.github.chantsune.swipetoaction.demo.databinding.SampleItemBinding
import com.github.chantsune.swipetoaction.views.SwipeLayout
import com.github.chantsune.swipetoaction.views.SwipeLayout.OnSwipeItemClickListener
import java.util.*

class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {
    private val strings = ArrayList<String>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v =
            RecyclerViewItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.contentBinding.recyclerItemTv.text = "Item # ${strings[position]}"
        (holder.itemView as SwipeLayout).setItemState(SwipeLayout.ITEM_STATE_COLLAPSED, false)
    }

    override fun getItemCount(): Int = strings.size

    inner class ViewHolder(val binding: RecyclerViewItemBinding) :
        RecyclerView.ViewHolder(binding.root),
        View.OnClickListener, OnLongClickListener, OnSwipeItemClickListener {
        val contentBinding = SampleItemBinding.bind(binding.swipeLayout.contentView!!)
        override fun onClick(view: View) {
            Toast.makeText(
                view.context,
                "Clicked at " + strings[adapterPosition],
                Toast.LENGTH_SHORT
            ).show()
        }

        override fun onLongClick(view: View): Boolean {
            Toast.makeText(
                view.context,
                "Long Clicked at " + strings[adapterPosition],
                Toast.LENGTH_SHORT
            ).show()
            return true
        }

        override fun onSwipeItemClick(left: Boolean, index: Int) {
            if (left) {
                binding.swipeLayout.also { swipeLayout ->
                    if (swipeLayout.isEnabledAtIndex(true, 0)) {
                        swipeLayout.setAlphaAtIndex(true, 0, 0.5f)
                        swipeLayout.setEnableAtIndex(true, 0, false)
                    } else {
                        swipeLayout.setAlphaAtIndex(true, 0, 1f)
                        swipeLayout.setEnableAtIndex(true, 0, true)
                    }
                }
            } else {
                when (index) {
                    0 -> {
                        Toast.makeText(itemView.context, "Reload", Toast.LENGTH_SHORT).show()
                    }
                    1 -> {
                        Toast.makeText(itemView.context, "Settings", Toast.LENGTH_SHORT).show()
                    }
                    2 -> {
                        val pos = adapterPosition
                        strings.removeAt(pos)
                        notifyItemRemoved(pos)
                        Toast.makeText(itemView.context, "Trash", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        init {
            binding.swipeLayout.setOnClickListener(this)
            binding.swipeLayout.setOnLongClickListener(this)
            binding.swipeLayout.setOnSwipeItemClickListener(this)
        }
    }

    init {
        for (i in 0..29) {
            strings.add(i.toString() + "")
        }
    }
}