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

var View.viewWidth: Int
    get() = layoutParams.width
    set(value) {
        layoutParams.width = value
        requestLayout()
    }

var View.viewHeight: Int
    get() = layoutParams.height
    set(value) {
        layoutParams.height = value
        requestLayout()
    }

object Utils {
    @JvmStatic
    fun setViewWidth(view: View, width: Int) {
        view.layoutParams.width = width
        view.requestLayout()
    }
}
