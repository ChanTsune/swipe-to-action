package com.github.chantsune.swipetoaction.extensions

import android.view.View
import android.widget.LinearLayout

var View.viewWeight: Float
    get() = (layoutParams as LinearLayout.LayoutParams).weight
    set(value) {
        layoutParams = (layoutParams as LinearLayout.LayoutParams).apply {
            weight = value
        }
    }

object Utils {
    @JvmStatic
    fun setViewWidth(view: View, width: Int) {
        view.layoutParams.width = width
        view.requestLayout()
    }

    fun setViewHeight(view: View, height: Int) {
        view.layoutParams.height = height
        view.requestLayout()
    }
}
