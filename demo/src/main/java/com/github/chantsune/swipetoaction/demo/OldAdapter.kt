package com.github.chantsune.swipetoaction.demo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.github.chantsune.swipetoaction.views.SwipeLayout
import com.github.chantsune.swipetoaction.views.SwipeLayout.OnSwipeListener

internal class OldAdapter : RecyclerView.Adapter<OldAdapter.ViewHolder>() {
    private val COUNT = 30
    private val itemsOffset = IntArray(COUNT)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutId = R.layout.view_swipe_list_item
        val itemView = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        val viewHolder = ViewHolder(itemView)
        val onClick = View.OnClickListener { viewHolder.swipeLayout.animateReset() }
        if (viewHolder.leftView != null) {
            viewHolder.leftView.isClickable = true
            viewHolder.leftView.setOnClickListener(onClick)
        }
        if (viewHolder.rightView != null) {
            viewHolder.rightView.isClickable = true
            viewHolder.rightView.setOnClickListener(onClick)
        }
        viewHolder.swipeLayout.setOnSwipeListener(object : OnSwipeListener {
            override fun onBeginSwipe(swipeLayout: SwipeLayout, moveToRight: Boolean) {}
            override fun onSwipeClampReached(swipeLayout: SwipeLayout, moveToRight: Boolean) {
                Toast.makeText(
                    swipeLayout.context,
                    (if (moveToRight) "Left" else "Right") + " clamp reached",
                    Toast.LENGTH_SHORT
                )
                    .show()
                viewHolder.textViewPos.text = "TADA!"
            }

            override fun onLeftStickyEdge(swipeLayout: SwipeLayout, moveToRight: Boolean) {}
            override fun onRightStickyEdge(swipeLayout: SwipeLayout, moveToRight: Boolean) {}
        })
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textViewPos.text = "#${position + 1}"
        holder.swipeLayout.offset = itemsOffset[position]
    }

    override fun onViewDetachedFromWindow(holder: ViewHolder) {
        if (holder.adapterPosition != RecyclerView.NO_POSITION) {
            itemsOffset[holder.adapterPosition] = holder.swipeLayout.offset
        }
    }

    override fun getItemCount(): Int {
        return COUNT
    }

    internal class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewPos: TextView = itemView.findViewById(R.id.text_view_pos)
        val swipeLayout: SwipeLayout = itemView.findViewById(R.id.swipe_layout)
        val rightView: View? = itemView.findViewById(R.id.right_view)
        val leftView: View? = itemView.findViewById(R.id.left_view)
    }
}