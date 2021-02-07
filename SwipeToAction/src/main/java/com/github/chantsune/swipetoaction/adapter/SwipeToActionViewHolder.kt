package com.github.chantsune.swipetoaction.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.github.chantsune.swipetoaction.views.SwipeLayout

open class SwipeToActionViewHolder(private val view: SwipeLayout) : RecyclerView.ViewHolder(view) {
    val centerView: View? get() = view.centerView
    val rightView: View? get() = view.rightView
    val leftView: View? get() = view.leftView
}
