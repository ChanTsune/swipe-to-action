package com.github.chantsune.swipetoaction.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.chantsune.swipetoaction.views.SwipeLayout

abstract class SwipeToActionAdapter: RecyclerView.Adapter<SwipeToActionViewHolder>() {
    private val overSwipeEnabled = true
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SwipeToActionViewHolder {
        val lp = SwipeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val swipeLayout = SwipeLayout(parent.context).apply {
            layoutParams = lp
        }
        val rightItem = onCreateRightSwipeItemView(swipeLayout).apply {
            layoutParams = SwipeLayout.LayoutParams(layoutParams).also { params ->
                params.gravity = SwipeLayout.LayoutParams.GRAVITY_RIGHT
                params.sticky = SwipeLayout.LayoutParams.STICKY_SELF
                params.clamp = if (overSwipeEnabled) SwipeLayout.LayoutParams.CLAMP_PARENT else SwipeLayout.LayoutParams.CLAMP_SELF
            }
        }
        val leftItem = onCreateLeftSwipeItemView(swipeLayout).apply {
            layoutParams = SwipeLayout.LayoutParams(layoutParams).also { params ->
                params.gravity = SwipeLayout.LayoutParams.GRAVITY_LEFT
                params.sticky = SwipeLayout.LayoutParams.STICKY_SELF
                params.clamp = if (overSwipeEnabled) SwipeLayout.LayoutParams.CLAMP_PARENT else SwipeLayout.LayoutParams.CLAMP_SELF
            }
        }
        val centerView = onCreateItemView(swipeLayout, viewType)
        swipeLayout.centerView = centerView
        swipeLayout.leftView = leftItem
        swipeLayout.rightView = rightItem
        swipeLayout.addView(centerView)
        swipeLayout.addView(leftItem)
        swipeLayout.addView(rightItem)

        return SwipeToActionViewHolder(swipeLayout)
    }

    override fun onBindViewHolder(holder: SwipeToActionViewHolder, position: Int) {
        holder.centerView?.let {

        }
        (holder.itemView as? SwipeLayout)?.setOnSwipeListener(object : SwipeLayout.OnSwipeListener {
            override fun onBeginSwipe(swipeLayout: SwipeLayout, moveToRight: Boolean) {
                this@SwipeToActionAdapter.onBeginSwipe(swipeLayout, moveToRight, position)
            }

            override fun onSwipeClampReached(swipeLayout: SwipeLayout, moveToRight: Boolean) {
                this@SwipeToActionAdapter.onSwipeClampReached(swipeLayout, moveToRight, position)
            }

            override fun onLeftStickyEdge(swipeLayout: SwipeLayout, moveToRight: Boolean) {
                this@SwipeToActionAdapter.onLeftStickyEdge(swipeLayout,moveToRight, position)
            }

            override fun onRightStickyEdge(swipeLayout: SwipeLayout, moveToRight: Boolean) {
                this@SwipeToActionAdapter.onRightStickyEdge(swipeLayout, moveToRight, position)
            }
        })
        onBindItemView(holder, position)
    }
    abstract fun onCreateItemView(parent: ViewGroup, viewType: Int): View
    abstract fun onCreateRightSwipeItemView(parent: ViewGroup): View
    abstract fun onCreateLeftSwipeItemView(parent: ViewGroup): View

    abstract fun onBindItemView(holder: SwipeToActionViewHolder, position: Int)

    abstract fun onBeginSwipe(swipeLayout: SwipeLayout, moveToRight: Boolean, position: Int)
    abstract fun onSwipeClampReached(swipeLayout: SwipeLayout, moveToRight: Boolean, position: Int)
    abstract fun onLeftStickyEdge(swipeLayout: SwipeLayout, moveToRight: Boolean, position: Int)
    abstract fun onRightStickyEdge(swipeLayout: SwipeLayout, moveToRight: Boolean, position: Int)
}
